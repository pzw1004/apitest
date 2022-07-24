package com.example.apitest.Dao;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author 宋宗垚
 * @Date 2019/8/1 14:28
 * @Description TODO
 */
public class Model {
    private String modelPath;
    private Double mAP;
    private Date date;

    public Model(String modelPath, Double mAP) {
        this.modelPath = modelPath;
        this.mAP = mAP;
        this.date = new Date();


    }

    public String getDateString(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");
        return simpleDateFormat.format(this.date);
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getModelPath() {
        return modelPath;
    }

    public void setModelPath(String modelPath) {
        this.modelPath = modelPath;
    }

    public Double getmAP() {
        return mAP;
    }

    public void setmAP(Double mAP) {
        this.mAP = mAP;
    }
}
