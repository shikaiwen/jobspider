package com.kevin.service;

import org.apache.commons.collections.FastHashMap;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kaiwen on 10/03/2017.
 */
public class ServiceFactory {


    static Map <Class <?>, Object> serviceHolder = new FastHashMap();

    public static <T> T getService(Class <T> t)  {

        try{
            if (serviceHolder.get(t) == null) {
                T t1 = t.newInstance();
                serviceHolder.put(t, t1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("instantiate class error, " + t.getCanonicalName() ,e);
        }

        return (T)serviceHolder.get(t);

    }


}
