package com.example.apitest.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.apitest.Dao.DamageDetectMessage;
import com.example.apitest.Entity.ResultFromDetection;
import com.example.apitest.utils.EnvironmentPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 *
 * @Author 宋宗垚
 * @Date 2019/1/21 11:10
 * @Description 执行算法的Service类
 *
 */
@Service
public class ExecuteAlgorithmService {
    // 默认的python算法文件的位置
    private String detectionExeFilePath;// = "H:\\Anaconda\\Anaconda3\\envs\\welddetection\\python.exe H:\\LabelProject\\code\\PyTorch-YOLOv3-master\\detect_oneImage.py";//
    private String EdgedetectionExeFilePath; // /anaconda/bin/python  ./UNet3_plus-main/eval_img.py --output_folder ./output --checkpoint checkpoints/2022-07-28_22_18_11/chk_499.pth --img_path
    private String OCRdetectionExeFilePath; // /anaconda/bin/python  ./yolov5-master/detect.py --weights runs/best_exp/weights/best.pt --imgsz 640 --hide-conf --exist-ok --source 
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     *
     * 全局路径配置
     */
//    String pythonExE = "D:\\Anaconda\\envs\\bochuan\\python.exe";
    String pythonExE = "D:\\develop\\python\\Anconda3\\envs\\ML_env\\python.exe";
    String detect_path = "D:\\work\\apitest_aiservice\\defect-YOLOv3\\pytorchyolo";
    String action ="D:\\work\\apitest_aiservice\\unet_nested_multiple_classification_master_src_resolution\\pic_save.py";
//    String action ="D:\\hanfeng\\unet\\pic_save.py";
//    String orc_cmd_head ="cmd /c cd D: && conda activate bochuan"+"&&";
    String orc_cmd_head ="cmd /c cd D: && conda activate ML_env"+"&&";
//    String args_ocr = "cmd /c cd D: && conda activate ML_env"+"&&" + OCRdetectionExeFilePath + " " +"--source" +" "+ filePath;
//    String edge_cmd_head ="cmd /c cd D: && conda activate bochuan"+"&&";
    String edge_cmd_head ="cmd /c cd D: && conda activate ML_env"+"&&";
//        String args_edge = "cmd /c cd D: && conda activate ML_env"+"&&" +EdgedetectionExeFilePath + " --img_path "
    public ExecuteAlgorithmService() {
        this.detectionExeFilePath = EnvironmentPath.getInstance().getPythonExEPath() +
                " "+ EnvironmentPath.getInstance().getDetectionPythonFilePath();
        this.EdgedetectionExeFilePath = EnvironmentPath.getInstance().getPythonExEPath() +
                " "+ EnvironmentPath.getInstance().getEdgedetectionExeFilePath();
        this.OCRdetectionExeFilePath = EnvironmentPath.getInstance().getPythonExEPath() +
                " "+ EnvironmentPath.getInstance().getOCRdetectionExeFilePath();
//        // 默认读取的配置文件名称
//        String configFilePath = "config.txt";
//        File directory = new File("..");
//        String directoryPath = "";
//        try {
//            directoryPath = directory.getCanonicalPath(); //得到的是C:/test
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        configFilePath = directoryPath +"\\" + configFilePath;
//        File configFile = new File(configFilePath);
//        if (configFile.exists()){
//            // 如果配置信息文件存在
//            String configStr = readFile(configFilePath);
//            if (!configStr.isEmpty()){
//                // 如果配置文件中的内容不为空
//                detectionExeFilePath = configStr;
//            }
//        }
//    this.EdgedetectionExeFilePath = null; // TODO:通过配置文件读取python解释器路径和边缘检测文件。
//    this.OCRdetectionExeFilePath = null; // TODO:通过配置文件读取python解释器路径和OCR检测文件。
    }

