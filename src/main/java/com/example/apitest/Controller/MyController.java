package com.example.apitest.Controller;


import com.alibaba.fastjson.JSONObject;
import com.example.apitest.Dao.*;
import com.example.apitest.Entity.ResultFromDetection;
import com.example.apitest.Service.ExecuteAlgorithmService;
import com.example.apitest.Service.MyService;
import com.example.apitest.Service.RetrainModelService;
import com.example.apitest.mapper.DamageImageMapper;
import com.example.apitest.mapper.ModelMapper;
import com.example.apitest.utils.EnvironmentPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.io.File;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
/**
 * @Author 宋宗垚
 * @Date 2019/1/17 9:50
 * @Description
 * TODO:
 * 1、日志的加入
 * 2、切片的加入
 */


@RestController
public class MyController {

//    @Value("${myconfig.uploadFolderPath}")
//    private String uploadFolderPath;

    @Autowired
    private ExecuteAlgorithmService executeAlgorithmService;
    @Autowired
    private ModelMapper modelMapper;//没用上。。。
    @Autowired
    private DamageImageMapper damageImageMapper;//下面detectbyid用到了，针对图片
    @Autowired
    private RetrainModelService retrainModelService;
    @Autowired
    private MyService myService;
    private Logger logger = LoggerFactory.getLogger(getClass());
//    @GetMapping("/checkUploadPath")
//    @ResponseBody
//    public String checkUploadPath() {
//
//        return uploadFolderPath;
//    }

    @GetMapping("/test")
    public String  Test(){
//        String xmlFolderPath = "H:\\LabelProject\\20190516\\dataset_stage1\\augtif\\retrainTestData\\xmlTest";
//        String txtFolderPath = "H:\\LabelProject\\20190516\\dataset_stage1\\augtif\\retrainTestData\\ttt1";
//        ReturnMessage returnMessage = retrainModelService.retrain(xmlFolderPath,txtFolderPath);
//        logger.info(returnMessage.isSuccess()+"");
//        logger.info(((Model)returnMessage.getData()).getModelPath()+"");
//        RetrainModelService retrainModelService = RetrainModelService.getInstance();
//        Model model = new Model("H:\\LabelProject\\20190516\\dataset_stage1\\augtif\\retrain\\checkpoints\\yolov3_ckpt_0.pth",10.0);
//        retrainModelService.saveModelToFolder(model,"H:\\LabelProject\\code\\PyTorch-YOLOv3-master\\checkpoints");
//        Model model = new Model("pathTest",10.2);
//        modelMapper.saveModel(model);
       for (int i =0;i<10;i++){
           myService.testAsync1();
        }
//        for (int i=0;i<10;i++){
//            Thread t = new Thread(()->{
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                System.out.println(Thread.currentThread().getName()+"-----");
//            });
//            t.start();
//        }

      return "qqweq";
   }
//    @GetMapping("/test")
 //   public String  Test(){
 //       return"success";
 //   }

    /**
     * 重新训练模型的接口
     * @return ReturnMessage类型的执行结果
     */
    @PostMapping("/retrain")
    public ReturnMessage retrainModel(){
        // TODO: 所有的xml路径，txt路径，python环境路径、检测文件的文件路径、等路径均没有配置
        // 本功能的执行业务流程为
        // 1、从数据库中找出有损伤的图片，以及其对应的损伤信息，（需要考虑的是如果量过大，内存不足的问题）
        // 2、如果需要，将数据从raid中取出来，放到本地。如果不需要就保持图片原始位置。
        // 3、根据1中的信息，和2中的存放路径。生成XML损伤信息（需要指明生成目录）       完成
        // 4、根据XML文件，对图片进行Sample以及coco形式的数据集的生成。（需要指明目录）
        // 5、对数据进行拆分train valid test（需要指明目录）及其分别的路径txt
        // 6、进行训练。评价训练结果
        // 7、根据训练结果，更新模型文件。（这里要不要入库，要不要存历史，有待商榷）
        // 8、将训练好的模型移动到存放模型的文件夹下，然后把这个模型的路径和mAP存到数据库中
//        RetrainModelService retrainModelService = RetrainModelService.getInstance();
        if (retrainModelService.isTraining()){
            return new ReturnMessage(false,"模型正在训练中，请稍后",null);
        }

        String xmlFolderPath = EnvironmentPath.getInstance().getXmlFolderPath();//"H:\\LabelProject\\20190516\\dataset_stage1\\augtif\\retrainTestData\\xmlTest";
        String txtFolderPath = EnvironmentPath.getInstance().getTxtFolderPath();//"H:\\LabelProject\\20190516\\dataset_stage1\\augtif\\retrainTestData\\ttt1";
        ReturnMessage returnMessage = retrainModelService.retrain(xmlFolderPath,txtFolderPath);
        logger.info(returnMessage.isSuccess()+"");
        logger.info(((Model)returnMessage.getData()).getModelPath()+"");

        return returnMessage;
    }

