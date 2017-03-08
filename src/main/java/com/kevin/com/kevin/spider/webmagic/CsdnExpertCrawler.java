package com.kevin.com.kevin.spider.webmagic;

import com.alibaba.fastjson.JSON;
import com.kevin.db.MongoConnector;
import com.kevin.selenium.CommWebDriver;
import org.apache.commons.lang.math.NumberUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static us.codecraft.webmagic.selector.Selectors.$;

/**
 * Created by kaiwen on 28/02/2017.
 *
 * 没弄清的问题:
 * 如果在process中用将所有的结果添加到成员变量mapList中，在调试时发现数据是正常加入，但最后执行到往mongodb中
 * 插数据时发现mapList大小为0，估计是异步线程产生的闭包类似问题，但还没彻底弄清
 *
 */
public class CsdnExpertCrawler implements PageProcessor {

    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000).setTimeOut(10000);


    public static void main(String[] args) {

//        int pageCount = CommWebDriver.getCsdnExpertPageCount();
        new CsdnExpertCrawler().loadAllExpertToDB();
    }

    public List<Map> mapList = new ArrayList();

    /**
     * 加载所有csdn专家博客并存储到db
     */
    public void loadAllExpertToDB(){
        int pageCount = 2;//CommWebDriver.getCsdnExpertPageCount();
        int page = 1;
        String listUrl = "http://blog.csdn.net/peoplelist.html?channelid=0&page=";

        Spider spider = Spider.create(new CsdnExpertCrawler());//.thread(4);

        for(int i = 1 ;i <= pageCount;i++) {
            spider.addUrl(listUrl + i);
        }
//        spider.addPipeline(new DbPipeline(mapList));
        spider.run();
        MongoConnector.insertBatch("test", "csdn_expert", mapList);
    }

    @Override
    public void process(Page page) {

        Selectable select = page.getHtml().select($(".experts_list"));

        if(select == null ) return;

        List<Selectable> nodes = select.nodes();
        List <Map> arrList = new ArrayList <>();
//        nodes.forEach((item)->{
            for(int i = 0 ;i < nodes.size();i++) {
                Selectable item = nodes.get(i);

                Selectable selectable = item.select($(".expert_name"));
            String articleCnt = item.select($(".count .count_l b")).nodes().get(0).select($("b", "text")).toString();
            String viewCnt =  item.select($(".count .count_l b")).nodes().get(1).select($("b", "text")).toString();

            String address = item.select($(".address em")).select($("em", "text")).toString();
            String occupation = item.select($(".address span")).select($("span", "text")).toString();

            String blogHref = item.xpath("dt/a/@href").get();
            String iconUrl = item.xpath("dt/a/img/@src").get();
//            item.xpath("dt[1]")

            Map map = new HashMap();
            map.put("articleCnt", NumberUtils.toInt(articleCnt, 0));
            map.put("viewCnt", NumberUtils.toInt(viewCnt, 0));
            map.put("address", address);
            map.put("blogUrl", blogHref);
            map.put("iconUrl", iconUrl);
            map.put("source", "1"); // 1: from csdn
            map.put("username", blogHref.substring(blogHref.lastIndexOf("/") + 1));

            arrList.add(map);


            this.mapList.add(map);


//            System.out.println(JSON.toJSONString(CsdnExpertCrawler.this.mapList));

            }
//        page.putField("list", arrList);
//            mapList
//        });

    }

    @Override
    public Site getSite() {
        return site;
    }
}
