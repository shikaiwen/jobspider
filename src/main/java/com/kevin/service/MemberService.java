package com.kevin.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.kevin.db.MongoConnector;
import com.kevin.entity.BlogArticle;
import com.kevin.entity.BlogMember;
import com.kevin.entity.CsdnComment;
import com.kevin.utils.BeanUtils;
import com.kevin.utils.MappingUtil;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.collections.CollectionUtils;
import org.bson.BasicBSONObject;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

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
     * 保存爬取的用户名到db中，这里是多个爬虫调用，需要程序过滤掉重复的用户
     */
    public void saveUserName(List<String> userName){


    }

    /**
     * 批量保存会员信息，会先根据username过滤掉db中存在记录
     * @param blogMemberList
     * @return
     */
    public boolean saveBlogMember(List<BlogMember> blogMemberList){

        if (CollectionUtils.isEmpty(blogMemberList)) {
            return true;
        }

        MongoCollection <Document> memberCols = MongoConnector.getMemberCols();

        //过滤获取数据库中不存在的记录
        List <BlogMember> filteredMemberList = new ArrayList <>();

        List usernameList = new ArrayList();
        blogMemberList.forEach((blogMember -> {
            usernameList.add(blogMember.getUsername());
        }));

        Document condition = new Document("username",  new Document("$in",usernameList) );
        FindIterable <Document> documents = memberCols.find(condition);

        MongoCursor <Document> existedMember = memberCols.find(condition).iterator();


        if (!existedMember.hasNext()) {
            filteredMemberList.addAll(blogMemberList);
        }else{
            for(;existedMember.hasNext();) {
                Document doc = existedMember.next();
                String username = (String)doc.get("username");
                if (!usernameList.contains(username)) {
                    int indexOfUsername = usernameList.indexOf(username);
                    filteredMemberList.add(blogMemberList.get(indexOfUsername));
                }
            }
        }

        List<Document> documentList = new ArrayList <>();
        filteredMemberList.forEach((blogMember -> {
            Document document = new Document();
            Map map = BeanUtils.copyPropertiesToMap(blogMember, "_id");
            document.putAll(map);
            documentList.add(document);
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


    /**
     * 获取新用户
     * version=0
     */
    public List<BlogMember> getNewMember(int maxCount){

        List <BlogMember> memberList = new ArrayList <>();
        MongoCollection <org.bson.Document> memberCols = MongoConnector.getMemberCols();
        org.bson.Document bson = new org.bson.Document();

        int startVersion = 0;

        bson.put("version", startVersion);
        FindIterable <org.bson.Document> documents = memberCols.find(bson).limit(maxCount);
        if(documents.iterator().hasNext()){
            documents.forEach((Block<? super org.bson.Document>) (doc)->{
                BlogMember c = new BlogMember();

                String s = JSON.toJSONString(doc, new ValueFilter() {
                    @Override
                    public Object process(Object object, String name, Object value) {
                        if("_id".equals(name) && value instanceof ObjectId){
                            return value.toString();
                        }
                        return value;
                    }
                });

                BlogMember blogMember = JSON.parseObject(s, BlogMember.class);
                memberList.add(blogMember);

            });
        }

        return memberList;

    }


    /**
     * 获取增量爬取的用户
     */
    public List<BlogMember> getMemberToUpdate(int count){

        if(count <= 0) count = 1;

        List <BlogMember> resultList = new ArrayList <>();

        MongoCollection <org.bson.Document> memberCols = MongoConnector.getMemberCols();
        org.bson.Document bson = new org.bson.Document();

        int startVersion = 0;

        while(startVersion < 5){
            bson.put("version", startVersion);
            MongoCursor <Document> cursor = memberCols.find(bson).limit(count).iterator();

            if (cursor.hasNext()) {
                resultList.addAll(MappingUtil.documentArticleMapping(cursor, BlogMember.class));
                break;
            }else{
                startVersion ++;
            }
        }

        return resultList;

    }


}