    /**
     * 检测模型是否正在训练
     * @return 如果是在训练，返回true；如果不在训练，返回falase
     */
    @PostMapping("/istraining")
    public boolean isTraining(){

        logger.info("istraining: "+RetrainModelService.isTraining);
        return RetrainModelService.isTraining;
    }




    @GetMapping("/detectbyid")
    @ResponseBody
    public DamageDetectMessage detectDamageByID(@RequestParam("id")Integer id){
        // 根据ID获取到数据库中这个图片的存储路径
//        DamageDetectMessage message = new DamageDetectMessage();
        // 如果ID不存在，则将消息写入message，然后直接返回
//        ExecuteAlgorithmService executeAlgorithmService = new ExecuteAlgorithmService();
        // TODO:需要等待raid配置完成。可以先用自己本地模拟一个mysql去做
        DamageImage damageImage =damageImageMapper.findDamageImageById(id);
        String path = "";
        if (damageImage.getTransferImagePath()==null || "".equals(damageImage.getTransferImagePath())){
            path = damageImage.getSourceImagePath();
        }else {
            path = damageImage.getTransferImagePath();
        }
        DamageDetectMessage message = executeAlgorithmService.excute(path);
        return message;

    }


    @PostMapping("/detectuploadfile")
    @ResponseBody
    public DamageDetectMessage upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        System.out.println("调用//upload接口中的参数");

