package com.example.apitest.Service;

import com.example.apitest.Dao.DamageImage;
import com.example.apitest.utils.EnvironmentPath;
import com.example.apitest.Dao.Model;
import com.example.apitest.Dao.ReturnMessage;
import com.example.apitest.mapper.DamageImageMapper;
import com.example.apitest.mapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @Author 宋宗垚
 * @Date 2019/7/20 22:24
 * @Description 用于重新训练模型的Service
 *          // 本功能的执行业务流程为
 *         // 1、从数据库中找出有损伤的图片，以及其对应的损伤信息，（需要考虑的是如果量过大，内存不足的问题）
 *         // 2、如果需要，将数据从raid中取出来，放到本地。如果不需要就保持图片原始位置。
 *         // 3、根据1中的信息，和2中的存放路径。生成XML损伤信息（需要指明生成目录）
 *         // 4、根据XML文件，对图片进行Sample以及coco形式的数据集的生成。（需要指明目录）
 *         // 5、对数据进行拆分train valid test（需要指明目录）及其分别的路径txt
 *         // 6、进行训练。评价训练结果
 *         // 7、根据训练结果，更新模型文件。（这里要不要入库，要不要存历史，有待商榷）
 */
@Service
public class RetrainModelService {
//    private static volatile RetrainModelService instance;
    public trainingThread trainThread;
    public static volatile int epoch;
    public trainingThread getTrainThread() {
        return trainThread;
    }

    public void setTrainThread(trainingThread trainThread) {
        this.trainThread = trainThread;
    }

    @Autowired
    private DamageImageMapper damageImageMapper;
    @Autowired
    private ModelMapper modelMapper;

    public static volatile boolean isTraining;
    private String pythonEnviromentPath;// = "H:\\Anaconda\\Anaconda3\\envs\\welddetection\\python.exe";
    private String xmlToTxtPythonFilePath;
    private String bestModeSavePath;
    private String retrainPythonFilePath;
    private String retrainProjectFilePath;


    private Logger logger = LoggerFactory.getLogger(getClass());


    public RetrainModelService(){
        this.pythonEnviromentPath = EnvironmentPath.getInstance().getPythonExEPath();
        this.xmlToTxtPythonFilePath = EnvironmentPath.getInstance().getXmlToTxtPythonFilePath();
        this.bestModeSavePath = EnvironmentPath.getInstance().getBestModeSavePath();
        this.retrainProjectFilePath = EnvironmentPath.getInstance().getRetrainProjectFilePath();
        this.retrainPythonFilePath = EnvironmentPath.getInstance().getRetrainPythonFilePath();

//        isTraining = false;
    }

//    public static RetrainModelService getInstance() {
//        if (instance==null){
//            synchronized (RetrainModelService.class){
//                if (instance == null){
//                    instance = new RetrainModelService();
//                }
//            }
//        }
//        return instance;
//    }



    // 本功能的执行业务流程为
    // 1、从数据库中找出有损伤的图片，以及其对应的损伤信息，（需要考虑的是如果量过大，内存不足的问题）
    // 2、如果需要，将数据从raid中取出来，放到本地。如果不需要就保持图片原始位置。
    // 3、根据1中的信息，和2中的存放路径。生成XML损伤信息（需要指明生成目录）
    // 4、根据XML文件，对图片进行Sample以及coco形式的数据集的生成。（需要指明目录）
    // 5、对数据进行拆分train valid test（需要指明目录）及其分别的路径txt
    // 6、进行训练。评价训练结果
    // 7、根据训练结果，更新模型文件.
    // TODO：8、将训练好的模型移动到存放模型的文件夹下，然后把这个模型的路径和mAP存到数据库中

