package com.kevin.com.kevin.spider.webmagic;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kevin.entity.BlogArticle;
import com.kevin.entity.BlogMember;
import com.kevin.service.MemberService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by kaiwen on 28/02/2017.
 *
 * 找到一个获取csdn用户的方法
 * 首先从百度搜索技术关键字过滤到csdn的博客，然后进入博客获取此人的评论排行，从评论中就又能获取到其他的
 * 用户信息，从而不断的重复爬取就能获取到用户数据
 *
 *
 * 踩过的坑总结：
 * 1、csdn服务器会根据Http的User-Agent返回403，默认Jsoup使用User-Agent: Java/1.8.0，有问题，随便换一个就行了
 * 2、Jsoup接受json会报错，设置一下请求属性就行了.ignoreContentType(true)
 * 3、调试java的网络请求，-Djavax.net.debug=all
 *
 */
public class MainHandler {


    public static void main(String[] args) throws Exception{

//        Spider spider = Spider.create(new CsdnListPageProcessor());
//        int page = 1;
//        String listUrl = "http://blog.csdn.net/peoplelist.html?channelid=0&page=";
//        spider.run();

//        Document document = Jsoup.connect("http://blog.csdn.net/yuanmeng001")
//                .header("User-Agent","Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36").get();

        MainHandler mainHandler = new MainHandler();
//        mainHandler.start();
        mainHandler.handleListPage("qingfeng812");

    }


    public void setDefaultProxy(){
        System.setProperty("http.proxySet", "true");
        System.setProperty("http.proxyHost", "127.0.0.1");
        System.setProperty("http.proxyPort", "8888");
    }

    public void start(){

        MemberService memberService = new MemberService();
        BlogMember expertMember = memberService.getMemberToStartSpider();

        String username = expertMember.getUsername();
        String listPageAddress = expertMember.getAddress();

        String commentUrl = "";
        Document document;
        try {

            document = Jsoup.connect(listPageAddress)
                    .header("User-Agent","zeneyang").get();
            Elements select = document.select("#hotarticls2 .itemlist li");
            if (CollectionUtils.isEmpty(select))  return;

            //第一个评论最多
            String href = select.get(0).select("a").attr("href");
            String countTxt = select.get(0).select("span").text();
            String commentCnt = countTxt.replaceAll("[\\(\\)]", "");

            int i = NumberUtils.toInt(commentCnt, 0);
            if(i == 0) return;

            //href格式为文章链接：/huanghm88/article/details/3965218
            //获取评论有问题，直接通过url访问：
            commentUrl = href.replace("article/details","comment/list");

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try {
            String body = Jsoup.connect("http://blog.csdn.net"+commentUrl)
                    .header("User-Agent","zeneyang")
                    .ignoreContentType(true)
                    .execute().body();
            JSONObject parse = (JSONObject) JSON.parse(body);
            List<Map> commentList = (List<Map>)parse.get("list");

            memberService.saveComment(commentList);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    /**
     * 从 comment中去提取用户，可以用单独的线程去做
     */
    public void fetchUserFromComment(){

    }

    /**
     * 根据文章去获取评论
     * csdn会返回所有的评论
     * http://blog.csdn.net/huanghm88/comment/list/3965218?page=1
     * 通过测试发现url中的用户名对返回结果没影响(只要用户存在即可)，也就是说csdn其实是根据articleId去获取评论的
     */
    public void getCommentListByArticleId(Integer articleId){

        String commentUrl = "http://blog.csdn.net/shikaiwencn/comment/list/"+articleId+"?page=1";

        try {
            String body = Jsoup.connect("http://blog.csdn.net"+commentUrl)
                    .header("User-Agent","zeneyang")
                    .ignoreContentType(true)
                    .execute().body();
            JSONObject parse = (JSONObject) JSON.parse(body);
            List<Map> commentList = (List<Map>)parse.get("list");

//            memberService.saveComment(commentList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据用户名获取所有的文章
     * @param username
     */
    public void handleListPage(String username){
        String listUrl = "http://blog.csdn.net/"+username;

        try {
            Document document = Jsoup.connect(listUrl)
                    .header("User-Agent", "zeneyang").get();
            Elements select = document.select("#papelist a");

            String pageInfoStr = document.select("#papelist span").text();
            Matcher matcher1 = Pattern.compile("(.*)条").matcher(pageInfoStr);
            String recordCnt = "";
            if(matcher1.find()){
                recordCnt = matcher1.group(1).trim();
            }

            /**
             * 这里有一个hack方式，可以将pagesize设置为一个大于实际页数的，csdn会返回所有的文章
             */
            int count = NumberUtils.toInt(recordCnt, 0);
            String getAllListUrl = listUrl + "/article/list/" + count;

            Document articleDoc = Jsoup.connect(getAllListUrl).header("User-Agent", "neznen").get();

            List<BlogArticle> articleList = new ArrayList<>(30);

            Elements articleElts = articleDoc.select(".list_item_new .list_item");
            articleElts.forEach((item)->{

                Elements titleElt = item.select(".link_title a");
                String articleUrl = titleElt.attr("href");
                String articleIdStr = articleUrl.substring(articleUrl.lastIndexOf("/") + 1);
                String title = titleElt.text();

                String createDateTxt = item.select(".link_postdate").text();
                String viewCountHtmlText = item.select(".link_view").text();
                String viewCntStr = "";
                Matcher matcher = Pattern.compile("(\\()([0-9]+)\\)").matcher(viewCountHtmlText);
                if(matcher.find()){
                    viewCntStr = matcher.group(2);
                }

                String commentCntStr = "";
                Matcher commentCntMatcher = Pattern.compile("(\\()([0-9]+)\\)").matcher(item.select(".link_comments").text());
                if(commentCntMatcher.find()){
                    commentCntStr = matcher.group(2);
                }

                BlogArticle article = new BlogArticle();
                article.setArticleId(NumberUtils.toInt(articleIdStr));
                article.setTitle(title);
                try {
                    article.setCreateDate(DateUtils.parseDate(createDateTxt, "yyyy-MM-dd HH:mm"));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                article.setViewCount(NumberUtils.toInt(viewCntStr, 0));
                article.setCommentCount(NumberUtils.toInt(commentCntStr, 0));
                articleList.add(article);
            });


//            if(select == null || select.size() == 0) return;
//            int maxPage = 1;
//            int currentpage = 1;
//            if(select != null){
//                //获取文章页数
//                Element lastpage = select.get(select.size() - 1);
//                String href = lastpage.attr("href");
//                String lastPageStr = href.substring(href.lastIndexOf("/") + 1);
//                maxPage = NumberUtils.toInt(lastPageStr, 1);
//            }

//            while (currentpage <= maxPage) {
//                String pageUrl = listUrl + "/article/list/" + currentpage;
//                Document articleDoc = null;
//                if (currentpage == 1) {
//                    articleDoc = document;
//                } else {
//                    articleDoc = Jsoup.connect(pageUrl).header("User-Agent", "neznen").get();
//                }
//            }

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

    }



    public void expertComment(String dbId){




    }

    public void getMemberListPageInfo(String id){



    }

}