        System.out.println(file.getSize());
        DamageDetectMessage message = new DamageDetectMessage();
        if (file.isEmpty() ) {
            // 如果文件是空文件，
            message.setStatus(false);
            message.setInfo("检测失败，上传文件非法");
            return message;

        }
        else if( file.getOriginalFilename().endsWith(".jpg"))
        {
            String OriginalFileName = file.getOriginalFilename();
            String saveFileName = executeAlgorithmService.getRandomFileName() + ".jpg";
            //
            String saveFolder = System.getProperty("user.dir")+"\\src\\main\\resources\\static\\upload\\";//"E:\\IDEA\\tools\\upload\\";
            //String saveFolder = "A:\\pics\\";
            File saveFile = new File( saveFolder + saveFileName);
            if (!saveFile.getParentFile().exists()) {
                saveFile.getParentFile().mkdirs();
            }
            try {
                System.out.println(saveFileName);
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(saveFile));
                out.write(file.getBytes());
                System.out.print(file.getBytes());
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
                message.setStatus(false);
                message.setInfo(e.getMessage());
                return message;
            }
            message = executeAlgorithmService.excute(saveFolder + saveFileName);
            System.out.println(message.getJsonData().toString());
            if (saveFile.exists() && saveFile.isFile()){
                if (saveFile.delete()) {
                    System.out.println("删除单个文件" + saveFileName + "成功！");
                } else {
                    System.out.println("删除单个文件" + saveFileName + "失败！");
                }
            }else {
                System.out.println("删除单个文件失败：" + saveFileName + "不存在！");
            }
            return message;
        }
        else
            {
            String OriginalFileName = file.getOriginalFilename();
            String saveFileName = executeAlgorithmService.getRandomFileName() + ".tif";
            //
            String saveFolder = System.getProperty("user.dir")+"\\src\\main\\resources\\static\\upload\\";//"E:\\IDEA\\tools\\upload\\";
            //String saveFolder = "A:\\pics\\";
            File saveFile = new File( saveFolder + saveFileName);
            if (!saveFile.getParentFile().exists()) {
                saveFile.getParentFile().mkdirs();
            }
            try {
                System.out.println(saveFileName);
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(saveFile));
                out.write(file.getBytes());
                System.out.print(file.getBytes());
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
                message.setStatus(false);
                message.setInfo(e.getMessage());
                return message;
            }
            message = executeAlgorithmService.excute(saveFolder + saveFileName);
            System.out.println(message.getJsonData().toString());
            if (saveFile.exists() && saveFile.isFile()){
                if (saveFile.delete()) {
                    System.out.println("删除单个文件" + saveFileName + "成功！");
                } else {
                    System.out.println("删除单个文件" + saveFileName + "失败！");
                }
            }else {
                System.out.println("删除单个文件失败：" + saveFileName + "不存在！");
            }
            return message;
        }
    }

    //修改：返回值与res类别
    @PostMapping("/detectuploadfile_v2")
    @ResponseBody
    public ResultFromDetection upload_v2(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        System.out.println("调用//upload接口中的参数");

        ResultFromDetection res = null;
//        String line = "";

        System.out.println(file.getSize());
        if (file.isEmpty() ) {
            // 如果文件是空文件，
//            message.setStatus(false);
//            message.setInfo("检测失败，上传文件非法");
//            return message;
            return null;

        }
        else if( file.getOriginalFilename().endsWith(".jpg"))
        {
            String OriginalFileName = file.getOriginalFilename();
            String saveFileName = executeAlgorithmService.getRandomFileName() + ".jpg";
            //
            String saveFolder = System.getProperty("user.dir")+"\\src\\main\\resources\\static\\upload\\";//"E:\\IDEA\\tools\\upload\\";
            //String saveFolder = "A:\\pics\\";
            File saveFile = new File( saveFolder + saveFileName);
            if (!saveFile.getParentFile().exists()) {
                saveFile.getParentFile().mkdirs();
            }
            try {
                System.out.println(saveFileName);
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(saveFile));
                out.write(file.getBytes());
                System.out.print(file.getBytes());
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
//                message.setStatus(false);
//                message.setInfo(e.getMessage());
//                return message;
                return res;
            }
            res = executeAlgorithmService.excute_v2(saveFolder + saveFileName);
//            System.out.println(message.getJsonData().toString());
//            if (saveFile.exists() && saveFile.isFile()){
//                if (saveFile.delete()) {
//                    System.out.println("删除单个文件" + saveFileName + "成功！");
//                } else {
//                    System.out.println("删除单个文件" + saveFileName + "失败！");
//                }
//            }else {
//                System.out.println("删除单个文件失败：" + saveFileName + "不存在！");
//            }
//            System.out.println("=========controller=============");
//            System.out.println(res);
            return res;
        }
        else if (file.getOriginalFilename().endsWith(".png"))
        {
            String OriginalFileName = file.getOriginalFilename();
            String saveFileName = executeAlgorithmService.getRandomFileName() + ".png";
            //
            String saveFolder = System.getProperty("user.dir")+"\\src\\main\\resources\\static\\upload\\";//"E:\\IDEA\\tools\\upload\\";
            //String saveFolder = "A:\\pics\\";
            File saveFile = new File( saveFolder + saveFileName);
            if (!saveFile.getParentFile().exists()) {
                saveFile.getParentFile().mkdirs();
            }
            try {
                System.out.println(saveFileName);
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(saveFile));
                out.write(file.getBytes());
                System.out.print(file.getBytes());
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
//                message.setStatus(false);
//                message.setInfo(e.getMessage());
//                return message;
                 return res;
            }
            res = executeAlgorithmService.excute_v2(saveFolder + saveFileName);
