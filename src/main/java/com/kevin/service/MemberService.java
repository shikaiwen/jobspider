package com.kevin.service;

import com.alibaba.fastjson.JSON;
import com.kevin.db.MongoConnector;
import com.kevin.entity.BlogMember;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.BasicBSONObject;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by kaiwen on 01/03/2017.
 *
 * https://www.mkyong.com/mongodb/java-mongodb-query-document/
 * https://docs.mongodb.com/manual/reference/method/db.collection.find/
 */
public class MemberService {


    /**
     * 获取一个用户并开始爬取
     * 先从专家开始
     * @return
     */
    public BlogMember getMemberToStartSpider(){

        MongoClient client = MongoConnector.getClient();


        MongoDatabase database = client.getDatabase("test");
        MongoCollection<Document> csdn_expert = database.getCollection("csdn_expert");

        BasicBSONObject query = new BasicBSONObject();
        query.put("crawlCnt", 0);

        FindIterable<Document> documents = csdn_expert.find((Bson) query).limit(1);

        MongoCursor<Document> iter = documents.iterator();

        if (iter.hasNext()) {
            Document next = iter.next();
            BlogMember blogMember = JSON.parseObject(JSON.toJSONString(next), BlogMember.class);
            return  blogMember;
        }

        return null;

//        for(MongoCursor<Document> iterator = documents.iterator(); iterator.hasNext() ;) {
//            Document next = iterator.next();
//        }


    }

    /**
     * 保存爬取到的评论
     */
    public void saveComment(List<Map> commentList) {

        MongoClient client = MongoConnector.getClient();
        MongoDatabase database = client.getDatabase("test");
        MongoCollection<Document> commentCollection = database.getCollection("csdn_comment");

        List<Document> saveDocList = new ArrayList<>();
        commentList.forEach((cmt)->{
//            BasicBSONObject doc = new BasicBSONObject();
            Document doc = new Document();
            doc.putAll(cmt);
            saveDocList.add(doc);
        });

        commentCollection.insertMany(saveDocList);

    }


}
