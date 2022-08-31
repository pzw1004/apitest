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
    public ExecuteAlgorithmService() {
        this.detectionExeFilePath = EnvironmentPath.getInstance().getPythonExEPath() +
                " "+ EnvironmentPath.getInstance().getDetectionPythonFilePath();
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
    this.EdgedetectionExeFilePath = null; // TODO:通过配置文件读取python解释器路径和边缘检测文件。
    this.OCRdetectionExeFilePath = null; // TODO:通过配置文件读取python解释器路径和OCR检测文件。
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

//        DamageDetectMessage message = new DamageDetectMessage();
        ResultFromDetection resultFromDetection = new ResultFromDetection();
        String[] resPerCategories = null;
        String[] beliefs = null;
        String[][] res = null;
        String[][] res2 = null;
//        String line = "";
        // 执行python脚本   H:\Anaconda\Anaconda3\envs\welddetection\python.exe H:\LabelProject\code\PyTorch-YOLOv3-master\detect_oneImage.py E:\IDEA\apitest\src\main\resources\static\\upload\1332420190611043817.tif
        long startTime =  System.currentTimeMillis();
//        System.out.println("---------------------------------------------开始执行脚本---------------------------------------------");
        logger.info("---开始执行缺陷检测脚本---");
        Runtime mt =Runtime.getRuntime();

        String args = detectionExeFilePath + " " + filePath;//H:\LabelProject\20190102\3_channels\VIDARImage1.jpg
        try {
            Process pr = mt.exec(args);
            System.out.println(args+'\n');

            InputStreamReader ir = new InputStreamReader(pr.getInputStream());
            LineNumberReader in = new LineNumberReader(ir);
            String line;
//            line = in.readLine();
            //            System.out.println(in.readLine());
            while ((line = in.readLine()) != null) {
//                {"x_min": 111.56450653076172, "y_min": 452.42237854003906, "x_max": 150.10369873046875,
//                  "y_max": 476.8945617675781, "conf": 0.8893440365791321,
//                      "cls_conf": 0.9999973773956299, "cls_pred": 0}
                // TODO 注意！！！ 这里手动加了额外的可信度以便测试！！！！！
//                line = line + "*0.9:0.8---";
//                StringBuilder beliefAdds = new StringBuilder("*");
                System.out.println("vvvvvvvvvvvvvvvvvvvv");
                System.out.println(line);
                System.out.println("^^^^^^^^^^^^^^^^^^^^");
                String[] allOfData = line.split("\\*");  // *前为position, *后为belief
                resPerCategories = allOfData[0].split("-");
//                System.out.println(resPerCategories.length);
//                System.out.println(resPerCategories[resPerCategories.length - 1] == null);
                res = new String[resPerCategories.length][];
                for (int j = 0; j < resPerCategories.length; j++){
                    res[j] = resPerCategories[j].split(":");
                    for (int i = 0; i < res[j].length; i++) {
                        System.out.println("第"+(i+1)+"个点:");
                        System.out.println(res[j][i]);
                    }
                }
                System.out.println("===下面是这个点的可信度===========================================");
                beliefs = allOfData[1].split("-");
//                System.out.println(resPerCategories.length);
//                System.out.println(resPerCategories[resPerCategories.length - 1] == null);
                res2 = new String[beliefs.length][];
                for (int j = 0; j < beliefs.length; j++){
                    res2[j] = beliefs[j].split(":");
                    for (int i = 0; i < res2[j].length; i++) {
                        System.out.println("第"+(i+1)+"个点:");
                        System.out.println(res2[j][i]);
                    }
                }
                resultFromDetection.setPosition(res);
                resultFromDetection.setBelief(res2);
                // 没有belief的版本
//                res = new String[resPerCategories.length][];
//                for (int j = 0; j < resPerCategories.length; j++){
//                    res[j] = resPerCategories[j].split(":");
//                    for (int i = 0; i < res[j].length; i++) {
//                        System.out.println("第"+(i+1)+"个点:");
//                        System.out.println(res[j][i]);
//                    }
//                }

//                res = line.split(":");
//                for (int i = 0; i < res.length; i++) {
//                    System.out.println("第"+(i+1)+"个点:");
//                    System.out.println(res[i]);
//                }

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

        } catch (IOException e) {
            logger.error("---执行检测算法的时候出错---");
            logger.error(e.getMessage());
        }


        logger.info("---开始执行OCR脚本---");
        Runtime mt_ocr =Runtime.getRuntime();
        String args_ocr = OCRdetectionExeFilePath + " " + filePath;//H:\LabelProject\20190102\3_channels\VIDARImage1.jpg
        String houdu = null;
        try {
            Process pr = mt_ocr.exec(args_ocr);
            System.out.println(args_ocr+'\n');

            InputStreamReader ir = new InputStreamReader(pr.getInputStream());
            LineNumberReader in = new LineNumberReader(ir);
            String line_ocr;
            while((line_ocr=in.readLine()) != null) // {"T":"18"}
            {
                JSONObject json_houdu = JSON.parseObject(line_ocr);
                houdu = json_houdu.get("T").toString();
                resultFromDetection.setHoudu(houdu);
            }


            in.close();
            pr.waitFor();
            pr.destroy();
        }catch (InterruptedException e) {
//
            logger.error("---执行OCR算法的时候出错---");
            logger.error(e.getMessage());

        } catch (IOException e) {
            logger.error("---执行OCR算法的时候出错---");
            logger.error(e.getMessage());
        }


        logger.info("---开始执行边缘检测脚本---");
        Runtime mt_edge =Runtime.getRuntime();
        String args_edge = EdgedetectionExeFilePath + " " + filePath;//H:\LabelProject\20190102\3_channels\VIDARImage1.jpg
        
        try {
            Process pr = mt_edge.exec(args_ocr);
            System.out.println(args_edge+'\n');

            InputStreamReader ir = new InputStreamReader(pr.getInputStream());
            LineNumberReader in = new LineNumberReader(ir);
            String line_edge;
            while((line_edge=in.readLine()) != null) // 121,244 122,244
            {
                String[] all_edges = line_edge.split(":");
                resultFromDetection.setEdge(all_edges);
            }


            in.close();
            pr.waitFor();
            pr.destroy();
        }catch (InterruptedException e) {
//
            logger.error("---执行OCR算法的时候出错---");
            logger.error(e.getMessage());

        } catch (IOException e) {
            logger.error("---执行OCR算法的时候出错---");
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
