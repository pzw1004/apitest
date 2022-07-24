package com.example.apitest.Dao;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * @Author 宋宗垚
 * @Date 2019/1/21 16:28
 * @Description TODO
 */
public class ExecuteException extends Exception {
    private String errMessage;



    public ExecuteException(String line ,BufferedReader err){
        this.errMessage = line.concat("\n");
        try{
            while ((line = err.readLine()) != null) {
                // TODO：获取python文件输出的内容并将其传递给message
                this.errMessage = this.errMessage.concat(line).concat("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String getErrMessage(){
        return this.errMessage;
    }


}
