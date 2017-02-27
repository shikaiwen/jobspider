package com.kevin.spider;

import com.kevin.db.MongoConnector;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.lang3.StringUtils;
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
//        Map map = spider.getProvince().get(0);
//        spider.getCityList(map.get("nextUrl").toString());

        spider.insertData();
    }


    public void insertData(){

        MongoConnector connector = new MongoConnector();
        List<Map> province = getProvince();

        MongoDatabase test = connector.getClient().getDatabase("test");
        MongoCollection<org.bson.Document> regionCol = test.getCollection("region");

//        List provinceDocList = new ArrayList();
//        province.forEach((item)->{
//            org.bson.Document doc = new org.bson.Document();
//            doc.putAll(item);
//            doc.put("type", 1);
//            provinceDocList.add(doc);
//        });

        List proDocList = new ArrayList();
        List cityDocList = new ArrayList();
        List countyDocList = new ArrayList();
        List townDocList = new ArrayList();
        List villageDocList = new ArrayList();


        province.forEach((promap)->{
            org.bson.Document doc = new org.bson.Document();
            doc.put("type", 1);
            doc.putAll(promap);
            proDocList.add(doc);

            String nextUrl =  (String)promap.get("nextUrl");
            if(StringUtils.isEmpty(nextUrl)) return;

            List<Map> cityList = getCityList(nextUrl);
            cityList.forEach((cityMap)->{

                org.bson.Document doc2 = new org.bson.Document();
                doc2.putAll(cityMap);
                doc2.put("type", 2);
                doc2.put("parent", promap.get("code"));
                cityDocList.add(doc2);

                String cityNextUrl =  (String)cityMap.get("nextUrl");
                if(StringUtils.isEmpty(cityNextUrl)) return;
                List<Map> countyList = getCountyList(cityNextUrl);
                countyList.forEach((countyMap)->{

                    org.bson.Document doc3 = new org.bson.Document();
                    doc3.putAll(countyMap);
                    doc3.put("type", 3);
                    doc3.put("parent", cityMap.get("code"));
                    countyDocList.add(doc3);

                    String countyNextUrl =  (String)countyMap.get("nextUrl");
                    if(StringUtils.isEmpty(countyNextUrl)) return;
                    List<Map> townList = getTownList(countyNextUrl);

                    townList.forEach((townMap)->{


                        org.bson.Document doc4 = new org.bson.Document();
                        doc4.putAll(townMap);
                        doc4.put("type", 4);
                        doc4.put("parent", countyMap.get("code"));
                        townDocList.add(doc4);

                        String townNextUrl =  (String)townMap.get("nextUrl");
                        if(StringUtils.isEmpty(townNextUrl)) return;
                        List<Map> villageList = getVillageList(townNextUrl);

                        villageList.forEach((villageMap)->{

                            org.bson.Document doc5 = new org.bson.Document();
                            doc5.putAll(villageMap);

                            doc5.put("type", 5);
                            doc5.put("parent", townMap.get("code"));
                            villageDocList.add(doc5);

                        });

                    });

                });

            });

        });


//        regionCol.insertMany(provinceDocList);

        regionCol.insertMany(proDocList);
        regionCol.insertMany(cityDocList);
        regionCol.insertMany(countyDocList);
        regionCol.insertMany(townDocList);
        regionCol.insertMany(villageDocList);
    }


    public Document getDocument(String url){
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
            return getDocument(url);
        }
//        Elements newsHeadlines = doc.select("#mp-itn b a");

        return doc;

    }

    public void getCity(String url){

    }


    public List<Map> getVillageList(String url) {

        List countyList = new ArrayList();
        Document document = getDocument(url);
        Elements cityTrs = document.select(".villagetable .villagetr");
        for (Iterator<Element> iterator = cityTrs.iterator(); iterator.hasNext(); ) {
            Element tr = iterator.next();
            String code = tr.child(0).text();
            String name = tr.child(2).text();

            Map map = new HashMap();
            map.put("name", name);
            map.put("code", code);
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

    public List<Map> getCountyList(String url) {

        List countyList = new ArrayList();
        Document document = getDocument(url);
        Elements cityTrs = document.select(".countytable .countytr");
        for (Iterator<Element> iterator = cityTrs.iterator(); iterator.hasNext(); ) {

            System.out.println(url);

            Element next = iterator.next();
//            Element codeTd = next.child(0);
            if(next.children().size()<2) continue;
            String code = next.child(0).text();
            Element nameTd = next.child(1);


            if(nameTd.children().size() == 0) continue;

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




    public List<Map> getCityList(String url) {

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
