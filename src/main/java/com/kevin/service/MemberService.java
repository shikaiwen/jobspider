package com.kevin.service;

import com.alibaba.fastjson.JSON;
import com.kevin.db.MongoConnector;
import com.kevin.entity.BlogMember;
import com.kevin.utils.BeanUtils;
import com.mongodb.Block;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.QueryBuilder;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.apache.commons.collections.CollectionUtils;
import org.bson.BasicBSONObject;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolver.iterator;

/**
 * Created by kaiwen on 01/03/2017.
 *
 * https://www.mkyong.com/mongodb/java-mongodb-query-document/
 * https://docs.mongodb.com/manual/reference/method/db.collection.find/
 */
public class MemberService {


    /**
     * 保存爬取的用户名到db中，这里是多个爬虫调用，需要程序过滤掉重复的用户
     */
    public void saveUserName(List<String> userName){


    }


    public boolean saveBlogMember(List<BlogMember> blogMemberList){

        if (CollectionUtils.isEmpty(blogMemberList)) {
            return true;
        }

        MongoCollection <Document> memberCols = MongoConnector.getMemberCols();

        //过滤获取数据库中不存在的记录
        List <BlogMember> filteredMemberList = new ArrayList <>();

        List idList = new ArrayList();
        blogMemberList.forEach((blogMember -> {
            idList.add(new ObjectId(blogMember.get_id()));
        }));

        Document condition = new Document("_id", Filters.in("$in",idList) );

        FindIterable <Document> existedMember = memberCols.find(condition);
        existedMember.forEach((Block<? super Document>) (doc)->{

            String id = doc.getObjectId("_id").toString();
            if (!idList.contains(id)) {
                int indexOfId = idList.indexOf(id);
                filteredMemberList.add(blogMemberList.get(indexOfId));
            }
        });

        List<Document> documentList = new ArrayList <>();
        filteredMemberList.forEach((blogMember -> {
            Document document = new Document();
            Map map = BeanUtils.copyPropertiesToMap(blogMember);
            document.putAll(map);

        }));

        MongoConnector.getMemberCols().insertMany(documentList);
        return true;
    }



    static MemberService instance = null;
    public static MemberService getInstance(){
        if (instance == null) {
            instance = new MemberService();
        }
        return instance;
    }


    public List<BlogMember> getAllExpertBlog(){
        MongoClient client = MongoConnector.getClient();

        MongoDatabase database = client.getDatabase("test");
        MongoCollection<Document> csdn_expert = database.getCollection("csdn_expert");

        BasicBSONObject query = new BasicBSONObject();



        Document bson = new Document();
        FindIterable<Document> documents = csdn_expert.find(bson).limit(3);

        MongoCursor<Document> iter = documents.iterator();

        List<BlogMember> expertBlogList = new ArrayList<>();
        while (iter.hasNext()) {
            Document next = iter.next();
            BlogMember blogMember = JSON.parseObject(JSON.toJSONString(next), BlogMember.class);
            expertBlogList.add(blogMember);
        }

        return expertBlogList;
    }

    /**
     * 获取一个用户并开始爬取
     * 先从专家开始
     * @return
     */
    public BlogMember getMemberToStartSpider(){
//        if(true){
//            BlogMember blogMember = new BlogMember();
//            blogMember.setAddress("http://blog.csdn.net/yuanmeng001");
//            blogMember.setAddress("http://blog.csdn.net/huanghm88");
//            return  blogMember;
//        }

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
