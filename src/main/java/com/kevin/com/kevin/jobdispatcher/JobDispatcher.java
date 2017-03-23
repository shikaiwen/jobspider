package com.kevin.com.kevin.jobdispatcher;

import com.kevin.com.kevin.spider.webmagic.MainHandler;

/**
 * Created by kaiwen on 23/03/2017.
 */
public class JobDispatcher {



    public static void main(String[] args) {
        JobDispatcher dis = new JobDispatcher();
        dis.dispatch();
    }


    public void dispatch(){

        MainHandler handler = new MainHandler();
//        handler.job1();

        Thread job2Thread = new Thread(new Runnable() {
            @Override
            public void run() {
                handler.job2();
            }
        });

        Thread job3Thread = new Thread(new Runnable() {
            @Override
            public void run() {
                handler.job3();
            }
        });

        job2Thread.start();
        job3Thread.start();

        try {
            job2Thread.join();
            job3Thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
