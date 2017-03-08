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
import java.net.URLConnection;

/**
 * Created by kaiwen on 07/03/2017.
 */
public class JsoupOk {

    public static void main(String[] args) {
        logger.warn("dasfasfasdfasdf......");
        getDocumentWithRetry("http://blog.csdn.net/caimouse/article/list/1452");
    }

    static Logger logger = LoggerFactory.getLogger(JsoupOk.class);

    public static Document getDocumentWithRetry(String url) {
        return getDocumentWithRetry(url,3);
    }


    public static Document getDocumentWithRetry(String url,int times) {
        Document resultDoc = null;
        int cur = 1;
        while (cur < times) {
            try {

                Document doc = Jsoup.connect(url)
                        .header("User-Agent", "kessa")
//                        .proxy(HttpProxy.getProxy())
                        .get();
                resultDoc = doc;

                break;
            } catch (IOException e) {
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e1) {
//                    e1.printStackTrace();
//                }
                e.printStackTrace();
            }
            cur++;
        }
        if (resultDoc == null) {
            logger.warn("尝试获取document失败:url" + url);
//            logger.info("尝试使用代理:url" + url);
//            getDocumentWithRetryProxy(url, times, null);
            resultDoc = getWithJdkHttpConnection(url);

            return resultDoc;
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
