package com.kevin.db;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

/**
 * Created by kaiwen on 27/02/2017.
 */
public class MongoConnector {

    public static MongoDatabase getDB(String db){
//        Mongo mongo = new Mongo("localhost", 27017);
        MongoClient client = new MongoClient("localhost", 27017);
        MongoDatabase database = client.getDatabase(db);
        return database;
    }


    public void insert(){
        MongoDatabase test = getDB("test");

        MongoCollection<Document> province = test.getCollection("province");

    }
}
