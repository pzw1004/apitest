package com.example.apitest;

import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApitestApplicationTests {

    @Test
    public void contextLoads() {
//        damage location @@ {"x_min": 111.56450653076172, "y_min": 452.42237854003906, "x_max": 150.10369873046875, "y_max": 476.8945617675781, "conf": 0.8893440365791321, "cls_conf": 0.9999973773956299, "cls_pred": 0}
//damage location @@ {"x_min": 1438.865966796875, "y_min": 399.70437240600586, "x_max": 1473.2766723632812, "y_max": 429.1577453613281, "conf": 0.8596952557563782, "cls_conf": 0.9999997615814209, "cls_pred": 0}
//damage location @@ {"x_min": 3412.7866821289062, "y_min": 934.65625, "x_max": 3524.7908935546875, "y_max": 1094.7154541015625, "conf": 0.9922358393669128, "cls_conf": 0.9999822378158569, "cls_pred": 0}
//damage location @@ {"x_min": 6591.931884765625, "y_min": 819.11376953125, "x_max": 6630.0731201171875, "y_max": 1183.1865844726562, "conf": 0.9520813822746277, "cls_conf": 0.999992847442627, "cls_pred": 0}
//damage location @@ {"x_min": 6577.108123779297, "y_min": 739.42236328125, "x_max": 6635.550872802734, "y_max": 792.7274169921875, "conf": 0.946650505065918, "cls_conf": 0.9999371767044067, "cls_pred": 0}
//damage location @@ {"x_min": 6602.977233886719, "y_min": 793.2806549072266, "x_max": 6629.172302246094, "y_max": 834.7659759521484, "conf": 0.9058837890625, "cls_conf": 0.9999310970306396, "cls_pred": 0}
//damage location @@ {"x_min": 6591.204742431641, "y_min": 773.5471801757812, "x_max": 6633.807189941406, "y_max": 821.9480285644531, "conf": 0.8954223990440369, "cls_conf": 0.9999524354934692, "cls_pred": 0}
        String xx = "damage location @@ {\"x_min\": 1438.865966796875, \"y_min\": 399.70437240600586, \"x_max\": 1473.2766723632812, \"y_max\": 429.1577453613281, \"conf\": 0.8596952557563782, \"cls_conf\": 0.9999997615814209, \"cls_pred\": 0}";
        String obj = xx.split("@@")[1].trim();
        JSONObject jsonObject = JSONObject.parseObject(obj);
        System.out.println(jsonObject);
    }

}

