package com.example.apitest.Service;

import java.io.*;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class trainingThread implements Runnable{
    public Process pr ;
    private Thread worker;
    private final AtomicBoolean running = new AtomicBoolean(false);
    public int epoch = 0;
    public String[] args;

    public trainingThread(String[] args) {
        this.args = args;
    }

    @Override
    public void run() {
        if(running.get()){
            try {
                pr =  Runtime.getRuntime().exec(this.args);
                pr.waitFor();
                pr.destroy();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }


    }


    public void start() {
        worker = new Thread(this);
        running.set(true);
        worker.start();
    }

    public void stop() throws IOException {
        pr.destroy();
        Runtime.getRuntime().exec("taskkill /f /t /im python.exe");
        RetrainModelService.epoch=0;
        running.set(false);
    }
}
