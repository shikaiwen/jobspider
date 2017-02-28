package com.kevin.com.kevin.spider.webmagic;

import com.kevin.selenium.WebDriverTest;
import us.codecraft.webmagic.Spider;

/**
 * Created by kaiwen on 28/02/2017.
 */
public class MainHandler {


    public static void main(String[] args) {

        Spider spider = Spider.create(new CsdnListPageProcessor());
        int page = 1;
        String listUrl = "http://blog.csdn.net/peoplelist.html?channelid=0&page=";

        spider.run();
    }


    public void getMemberListPageInfo(String id){



    }

}
