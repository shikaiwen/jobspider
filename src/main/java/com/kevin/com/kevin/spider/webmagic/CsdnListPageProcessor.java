package com.kevin.com.kevin.spider.webmagic;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
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
public class CsdnListPageProcessor implements PageProcessor {

    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000).setTimeOut(10000);





    @Override
    public void process(Page page) {

        Selectable select = page.getHtml().select($(".list_item_new .list_item"));
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

    }

    @Override
    public Site getSite() {
        return site;
    }
}
