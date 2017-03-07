package com.kevin.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by kaiwen on 07/03/2017.
 */
public class JsoupOk {

    public static void main(String[] args) {
        logger.warn("dasfasfasdfasdf......");
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
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36")
                        .get();
                resultDoc = doc;

                break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            cur++;
        }
        if (resultDoc == null) {
            logger.warn("获取document失败:url" + url);
        }
        return resultDoc;
    }

}
