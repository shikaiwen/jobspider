package com.kevin.db;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.apache.commons.lang.StringUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by kaiwen on 27/02/2017.
 * http://www.runoob.com/mongodb/mongodb-query.html
 * http://www.runoob.com/mongodb/mongodb-java.html
 *
 *  vim /etc/mongod.conf
 *
 * centos
 *  service mongod start
 *  service mongod stop
 *
 * server: centOS 6.6
 *
 * vim /etc/yum.repos.d/mongodb-org-3.4.repo
     [mongodb-org-3.4]
     name=MongoDB Repository
     baseurl=https://repo.mongodb.org/yum/redhat/$releasever/mongodb-org/3.4/x86_64/
     gpgcheck=1
     enabled=1
     gpgkey=https://www.mongodb.org/static/pgp/server-3.4.asc
 *
 * sudo yum install -y mongodb-org
 *
 * install log :
 * Installed:
 mongodb-org.x86_64 0:3.4.2-1.el6
 Dependency Installed:
 mongodb-org-mongos.x86_64 0:3.4.2-1.el6
 mongodb-org-server.x86_64 0:3.4.2-1.el6
 mongodb-org-shell.x86_64 0:3.4.2-1.el6
 mongodb-org-tools.x86_64 0:3.4.2-1.el6
 *
 *
 *
 * op command:
 * show dbs
 * use dbname
 * db.COLLECTION_NAME.find()
 *
 */
public class MongoConnector {

    public static final String DB = "test";
    public static final Integer PORT = 27017;
    public static final String HOST = "172.16.2.31";

    public static final String ARTICLE_COLS = "article";
    public static final String COMMENT_COLS = "comments";
    public static final String MEMBER_COLS = "member";
    public static final String CSDN_EXPERT_COLS = "csdn_expert";

//    static Logger logger = LoggerFactory.getLogger("com.mongodb");
    static {


    }
//    Logger mongoLogger = Logger.getLogger(  );
//    mongoLogger.setLevel(Level.SEVERE);


    public static void main(String[] args) {
//        getDB("test");

        inTest();

    }

    public static void inTest(){

        // Enable MongoDB logging in general
        System.setProperty("DEBUG.MONGO", "true");
        // Enable DB operation tracing
        System.setProperty("DB.TRACE", "true");

        List list = new ArrayList();
        list.add(new ObjectId("58bfb40110a1fb3d444ece77"));
//        list.add("58bfb40110a1fb3d444ece78");
//        list.add("58bfb40110a1fb3d444ece79");
//        list.add("58bfb40110a1fb3d444ece7a");
//        list.add("58bfb40110a1fb3d444ece7b");
//            list.add("chszs");
//            list.add("xiangzhihong8");
//        Document condition = new Document("_id", new Document("$in",list));
//        Document condition = new Document("username", "FungLeo");

        Bson idQuery = Filters.in("_id", list);

//        FindIterable<Document> existedMember = getMemberCols().find(idQuery);
        MongoCursor<Document> cursor = getMemberCols().find(idQuery).iterator();

        System.out.println(cursor.hasNext());
        List namelist = new ArrayList <>();
//        existedMember.forEach((Block <? super Document>) (doc)->{
//            Object username = doc.get("username");
//            namelist.add(username);
//        });

        System.out.println(StringUtils.join(namelist.toArray()));
    }





    public static MongoCollection<Document> getArticleCols(){
        return getCollection(ARTICLE_COLS);

    }
    public static MongoCollection<Document> getCommentCols(){
        return getCollection(COMMENT_COLS);
    }

    public static MongoCollection<Document> getMemberCols(){
        return getCollection(MEMBER_COLS);
    }
    public static MongoCollection<Document> getCSDNExpertCols(){
        return getCollection(CSDN_EXPERT_COLS);
    }

    public static MongoCollection<Document> getCollection(String colName){
        MongoClient client = getClient();
        MongoDatabase database = client.getDatabase(DB);
        MongoCollection<Document> collection = database.getCollection(colName);
        return collection;
    }


    public static MongoDatabase getDB(){
        return getDB(DB);
    }

    public static MongoDatabase getDB(String db){
//        Mongo mongo = new Mongo("localhost", 27017);
        MongoClient client = new MongoClient(HOST, PORT);
        MongoDatabase database = client.getDatabase(db);
        return database;
    }

    public static MongoClient getClient(){
        MongoClient client = new MongoClient("172.16.2.31", 27017);
        return client;
    }


    public static void insertBatch(String dbName, String collectionName, List<Map> mapList) {


        MongoDatabase db = getClient().getDatabase(dbName);
        MongoCollection<org.bson.Document> collection = db.getCollection(collectionName);

        List<Document> documentList = new ArrayList<>(mapList.size());
        mapList.forEach((map)->{
            org.bson.Document document = new org.bson.Document();
            document.putAll(map);
            documentList.add(document);
        });

        collection.insertMany(documentList);


    }


    public void insert(){

//        MongoDatabase db = getDB("test");
//        MongoCollection<Document> test = db.getCollection("test");
//        Document document = new Document();
//        document.append("username", "kevin")
//                .append("age", "18");
//        test.insertOne(document);

        MongoClient client = this.getClient();
        DB db = client.getDB("test");

        BasicDBObject dbObject = new BasicDBObject();
        dbObject.append("username", "kevin")
                .append("age", "18");

        DBCollection emp = db.getCollection("emp");
        WriteResult insert = emp.insert(dbObject);


    }
}
