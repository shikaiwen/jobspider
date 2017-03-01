package com.kevin.com.kevin.spider.webmagic;

import org.apache.commons.collections.CollectionUtils;
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
//        String listUrl =
        spider.addUrl(startUrl);
        spider.run();
    }

    @Override
    public void process(Page page) {

        if (page.getRequest().getUrl().equals(startUrl)) {

            Document document = page.getHtml().getDocument();

            Elements select = document.select("#hotarticls2 .itemlist li");
            if (CollectionUtils.isEmpty(select))  return;
            String href = select.get(0).select("a").attr("href");
            page.addTargetRequest(href);
        }else{


            Document document = page.getHtml().getDocument();
            Elements commentList = document.select("#newcomments itemlist");//.select(".comment_item");
            commentList.forEach((elt)->{

                String username = elt.select(".username").text();
                String ptime = elt.select(".ptime").text();
                String commentBody = elt.select(".comment_body").text();

                String s = username + ", " + ptime + ", " + commentBody;
                System.out.println(s);

            });

        }








//        page.getHtml().$("#comment_list .comment_item");

    }

    @Override
    public Site getSite() {
        return site;
    }

}
