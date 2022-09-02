package com.example.apitest.Service;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class trainingThread implements Runnable{
    private Thread worker;
    private final AtomicBoolean running = new AtomicBoolean(false);
    public int epoch = 0;
    @Override
    public void run() {
        try{
            for (int i = 1;i<=100;i++){
                if(running.get()){
                    RetrainModelService.epoch++ ;
                    try{
                        Thread.sleep(3000);
                    }
                    catch (InterruptedException e){

                        e.printStackTrace();
                    }
                }else{
                    System.out.println("已经是停止状态了，我要退出了！");
                    throw new InterruptedException();
                }

            }
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }


        }


    public void start() {
        worker = new Thread(this);
        running.set(true);
        worker.start();
    }

    public void stop() {
        RetrainModelService.epoch=0;
        running.set(false);
    }
}