    public void savePic(String path,String fileName,String points){
//        String pythonExE = "D:\\develop\\python\\Anconda3\\envs\\ML_env\\python.exe";
//        String action ="D:\\work\\apitest_aiservice\\unet_nested_multiple_classification_master_src_resolution\\pic_save.py";
//        String action ="D:/FlawSegmentation/PSPNet/test_interface.py";
        String args = pythonExE+" "+action+" "+"--dir_path"+" "+path+" "+"--pic_name"+" "+fileName+" "+"--flaws"+" "+points;
        System.out.println(args);
//        String args = "E:/Anaconda/envs/lpf/python.exe D:/FlawSegmentation/PSPNet/test_interface.py";
        try {
//            System.out.println("test");
            Process pr = Runtime.getRuntime().exec(args);
            InputStreamReader ir = new InputStreamReader(pr.getInputStream());
            LineNumberReader in = new LineNumberReader(ir);
            String line;
//            System.out.println(in.readLine());
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
//
        }catch (IOException e) {
            System.out.println("保存错误");

        }
    }


    /**
     *
     * @param filePath 需要检测的图像完整路径
     * @return 检测完成之后的结果信息
     */
    public DamageDetectMessage excute(String filePath){

        DamageDetectMessage message = new DamageDetectMessage();


        // 先检验需检测的图片是否存在
        File file = new File(filePath);
        if(!file.exists()){
            // 如果文件不存在
            message.setStatus(false);
            message.setInfo("该文件不存在");
            return message;
        }


        // 执行python脚本   H:\Anaconda\Anaconda3\envs\welddetection\python.exe H:\LabelProject\code\PyTorch-YOLOv3-master\detect_oneImage.py E:\IDEA\apitest\src\main\resources\static\\upload\1332420190611043817.tif
        long startTime =  System.currentTimeMillis();
//        System.out.println("---------------------------------------------开始执行脚本---------------------------------------------");
        logger.info("---开始执行脚本---");
        Runtime mt =Runtime.getRuntime();

        String args = detectionExeFilePath + " " + filePath;//H:\LabelProject\20190102\3_channels\VIDARImage1.jpg
        try {
            Process pr = mt.exec(args);
            System.out.println(args+'\n');
//            BufferedReader in = new BufferedReader(new InputStreamReader(
//                    pr.getInputStream()));
            InputStreamReader ir = new InputStreamReader(pr.getInputStream());
            LineNumberReader in = new LineNumberReader(ir);
            String line;
//            System.out.println(in.readLine());
            while ((line = in.readLine()) != null) {
//                {"x_min": 111.56450653076172, "y_min": 452.42237854003906, "x_max": 150.10369873046875,
//                  "y_max": 476.8945617675781, "conf": 0.8893440365791321,
//                      "cls_conf": 0.9999973773956299, "cls_pred": 0}
                //
                System.out.println(line);
                String[] res = line.split(":");
                for (int i = 0; i < res.length; i++) {
                    System.out.println("第"+(i+1)+"个点:");
                    System.out.println(res[i]);
                }

//                if(line.startsWith("damage location")){

//                    System.out.println("====2========");
//                    String[] splited = line.split("@@");
//                    System.out.println(splited);
//                    JSONObject jsonObject = JSONObject.parseObject(splited[1]);
//                    //  damage location x: ', x2 ,' y: ',y2
//                    int x_min = jsonObject.getDouble("x_min").intValue();
//                    int y_min = jsonObject.getDouble("y_min").intValue();
//                    int x_max = jsonObject.getDouble("x_max").intValue();
//                    int y_max = jsonObject.getDouble("y_max").intValue();
////                    System.out.println("x_min:"+x_min+"y_min"+y_min+"x_max"+x_max+"y_max"+y_max);
//                    DamageData dd = new DamageData(x_min,y_min,x_max,y_max,"Algorithm",
//                            jsonObject.getInteger("cls_pred"),jsonObject.getDouble("conf"),jsonObject.getDouble("cls_conf"));
//                    System.out.println(dd.toString());
//                    message.getDamageDataList().add(dd);
                }
//                ddd = ddd.concat(line);
//                System.out.println(line);
//            }
            in.close();
            pr.waitFor();
            pr.destroy();
        }catch (InterruptedException e) {
//
            logger.error("---执行检测算法的时候出错---");
            logger.error(e.getMessage());
            message.setInfo(e.getMessage());
            message.setStatus(false);
        } catch (IOException e) {
            logger.error("---执行检测算法的时候出错---");
            logger.error(e.getMessage());
//            System.out.println(e.getMessage());
            message.setInfo(e.getMessage());
            message.setStatus(false);
        }

        long endTime =  System.currentTimeMillis();
        long usedTime = (endTime-startTime)/1000;
        logger.info("---算法执行结束，总时间为"+usedTime/60.0+" 分钟");
//        System.out.println("---------------------------------------------脚本执行结束---------------------------------------------");
//        System.out.println("---------------------用时--------------"+usedTime+"---------------------------------------------");
        return message;

    }