//            System.out.println("=========controller=============");
//            System.out.println(line);
//            System.out.println(message.getJsonData().toString());
//            if (saveFile.exists() && saveFile.isFile()){
//                if (saveFile.delete()) {
//                    System.out.println("删除单个文件" + saveFileName + "成功！");
//                } else {
//                    System.out.println("删除单个文件" + saveFileName + "失败！");
//                }
//            }else {
//                System.out.println("删除单个文件失败：" + saveFileName + "不存在！");
//            }
            return res;
        } else
        {
            String OriginalFileName = file.getOriginalFilename();
            String saveFileName = executeAlgorithmService.getRandomFileName() + ".tif";
            //
            String saveFolder = System.getProperty("user.dir")+"\\src\\main\\resources\\static\\upload\\";//"E:\\IDEA\\tools\\upload\\";
            //String saveFolder = "A:\\pics\\";
            File saveFile = new File( saveFolder + saveFileName);
            if (!saveFile.getParentFile().exists()) {
                saveFile.getParentFile().mkdirs();
            }
            try {
                System.out.println(saveFileName);
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(saveFile));
                out.write(file.getBytes());
                System.out.print(file.getBytes());
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
//                message.setStatus(false);
//                message.setInfo(e.getMessage());
//                return message;
                return res;
            }
            res = executeAlgorithmService.excute_v2(saveFolder + saveFileName);
//            System.out.println("=========controller=============");
//            System.out.println(line);
//            System.out.println(message.getJsonData().toString());
//            if (saveFile.exists() && saveFile.isFile()){
//                if (saveFile.delete()) {
//                    System.out.println("删除单个文件" + saveFileName + "成功！");
//                } else {
//                    System.out.println("删除单个文件" + saveFileName + "失败！");
//                }
//            }else {
//                System.out.println("删除单个文件失败：" + saveFileName + "不存在！");
//            }
            return res;
        }
    }

    @GetMapping("/fakedata")
    @ResponseBody
    public JSONObject fakeData() {
        String ss = "{\"damageDataList\":[{\"author\":\"Algorithm\",\"timestamp\":1551149845732,\"x\":121,\"y\":774},{\"author\":\"Algorithm\",\"timestamp\":1551149845732,\"x\":153,\"y\":774},{\"author\":\"Algorithm\",\"timestamp\":1551149845732,\"x\":121,\"y\":806},{\"author\":\"Algorithm\",\"timestamp\":1551149845732,\"x\":153,\"y\":806},{\"author\":\"Algorithm\",\"timestamp\":1551149845732,\"x\":185,\"y\":806},{\"author\":\"Algorithm\",\"timestamp\":1551149845732,\"x\":121,\"y\":806},{\"author\":\"Algorithm\",\"timestamp\":1551149845732,\"x\":153,\"y\":806},{\"author\":\"Algorithm\",\"timestamp\":1551149845733,\"x\":185,\"y\":806},{\"author\":\"Algorithm\",\"timestamp\":1551149845733,\"x\":121,\"y\":838},{\"author\":\"Algorithm\",\"timestamp\":1551149845733,\"x\":153,\"y\":838},{\"author\":\"Algorithm\",\"timestamp\":1551149845733,\"x\":185,\"y\":838},{\"author\":\"Algorithm\",\"timestamp\":1551149845733,\"x\":121,\"y\":870},{\"author\":\"Algorithm\",\"timestamp\":1551149845733,\"x\":153,\"y\":870},{\"author\":\"Algorithm\",\"timestamp\":1551149845733,\"x\":185,\"y\":806},{\"author\":\"Algorithm\",\"timestamp\":1551149845733,\"x\":185,\"y\":838},{\"author\":\"Algorithm\",\"timestamp\":1551149845733,\"x\":121,\"y\":1126},{\"author\":\"Algorithm\",\"timestamp\":1551149845733,\"x\":121,\"y\":1158},{\"author\":\"Algorithm\",\"timestamp\":1551149845733,\"x\":121,\"y\":1190},{\"author\":\"Algorithm\",\"timestamp\":1551149845733,\"x\":121,\"y\":1190},{\"author\":\"Algorithm\",\"timestamp\":1551149845733,\"x\":121,\"y\":1222}],\"timestamp\":\"2019-02-26 10:54:26.797\",\"status\":\"SUCCESS\",\"info\":\"nothing\"}\n" ;
        return JSONObject.parseObject(ss);
    }


}
