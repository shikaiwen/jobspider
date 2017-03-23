package com.kevin.com.kevin.jobdispatcher;

/**
 * Created by kaiwen on 23/03/2017.
 */
public class RemoteMsgController {


    public static String ACTION_START = "start";
    public static String ACTION_SUSPEND = "suspend";
    public static String ACTION_RECORVER = "recover";

    public static String ACTION_STOP = "stop";

    public static boolean checkAndDoAction(){

        String action = actionFromRemote();
        if (ACTION_START.equals(action)) {

            JobDispatcher jobDispatcher = new JobDispatcher();
            jobDispatcher.dispatch();

        } else if (ACTION_STOP.equals(action)) {

            Thread.currentThread().stop();

        }else if(ACTION_RECORVER.equals(action)){

        }

        return true;
    }


    public static String actionFromRemote(){

        return ACTION_START;
    }

}
