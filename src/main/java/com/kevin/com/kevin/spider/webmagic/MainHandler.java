package com.kevin.com.kevin.spider.webmagic;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kevin.constant.Const;
import com.kevin.entity.BlogArticle;
import com.kevin.entity.BlogMember;
import com.kevin.entity.CsdnComment;
import com.kevin.service.ArticleService;
import com.kevin.service.CommentService;
import com.kevin.service.MemberService;
import com.kevin.service.ServiceFactory;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

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

    static ArticleService articleService = ServiceFactory.getService(ArticleService.class);
    static MemberService memberService = ServiceFactory.getService(MemberService.class);
    static CommentService commentService = ServiceFactory.getService(CommentService.class);


    public static void main(String[] args) throws Exception{

//        Spider spider = Spider.create(new CsdnListPageProcessor());
//        int page = 1;
//        String listUrl = "http://blog.csdn.net/peoplelist.html?channelid=0&page=";
//        spider.run();

//        Document document = Jsoup.connect("http://blog.csdn.net/yuanmeng001")
//                .header("User-Agent","Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36").get();

        List list = Arrays.asList(1);
        System.out.println(StringUtils.join(list.subList(0, 1)));

        MainHandler mainHandler = new MainHandler();
        mainHandler.job1();
//        mainHandler.start();
//        mainHandler.job1();
//        mainHandler.job3();

    }

    /**
     * 删除所有数据重新执行
     */
    public void dropAllData(){

    }

    public void setDefaultProxy(){
        System.setProperty("http.proxySet", "true");
        System.setProperty("http.proxyHost", "127.0.0.1");
        System.setProperty("http.proxyPort", "8888");
    }


    /**
     * 总任务，从获取专家号和专家博客开始
     */
    public void job1(){

        CsdnExpertCrawler expertCrawler = new CsdnExpertCrawler();
        expertCrawler.loadAllExpertToDB();

//        MemberService memberService = MemberService.getInstance();
//        ArticleService articleService = ArticleService.getInstance();

        List<BlogMember> allExpertBlog = memberService.getAllExpertBlog();

        List <BlogArticle> articleAll = new ArrayList <>(1000);
        List <CsdnComment> commentsAll = new ArrayList <>(5000);

        for (Iterator<BlogMember> iterator = allExpertBlog.iterator(); iterator.hasNext(); ) {
            BlogMember blogMember = iterator.next();

            String username = blogMember.getUsername();

            List<BlogArticle> articleList = articleService.getAllArticleByUserName(username);

            for (Iterator<BlogArticle> articleIterator = articleList.iterator(); articleIterator.hasNext(); ) {
                BlogArticle article = articleIterator.next();
                Integer articleId = article.getArticleId();

                if (article.getCommentCount() > 0) {
                    List <CsdnComment> comentList = commentService.getCommentListByArticleId(articleId);
                    comentList.forEach((c)->{c.setCreateDate(new Date());});
                    commentsAll.addAll(comentList);
                }
            }
            articleAll.addAll(articleList);
        }

        if(CollectionUtils.isNotEmpty(articleAll)){
            articleService.saveArticle(articleAll);
        }
        if(CollectionUtils.isNotEmpty(commentsAll)){
            commentService.saveComment(commentsAll);
        }


    }

    /**
     * 从评论中获取新用户,有去重功能，不会保存已经存在的用户
     */
    public void job2(){



    }


    /**
     * 检查更新操作，更新文章信息，列表信息
     * job
     */
    public void job3(){


    }


    /**
     * 查询新爬取到的用户去处理其所有文章和评论
     */
    public void job4(){


    }





    /**
     * 更新单个用户的所有信息
     * 获取用户新增的文章
     * 更新用户所有的评论，先删除，再新增
     * @param blogMember
     */
    public void doUpdateJobByMember(BlogMember blogMember) {

        List <BlogArticle> userNewArticle = articleService.getUserNewArticle(blogMember.getUsername());
        if (CollectionUtils.isNotEmpty(userNewArticle)) {
            articleService.saveArticle(userNewArticle);
        }

        userNewArticle.forEach(article->{

            boolean b = commentService.deleteCommentByArticle(article.getArticleId());
            List <CsdnComment> comments = commentService.getCommentListByArticleId(article.getArticleId());
            commentService.saveComment(comments);

        });


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



}
