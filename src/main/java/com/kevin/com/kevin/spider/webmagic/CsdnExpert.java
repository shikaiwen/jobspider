package com.kevin.com.kevin.spider.webmagic;

import com.kevin.db.MongoConnector;
import com.kevin.selenium.WebDriverTest;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static us.codecraft.webmagic.selector.Selectors.$;

/**
 * Created by kaiwen on 28/02/2017.
 */
public class CsdnExpert implements PageProcessor {

    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000).setTimeOut(10000);


    public static void main(String[] args) {


        Spider spider = Spider.create(new CsdnExpert());

        int pageCount = WebDriverTest.getPageCount();


        int page = 1;
        String listUrl = "http://blog.csdn.net/peoplelist.html?channelid=0&page=";

        for(int i = 1 ;i <= pageCount;i++) {

            spider.addUrl(listUrl + i);
        }

        spider.run();
    }

    @Override
    public void process(Page page) {

        Selectable select = page.getHtml().select($(".experts_list"));

        if(select == null ) return;

        List<Selectable> nodes = select.nodes();

        List<Map> mapList = new ArrayList();
        nodes.forEach((item)->{

            Selectable selectable = item.select($(".expert_name"));
            String articleCnt = item.select($(".count .count_l b")).nodes().get(0).select($("b", "text")).toString();
            String viewCnt =  item.select($(".count .count_l b")).nodes().get(1).select($("b", "text")).toString();


            String address = item.select($(".address em")).select($("em", "text")).toString();
            String occupation = item.select($(".address span")).select($("span", "text")).toString();

            String blogHref = item.xpath("dt/a/@href").get();
            String iconUrl = item.xpath("dt/a/img/@src").get();
//            item.xpath("dt[1]")

            Map map = new HashMap();
            map.put("article_cnt", articleCnt);
            map.put("view_cnt", viewCnt);
            map.put("address", address);
            map.put("blog_url", blogHref);
            map.put("icon_url", iconUrl);
            map.put("source", "1"); // 1: from csdn

            mapList.add(map);
        });


        MongoConnector.insertBatch("test", "csdn_expert", mapList);

        System.out.println(select);
    }

    @Override
    public Site getSite() {
        return site;
    }
}
