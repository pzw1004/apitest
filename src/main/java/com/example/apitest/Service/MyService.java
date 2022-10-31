package com.example.apitest.Service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @Author 宋宗垚
 * @Date 2019/8/7 16:23
 * @Description TODO
 */
@Service
public class MyService {


    @Async
    public void testAsync1(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName()+" --------");
    }

    public void tttt(){
        System.out.println("------------------");
    }


//    public void training(){
//        trainingThread th = new trainingThread();
//        Thread t1 = new Thread(th);
//        t1.start();
//        try {
//            t1.join();
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//
//    }
}