    public ResultFromDetection excute_v2(String filePath){
        ResultFromDetection resultFromDetection = new ResultFromDetection();
        List<String> position_list = new ArrayList<>();
        List<String> flaw_type = new ArrayList<>();
        List<String> beliefs = new ArrayList<>();
        long startTime =  System.currentTimeMillis();
//        System.out.println("---------------------------------------------开始执行脚本---------------------------------------------");
        logger.info("---开始执行缺陷检测脚本---");
        Runtime mt =Runtime.getRuntime();
        String args = "cmd /c D: && cd "+detect_path+" && D: && conda activate ML_env"+"&&" +detectionExeFilePath + " " + "--image "+filePath;//H:\LabelProject\20190102\3_channels\VIDARImage1.jpg
        try {
            Process pr = mt.exec(args);
            System.out.println(args+'\n');
            InputStreamReader ir = new InputStreamReader(pr.getInputStream());
            LineNumberReader in = new LineNumberReader(ir);
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println("vvvvvvvvvvvvvvvvvvvv");
                System.out.println(line);
                System.out.println("^^^^^^^^^^^^^^^^^^^^");
                line = line.replace('[',' ');
                line = line.replace(']',' ');
                line = line.trim();
                String[] linedata = line.split("\\s+");
                for (int i=0 ; i<linedata.length;i++){
                    int r = i % 6;
                    if(r == 0){// new epoch
                        int x1 = Math.round(Float.parseFloat(linedata[i]));
                        int y1 = Math.round(Float.parseFloat(linedata[i+1]));
                        int x2 = Math.round(Float.parseFloat(linedata[i+2]));
                        int y2 = Math.round(Float.parseFloat(linedata[i+3]));
                        String rect = x1+","+y1+" "+x2+","+y1+" "+x2+","+y2+" "+x1+","+y2;
                        position_list.add(rect);
                        flaw_type.add(linedata[i+5]);
                        beliefs.add(linedata[i+4]);
                    }
                }
                }
            resultFromDetection.setPosition(position_list);
            resultFromDetection.setBeliefs(beliefs);
            resultFromDetection.setFlaw_type(flaw_type);
            in.close();
            pr.waitFor();
            pr.destroy();
        }catch (InterruptedException e) {
//
            logger.error("---执行检测算法的时候出错---");
            logger.error(e.getMessage());

        } catch (IOException e) {
            logger.error("---执行检测算法的时候出错---");
            logger.error(e.getMessage());
        }

        long endTime =  System.currentTimeMillis();
        long usedTime = (endTime-startTime)/1000;
        logger.info("---算法执行结束，总时间为"+usedTime/60.0+" 分钟");
//        System.out.println("---------------------------------------------脚本执行结束---------------------------------------------");
//        System.out.println("---------------------用时--------------"+usedTime+"---------------------------------------------");
        return resultFromDetection;

    }



    public String getRandomFileName() {

        SimpleDateFormat simpleDateFormat;
        simpleDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
        Date date = new Date();
        String str = simpleDateFormat.format(date);
        Random random = new Random();
        int rannum = (int) (random.nextDouble() * (99999 - 10000 + 1)) + 10000;// 获取5位随机数
        return rannum + str;// 当前时间
    }


    public String readFile(String filePath){
        try {
            File jsonFile = new File(filePath);
            FileReader fileReader = new FileReader(jsonFile);

            Reader reader = new InputStreamReader(new FileInputStream(jsonFile),"utf-8");
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            fileReader.close();
            reader.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