    /**
     * 默认图片在本地的请款下，进行的重新训练的整体流程
     * @return
     */
    public synchronized ReturnMessage retrain(String xmlFolderPath, String txtFolderPath){
        setTraining(true);
        Model model = null;
        try {
            File xmlFolderFile = new File(xmlFolderPath);
            deleteFile(xmlFolderFile);
            if (!xmlFolderFile.exists()) {
                xmlFolderFile.mkdir();
            }

            List<Integer> idList = damageImageMapper.findAllDamageImageId();
            for (Integer id : idList) {
                DamageImage damageImage = damageImageMapper.findDamageImageById(id);
                if (damageImage==null ){
                    continue;
                }
                try {
                    generateXML(damageImage, xmlFolderPath);
                } catch (IOException e) {
                    logger.error("---生成xml过程出现异常---");
                    logger.error(e.getMessage());
                    setTraining(false);
                    return new ReturnMessage(false, "生成xml过程出现异常", e);
                }

            }
            logger.info("---成功生成xml---");
            try {
                xmlToTXT(xmlFolderPath, txtFolderPath);
            } catch (IOException e) {
                logger.error("---xml转化为txt过程出现异常---");
                logger.error(e.getMessage());
                setTraining(false);
                return new ReturnMessage(false, "xml转化为txt过程出现异常", e);
            }
            logger.info("---xml转化为txt成功---");
            try {
                splitDataToTrainAndTest(txtFolderPath);
            } catch (Exception e) {
                logger.error("---数据拆分过程出现异常---");
                logger.error(e.getMessage());
                setTraining(false);
                return new ReturnMessage(false, "数据拆分过程出现异常", e);
            }
            logger.error("---数据拆分过程成功---");

            try {
                model = trainModel(txtFolderPath);
            } catch (Exception e) {
                logger.error("---重新训练过程出现异常---");
                logger.error(e.getMessage());
                setTraining(false);
                return new ReturnMessage(false, "", e);
            }
            logger.info("---重新训练过程成功---");

            Double best = modelMapper.getBestMAP();
            if (model.getmAP() > best) {
                String path = "";
                try {
                    path = saveModelToFolder(model, bestModeSavePath);
                } catch (IOException e) {
                    logger.error("---模型复制过程出现异常---");
                    logger.error(e.getMessage());
                    setTraining(false);
                    return new ReturnMessage(false, "", e);
                }
                model.setModelPath(path);
                modelMapper.saveModel(model);
            }
        }catch (Exception e){
            setTraining(false);
        }
        setTraining(false);
        return new ReturnMessage(true,"",model);
    }


    /**
     * 将模型存储到指定的文件夹下
     * @param model
     * @param folder
     */
    public String saveModelToFolder(Model model,String folder) throws IOException {
        File folderFile = new File(folder);
        if (!folderFile.isDirectory()){
            // mkdir 只能创建一个文件夹，但是mkdirs可以在父文件夹不在的情况下连同父文件夹一起创建
            folderFile.mkdirs();
        }
        String sourceFilePath = model.getModelPath();
        File sourceFile = new File(sourceFilePath);
        String sourceFileName = sourceFile.getName();
        int index = sourceFileName.indexOf(".");
        String nameWithOutType = sourceFileName.substring(0,index);
        String type = sourceFileName.substring(index);
        nameWithOutType = nameWithOutType +"_"+ model.getDateString();
        String targetFilePath = folder + File.separator + nameWithOutType + type;
        File targetFile = new File(targetFilePath);
        copyFileUsingFileChannels(sourceFile,targetFile);

//        String targetFilePath = folder + File.separator +
//        File targetFile = new File()
        return targetFilePath;
    }

