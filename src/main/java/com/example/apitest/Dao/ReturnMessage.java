package com.example.apitest.Dao;

/**
 * @Author 宋宗垚
 * @Date 2019/7/20 22:10
 * @Description TODO
 */
public class ReturnMessage {
    private boolean success;
    private String message;
    private Object data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public ReturnMessage(boolean success, String message, Object data) {

        this.success = success;
        this.message = message;
        this.data = data;
    }
}
