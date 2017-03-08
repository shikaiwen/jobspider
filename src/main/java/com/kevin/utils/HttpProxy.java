package com.kevin.utils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

/**
 * Created by kaiwen on 07/03/2017.
 */
public class HttpProxy {



    public static Proxy getProxy(){

        return getFreeHttpProxy();
    }

    // http://proxy.mimvp.com/free.php
    public static Proxy getFreeHttpProxy(){

        String ip = "120.52.72.5";
        Integer port = 80;
        boolean b = TelnetUtil.checkAvaliable(ip, port);
        if(b) {
            return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));
        }

        if(true) return Proxy.NO_PROXY;

        Document doc = JsoupOk.getDocumentWithRetry("http://proxy.mimvp.com/free.php");
        Elements select = doc.select("#list tr");
        if (select != null && select.size() > 0) {

            for (int i = 1; i < select.size(); i++) {
                Element element = select.get(i);
                Elements ipTd = element.select("td:nth-child(2)");
//                Elements portTd = element.select("td:nth-child(3)");
                String portImgSrc = element.select("td:nth-child(3)").get(0).children().get(0).attr("src");
                /**
                 * 可以尝试去自动识别验证码
                 * http://proxy.mimvp.com/free.php
                 * http://www.open-open.com/solution/view/1319815639437
                 */

            }


        }

        return null;
    }

    void tt() throws IOException {

//        HttpURLConnection urlConnection = (HttpURLConnection)new URL("").openConnection();

    }

    public static void main(String[] args) {
//        freeHttpProxy();
        connectWithProxy();

    }


    public static void connectWithProxy(){

//        InetSocketAddress proxyInet = new InetSocketAddress("120.52.72.59",80);
//        Proxy proxy = new Proxy(Proxy.Type.HTTP, proxyInet);
        Proxy proxy = getFreeHttpProxy();

        try {
            HttpURLConnection urlConnection = (HttpURLConnection)new URL("http://www.csdn.net/").openConnection(proxy);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            int copy = IOUtils.copy(urlConnection.getInputStream(), baos);
            String content = new String(baos.toByteArray(), "UTF-8");
            System.out.println(content);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
