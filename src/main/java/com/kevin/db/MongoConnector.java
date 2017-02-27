package com.kevin.db;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

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

    public static void main(String[] args) {
//        getDB("test");
        new MongoConnector().insert();
    }

    public static MongoDatabase getDB(String db){
//        Mongo mongo = new Mongo("localhost", 27017);
        MongoClient client = new MongoClient("172.16.2.31", 27017);
        MongoDatabase database = client.getDatabase(db);
        return database;
    }

    public MongoClient getClient(){
        MongoClient client = new MongoClient("172.16.2.31", 27017);
        return client;
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
