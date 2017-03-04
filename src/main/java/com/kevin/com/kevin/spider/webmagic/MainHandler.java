package com.kevin.com.kevin.spider.webmagic;

import us.codecraft.webmagic.Spider;

/**
 * Created by kaiwen on 28/02/2017.
 *
 * 找到一个获取csdn用户的方法
 * 首先从百度搜索技术关键字过滤到csdn的博客，然后进入博客获取此人的评论排行，从评论中就又能获取到其他的
 * 用户信息，从而不断的重复爬取就能获取到用户数据
 */
public class MainHandler {


    public static void main(String[] args) {

        Spider spider = Spider.create(new CsdnListPageProcessor());
        int page = 1;
        String listUrl = "http://blog.csdn.net/peoplelist.html?channelid=0&page=";

        spider.run();
    }


    public void expertComment(String dbId){




    }

    public void getMemberListPageInfo(String id){



    }

}
