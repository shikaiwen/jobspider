package com.kevin.spider;

import com.alibaba.fastjson.JSON;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.*;

/**
 * Created by root on 2/26/2017.
 */
public class ProvinceCityCountySpider {

    public static void main(String[] args) {
        ProvinceCityCountySpider spider = new ProvinceCityCountySpider();
        Map map = spider.getProvince().get(0);
        spider.getCityList(map.get("nextUrl").toString());
    }

    public Document getDocument(String url){
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        Elements newsHeadlines = doc.select("#mp-itn b a");

        return doc;

    }

    public void getCity(String url){

    }


    public List getVillageList(String url) {

        List countyList = new ArrayList();
        Document document = getDocument(url);
        Elements cityTrs = document.select(".villagetable .villagetr");
        for (Iterator<Element> iterator = cityTrs.iterator(); iterator.hasNext(); ) {
            Element next = iterator.next();
            Element codeTd = next.child(0);
            String code = codeTd.child(0).text();

            Element nameTd = next.child(1);

            String name = nameTd.child(0).text();
            String href = nameTd.child(0).attr("href");
            String nextUrl = getUrlPath(url) + href;

            Map map = new HashMap();
            map.put("name", name);
            map.put("code", code);
            map.put("nextUrl", nextUrl);

            countyList.add(map);

        }

        return countyList;
    }

    public List getTownList(String url) {

        List countyList = new ArrayList();
        Document document = getDocument(url);
        Elements cityTrs = document.select(".towntable .towntr");
        for (Iterator<Element> iterator = cityTrs.iterator(); iterator.hasNext(); ) {
            Element next = iterator.next();
            Element codeTd = next.child(0);
            String code = codeTd.child(0).text();

            Element nameTd = next.child(1);

            String name = nameTd.child(0).text();
            String href = nameTd.child(0).attr("href");
            String nextUrl = getUrlPath(url) + href;

            Map map = new HashMap();
            map.put("name", name);
            map.put("code", code);
            map.put("nextUrl", nextUrl);

            countyList.add(map);

        }

        return countyList;
    }

    public List getCountyList(String url) {

        List countyList = new ArrayList();
        Document document = getDocument(url);
        Elements cityTrs = document.select(".countytable .countytr");
        for (Iterator<Element> iterator = cityTrs.iterator(); iterator.hasNext(); ) {
            Element next = iterator.next();
            Element codeTd = next.child(0);
            String code = codeTd.child(0).text();

            Element nameTd = next.child(1);

            String name = nameTd.child(0).text();
            String href = nameTd.child(0).attr("href");
            String nextUrl = getUrlPath(url) + href;

            Map map = new HashMap();
            map.put("name", name);
            map.put("code", code);
            map.put("nextUrl", nextUrl);

            countyList.add(map);

        }

        return countyList;
    }




    public List getCityList(String url) {

        List cityList = new ArrayList();
        Document document = getDocument(url);
        Elements cityTrs = document.select(".citytable .citytr");
        for (Iterator<Element> iterator = cityTrs.iterator(); iterator.hasNext(); ) {
            Element next = iterator.next();
            Element codeTd = next.child(0);
            String code = codeTd.child(0).text();

            Element nameTd = next.child(1);

            String name = nameTd.child(0).text();
            String href = nameTd.child(0).attr("href");
            String nextUrl = getUrlPath(url) + href;

            Map map = new HashMap();
            map.put("name", name);
            map.put("code", code);
            map.put("nextUrl", nextUrl);

            cityList.add(map);

        }

        return cityList;
    }


    public List<Map> getProvince(){
        String allUrl = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2015/index.html";
        Document document = getDocument(allUrl);
        Elements select = document.select(".provincetable .provincetr td");

        List<Map> provList = new ArrayList<>();
        for(Iterator iter = select.iterator();iter.hasNext();) {
            Element next = (Element) iter.next();
            Element aLink = next.child(0);

            String href = aLink.attr("href");
            Map map = new HashMap<>();
            map.put("name", aLink.text());
            String baseUrl =
                    allUrl.lastIndexOf("/") == allUrl.length() - 1 ? allUrl : allUrl.substring(0,allUrl.lastIndexOf("/")+1);
            map.put("nextUrl", baseUrl + href);
            provList.add(map);
        }

//        System.out.println(JSON.toJSONString(provList));
        return provList;
    }

    String getUrlPath(String allUrl){

        String str =  allUrl.lastIndexOf("/")
                == allUrl.length() - 1 ? allUrl : allUrl.substring(0,allUrl.lastIndexOf("/")+1);
        return str;
    }

}
