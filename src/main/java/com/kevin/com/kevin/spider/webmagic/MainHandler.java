package com.kevin.com.kevin.spider.webmagic;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kevin.entity.BlogMember;
import com.kevin.service.MemberService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.Spider;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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

        MainHandler mainHandler = new MainHandler();
        mainHandler.start();





    }


    public void start(){


        MemberService memberService = new MemberService();
        BlogMember expertMember = memberService.getMemberToStartSpider();


        String username = expertMember.getUsername();
        String listPageAddress = expertMember.getAddress();

        String commentUrl = "";
        Document document;
        try {
            document = Jsoup.connect(listPageAddress).get();
            Elements select = document.select("#hotarticls2 .itemlist li");
            if (CollectionUtils.isEmpty(select))  return;

            //第一个评论最多
            String href = select.get(0).select("a").attr("href");
            String countTxt = select.get(0).select("span").text();
            String commentCnt = countTxt.replaceAll("[\\(\\)]", "");

            int i = NumberUtils.toInt(commentCnt, 0);
            if(i == 0) return;

            //href格式为文章链接：/huanghm88/article/details/3965218
            //获取评论有问题，直接通过url访问：http://blog.csdn.net/huanghm88/comment/list/3965218?page=1
            commentUrl = href.replace("article/details","comment/list");

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try {
            String body = Jsoup.connect(listPageAddress).execute().body();
            JSONObject parse = (JSONObject) JSON.parse(body);
            List<Map> commentList = (List<Map>)parse.get("list");

            memberService.saveComment(commentList);

        } catch (IOException e) {
            e.printStackTrace();
        }


//        getCommentAddress(listPageAddress);

    }


//    public String getCommentAddress(String listPageAddress)  {
//    }


    public void expertComment(String dbId){




    }

    public void getMemberListPageInfo(String id){



    }

}
