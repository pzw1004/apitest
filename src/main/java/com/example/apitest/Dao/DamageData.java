package com.example.apitest.Dao;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @Author 宋宗垚
 * @Date 2019/1/21 12:22
 * @Description 损伤的实体类，
 */
public class DamageData {


    private int x_min;
    private int y_min;
    private int x_max;
    private int y_max;
    private int damageType;
    private double conf;
    private double cls_conf;
    private String author;
    private Timestamp timestamp;

    public DamageData(int x_min,int y_min,int x_max,int y_max,String author,int damageType,double conf,double cls_conf){
        this.x_min = x_min;
        this.x_max = x_max;
        this.y_min = y_min;
        this.y_max = y_max;
        this.author = author;
        Date date = new Date();
        this.timestamp = new Timestamp(date.getTime());
        this.damageType = damageType;
        this.conf = conf;
        this.cls_conf = cls_conf;
    }

    public DamageData(){

    }

    public Integer getDamageType() {
        return this.damageType;
    }

    public void setDamageType(int damageType) {

        this.damageType = damageType;
    }

    public int getX_min() {
        return x_min;
    }

    public void setX_min(int x_min) {
        this.x_min = x_min;
    }

    public int getY_min() {
        return y_min;
    }

    public void setY_min(int y_min) {
        this.y_min = y_min;
    }

    public int getX_max() {
        return x_max;
    }

    public void setX_max(int x_max) {
        this.x_max = x_max;
    }

    public int getY_max() {
        return y_max;
    }

    public void setY_max(int y_max) {
        this.y_max = y_max;
    }


    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }



    public double getConf() {
        return conf;
    }

    public void setConf(double conf) {
        this.conf = conf;
    }

    public double getCls_conf() {
        return cls_conf;
    }

    public void setCls_conf(double cls_conf) {
        this.cls_conf = cls_conf;
    }

}
