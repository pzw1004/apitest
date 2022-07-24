package com.example.apitest.Dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * @Author 宋宗垚
 * @Date 2019/1/21 10:30
 * @Description 作为消息类，传递给调用者 TODO:具体消息内容待定
 */

@Service
public class DamageDetectMessage {

    // 用于显示状态的变量
    private String status ;
    //消息的详细信息
    private String info;
    //用于记录消息时间的时间戳
    private Timestamp timestamp;
    private List<DamageData> damageDataList;

    public DamageDetectMessage(){
        Date date = new Date();
        this.timestamp  = new Timestamp(date.getTime());
        this.status = "SUCCESS";
        this.damageDataList = new ArrayList<>();
        this.info = "nothing";
    }

    public void addOneDamageData(DamageData data){
        this.damageDataList.add(data);
    }
    public List<DamageData> getDamageDataList(){
        return damageDataList;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(boolean b) {
        if (b){
            this.status = "SUCCESS";
        }else {
            this.status = "FAILED";
        }

    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }



    /**
     * 将message类中的数据转化为Json格式的数据用于返回给调用者
     * @return
     */
    public JSONObject getJsonData(){
        JSONObject result = new JSONObject();

        result.put("timestamp",this.timestamp.toString());
        result.put("status",this.status);
        result.put("info",this.info);
        result.put("damageDataList",this.damageDataList);
        return result;
    }



}
