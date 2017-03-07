package com.kevin.com.kevin.spider.webmagic;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * Created by root on 3/1/2017.
 */
public class CsdnBlogDetailProcessor implements PageProcessor {

    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000).setTimeOut(10000);

    static String startUrl = "http://blog.csdn.net/huanghm88";

    public static void main(String[] args) {

        Spider spider = Spider.create(new CsdnBlogDetailProcessor());

        spider.addUrl(startUrl);
        spider.run();
    }

    @Override
    public void process(Page page) {

        if (page.getRequest().getUrl().equals(startUrl)) {

            Document document = page.getHtml().getDocument();

            Elements select = document.select("#hotarticls2 .itemlist li");
            if (CollectionUtils.isEmpty(select))  return;

            //第一个评论最多
            String href = select.get(0).select("a").attr("href");
            String countTxt = select.get(0).select("span").text();
//            countTxt.replace("(", "").replace(")","");
            String commentCnt = countTxt.replaceAll("[\\(\\)]", "");

            int i = NumberUtils.toInt(commentCnt, 0);
            if(i == 0) return;

            //href格式为文章链接：/huanghm88/article/details/3965218
            //获取评论有问题，直接通过url访问：http://blog.csdn.net/huanghm88/comment/list/3965218?page=1
            String commentHref = href.replace("article/details","comment/list");

            page.addTargetRequest(commentHref);
        }else{


            //获取到的评论是个json
//            Html.create("").jsonPath()
            String content = page.getRawText();
            JSONObject parse = (JSONObject)JSON.parse(page.getRawText());
            JSONArray commentList = (JSONArray)parse.get("list");

            page.putField("commentItem",commentList);


        }

    }

    @Override
    public Site getSite() {
        return site;
    }

}