    private void copyFileUsingFileChannels(File source,File dest) throws IOException {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try{
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel,0,inputChannel.size());
        }finally{
            inputChannel.close();
            outputChannel.close();
        }
    }




    /**
     * 获取到损伤图片的数组的函数，为了防止数量过多内存过大。可以设置offset以及limitNumber
     * @param offset 偏移量
     * @param limitNumber 每次限制的数量
     * @return 损伤图片的数组
     */
    public List<DamageImage> getDamageImageList(int offset,int limitNumber){
        return new ArrayList<DamageImage>();
    }

    public DamageImage getDamageImageById(Integer id){
        return damageImageMapper.findDamageImageById(id);
    }


    /**
     * 将一个文件从sourcePath复制到targetPath
     * @param sourcePath 目标文件的源路径
     * @param targetPath 文件要复制到的目标路径
     * @return 复制是否成功
     */
    public boolean copyFile(String sourcePath,String targetPath){
        return true;
    }

    /**
     * 根据damageImageList，生成对应的xml标注信息。xml文件的格式暂定为如
     * 'H:\\LabelProject\\20190516\\dataset_stage1\\augtif\\outputs'
     * 中的一样
     * @param damageImage 损伤图片信息
     * @return 生成的xml文件的全路径
     */
    public void generateXML(DamageImage damageImage,String targetFolder) throws IOException {
        damageImage.generateXMLFile(targetFolder);
    }

    /**
     * 根据xml文件生成相对应的coco格式的
     * @param sourceFolder
     * @param targetFolderPath
     * @return
     */
    public boolean xmlToTXT(String sourceFolder,String targetFolderPath) throws IOException {
        // 先检验需检测的图片是否存在
        boolean result = true;
        File sourceFile = new File(sourceFolder);
        if(!sourceFile.exists()){
            // 如果文件不存在
            sourceFile.mkdir();
//            throw new FileNotFoundException();
//            return message;
        }
        File targetFile = new File(targetFolderPath);
        if (!targetFile.exists()){
            targetFile.mkdir();
//            throw new FileNotFoundException();
        }
        // 清空目标文件夹，然后建立新的文件夹
        deleteFile(targetFile);
        if (targetFile.mkdir()){
            File imagesFolder = new File(targetFolderPath+File.separator+"images");
            File labelsFolder = new File(targetFolderPath+File.separator+"labels");
            File masksFolder = new File(targetFolderPath+File.separator+"masks");
            System.out.println("make dirs");
            imagesFolder.mkdir();
            labelsFolder.mkdir();

            masksFolder.mkdir();
        }
//D:\Anaconda\envs\WeldDetection_old\python.exe H:\LabelProject\20190516\dataset_stage1\augtif\retrain\xml_to_txt.py --xml_folder_path=D:\ProjectCode\retrain_data\xml --target_path=D:\ProjectCode\retrain_data\txt
// H:\Anaconda\Anaconda3\envs\welddetection\python.exe H:\LabelProject\20190516\dataset_stage1\augtif\retrain\xml_to_txt.py --xml_folder_path=H:\LabelProject\20190516\dataset_stage1\augtif\retrainTestData\xml --target_path=H:\LabelProject\20190516\dataset_stage1\augtif\retrainTestData\sampleTest
        String args = pythonEnviromentPath+" "+xmlToTxtPythonFilePath
                +" --xml_folder_path="+sourceFile+" --target_path="+targetFolderPath;
        Runtime mt = Runtime.getRuntime();

        Process pr = mt.exec(args);
        BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
        String line;
        while ((line=in.readLine())!=null){
            System.out.println(line);
        }
//        } catch (IOException e) {
////            e.printStackTrace();
//            logger.error("xmlToTXT 执行cmd命令时出错");
//            logger.error(e.getMessage());
//            result = false;
//        }

        logger.info("xmlToTXT 执行成功");
        return result;
    }

    /**
     * 将xmlToTXT函数中生成好的数据进行分割，生成rain以及test数据
     * @param folderPath 存储images labels 以及mask 的文件夹路径
     */
    public void splitDataToTrainAndTest(String folderPath) throws Exception {
        String imagesFolderPath = folderPath+File.separator+"images";
        String labelsFolderPath =  folderPath+File.separator+"labels";
        File imagesFolder = new File(imagesFolderPath);
        File labelsFolder =new File(labelsFolderPath);
        if (!(imagesFolder.exists() && labelsFolder.exists())){
//            logger.error("images folder or labels folder not found");
            throw new Exception("images folder or labels folder not found");
        }
        // 或得到images文件夹下所有文件的名称
        String[] names = imagesFolder.list();
        if (names==null || names.length==0){
//            logger.error("images folder is empty");
            throw new Exception("images folder is empty");
        }
        // 将名称顺序打乱
        List<String> namesList = Arrays.asList(names);
        Collections.shuffle(namesList);
        // 根据8:2 的比例，对数据进行分割，分为训练集和测试集
        int trainNumber = (int) (namesList.size() * 0.8);
        List<String> trainList = namesList.subList(0,trainNumber);
        List<String> testList = namesList.subList(trainNumber,namesList.size());
        // 将分割好的结果分别写到train.txt 以及test.txt中
        String trainTXTPath = folderPath+File.separator+"train.txt";
        String testTXTPath = folderPath+File.separator+"test.txt";
        // 写入train.txt
        File trainTXTFile = new File(trainTXTPath);
        if (!trainTXTFile.exists()){
            trainTXTFile.createNewFile();
        }
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(trainTXTFile));
        for (String name : trainList){
            String data = imagesFolderPath+File.separator+name;
            bufferedWriter.write(data);
            bufferedWriter.newLine();
        }
        bufferedWriter.close();
        // 写入text.txt
        File testTXTFile = new File(testTXTPath);
        if (!testTXTFile.exists()){
            testTXTFile.createNewFile();
        }
        bufferedWriter = new BufferedWriter(new FileWriter(testTXTFile));
        for (String name : testList){
            String data = imagesFolderPath+File.separator+name;
            bufferedWriter.write(data);
            bufferedWriter.newLine();
        }
        bufferedWriter.close();
    }


    /**
     * 通过之前制作好的数据，对模型进行重新训练
     * 需要告知函数，数据文件夹的位置
     */
    public Model trainModel(String folderPath) throws Exception {
        String trainTXTPath = folderPath+File.separator+"train.txt";
        String testTXTPath = folderPath+File.separator+"test.txt";
        File trainTXTFile = new File(trainTXTPath);
        File testTXTFile = new File(testTXTPath);
        if (!(trainTXTFile.exists() && testTXTFile.exists())){
            throw new Exception("train.txt or test.txt not exists");
        }
        //如果都存在的话，执行py脚本。
        // D:\Anaconda\envs\WeldDetection_old\python.exe D:\ProjectCode\retrain\retrain\rerain.py  --project_path=D:\ProjectCode\retrain\retrain --train_txt_path=D:\ProjectCode\retrain_data\txt\train.txt --test_txt_path=D:\ProjectCode\retrain_data\txt\test.txt
        String args = pythonEnviromentPath+" "+retrainPythonFilePath+" "+
                " --project_path="+retrainProjectFilePath+
                " --train_txt_path="+trainTXTPath+
                " --test_txt_path="+testTXTPath;
        Runtime mt = Runtime.getRuntime();
//        H:\Anaconda\Anaconda3\envs\welddetection\python.exe
// --project_path=H:\LabelProject\20190516\dataset_stage1\augtif\retrain
// --train_txt_path=H:\LabelProject\20190516\dataset_stage1\augtif\retrainTestData\sampleTest\train.txt
// --test_txt_path=H:\LabelProject\20190516\dataset_stage1\augtif\retrainTestData\sampleTest\test.txt
        double mAP = 0;
        String modelPth = "";
        Process pr = mt.exec(args);
        BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
        String line;
        while ((line=in.readLine())!=null){
            if (line.startsWith("train end max mAP")){
                String[] splite =line.split("@@");
                mAP = Double.valueOf(splite[1]);
            }
            if (line.startsWith("train end model path")){
                String[] splite =line.split("@@");
                modelPth = splite[1].trim();
            }
            System.out.println(line);
        }
        return new Model(modelPth,mAP);

    }

    /**
     * 查找数据库中有多少图片是有损伤的
     * @return 有损伤的图片的数量
     */
    public Integer getDamageImageNumber(){
        return damageImageMapper.countDamageImage();
    }




    private  boolean deleteFile(File dirFile) {
        // 如果dir对应的文件不存在，则退出
        if (!dirFile.exists()) {
            return false;
        }

        if (dirFile.isFile()) {
            return dirFile.delete();
        } else {

            for (File file : dirFile.listFiles()) {
                deleteFile(file);
            }
        }

        return dirFile.delete();
    }

    public boolean isTraining() {
        return isTraining;
    }

    public void setTraining(boolean training){
        isTraining = training;
    }
    public void setTrainingF(boolean training , int type) {
        if(type == 0){
            //开始线程
            trainThread = new trainingThread();
            trainThread.start();
        }
        else{
            trainThread.stop();
        }
        isTraining = training;
    }

    public String testPr()  {
        String args = "E:/Anaconda/envs/lpf/python.exe D:/FlawSegmentation/PSPNet/test_interface.py";
        Process pr = null;
        String a = "";
        try {
            pr = Runtime.getRuntime().exec(args);
//            String s = pr.getClass("handle",java.lang.ProcessImpl.class);
//            pr.forClass("handle");
//            a = String.valueOf(pr.getClass());
//            a = pr.toString();
//
//            pr.
        } catch (IOException e) {
            e.printStackTrace();
        }

        return a;
    }

    //重训练方法
    public Process retrainNew(String imgsPath,String imgsMask){
        String pythonExE = "E:/Anaconda/envs/lpf/python.exe";
        String action ="D:/FlawSegmentation/PSPNet/retrain.py";
        String args = pythonExE+" "+action+" "+imgsPath+" "+imgsMask;
        Process pr = null;
        System.out.println(args);
//        String args = "E:/Anaconda/envs/lpf/python.exe D:/FlawSegmentation/PSPNet/test_interface.py";
        try {
//            System.out.println("test");
                pr = Runtime.getRuntime().exec(args);
//            String a = pr.getClass().getName();
//            String s =java.lang.Runtime.getRuntime().getClass().getName();
//            long pid = -1;
//            String s = pr.javaClass.name;
//            Class<?> clazz = Class.forName("java.lang.ProcessImpl");
//            Field filed = clazz.getDeclaredField("pid");
//            filed.setAccessible(true);
//            pid = (Integer)filed.get(p)
//            pr.exitValue();
//            String pidAndName = pr.javaClass.get;
//            InputStreamReader ir = new InputStreamReader(pr.getInputStream());
//            LineNumberReader in = new LineNumberReader(ir);
//            String line;
////            System.out.println(in.readLine());
//            while ((line = in.readLine()) != null) {
//                System.out.println(line);
//            }
//
        }catch (IOException e) {
            System.out.println("重训练失败");
        }
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
        return pr;
    }
    //检测当前训练轮数
    public int checkEpoch(String path){
        File file = new File(path);
        StringBuilder result = new StringBuilder();
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
            String s = null;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
                result.append(s);
            }
            br.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        String s = result.toString();
//        System.out.println(s);
        return Integer.valueOf(s);
    }
}
