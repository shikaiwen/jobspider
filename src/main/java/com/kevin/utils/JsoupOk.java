package com.kevin.utils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by kaiwen on 07/03/2017.
 */
public class JsoupOk {


    static Logger logger = LoggerFactory.getLogger(JsoupOk.class);

    public static void main(String[] args) {
        logger.warn("dasfasfasdfasdf......");
//        getDocumentWithRetry("http://blog.csdn.net/caimouse/article/list/1452");
//        getDocumentWithRetry("http://blog.csdn.net/qq_37357293");

        Random random = new Random();

        for(int i = 0 ;i < 100;i++) {
            System.out.println(random.nextInt(6));
        }

    }


    public static Document getDocumentWithRetry(String url) {
        return getDocumentWithRetry(url,3);
    }


    public static Document getDocumentWithRetry(String url,int times) {

        List <String> agentList = new ArrayList <>();
        agentList.add("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
        agentList.add("Mozilla/5.0 (Windows NT 6.1; WOW64; ");
        agentList.add("Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; WOW64; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR ");


        Document resultDoc = null;
        int cur = 1;

        String agent = agentList.get(0);

        while (cur < times) {
            try {
                Document doc = Jsoup.connect(url)
                        .header("User-Agent", agent)
                        .proxy(HttpProxy.getProxy())
                        .get();
                resultDoc = doc;
                break;
            } catch (IOException e) {
                //change User-Agent header
                agent = agentList.get(new Random().nextInt(agentList.size()));
                e.printStackTrace();
            }
            cur++;
        }

        if (resultDoc == null) {
            logger.warn("尝试获取document失败:url" + url) ;
//            logger.info("尝试使用代理:url" + url);
//            getDocumentWithRetryProxy(url, times, null);
            logger.info("尝试使用JDK HttpConnection ,url : " + url);
            resultDoc = getWithJdkHttpConnection(url);

            if(resultDoc == null){
                logger.error("获取url失败,url="+url);
            }
        }

        return resultDoc;
    }


    public static Document getWithJdkHttpConnection(String url){

        try {
            HttpURLConnection urlConnection = (HttpURLConnection)new URL(url).openConnection();

            urlConnection.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
//                urlConnection.setRequestProperty("Accept-Encoding","gzip, deflate, sdch");
            urlConnection.setRequestProperty("Accept-Language","zh-CN,zh;q=0.8,en-US;q=0.6,en;q=0.4");
            urlConnection.setRequestProperty("Cache-Control","max-age=0");
            urlConnection.setRequestProperty("Connection","keep-alive");
            urlConnection.setRequestProperty("Host","blog.csdn.net");
            urlConnection.setRequestProperty("Upgrade-Insecure-Requests","1");
            urlConnection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537. (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            IOUtils.copy(urlConnection.getInputStream(), baos);
            String content = new String(baos.toByteArray(), "UTF-8");
            Document doc = Jsoup.parse(content);

            return doc;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }


    public static Document getDocumentWithRetryProxy(String url, int times, Proxy proxy){

        if (proxy == null) {

        }
        return null;

    }


}
