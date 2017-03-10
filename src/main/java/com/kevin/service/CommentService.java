package com.kevin.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.kevin.constant.Const;
import com.kevin.db.MongoConnector;
import com.kevin.entity.CsdnComment;
import com.kevin.utils.BeanUtils;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import org.apache.commons.collections.CollectionUtils;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by kaiwen on 10/03/2017.
 */
public class CommentService {


    static ArticleService articleService = ServiceFactory.getService(ArticleService.class);

    /**
     * 根据文章去获取评论
     * csdn会返回所有的评论
     * http://blog.csdn.net/huanghm88/comment/list/3965218?page=1
     * 通过测试发现url中的用户名对返回结果没影响(只要用户存在即可)，也就是说csdn其实是根据articleId去获取评论的
     */
    public List<CsdnComment> getCommentListByArticleId(Integer articleId){

        String commentUrl = "http://blog.csdn.net/shikaiwencn/comment/list/"+articleId+"?page=1";
        List <CsdnComment> comments = new ArrayList<>();
        try {
            String body = Jsoup.connect(commentUrl)
                    .header("User-Agent","zeneyang")
                    .ignoreContentType(true)
                    .execute().body();
            JSONObject parse = (JSONObject) JSON.parse(body);
            if (parse == null) {
                return comments;
            }

            List<Map> commentList = (List<Map>)parse.get("list");
            Type type = new TypeReference<List<CsdnComment>>(){}.getType();
            comments = JSON.parseObject(JSON.toJSONString(commentList), type);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return comments == null ? new ArrayList <>() : comments;
    }


    /**
     * 查询用来获取用户名的评论
     * authorExtracted=0的记录
     */
    public List<CsdnComment> getCommentToExtractUser(int count){

        List <CsdnComment> commentList = new ArrayList <>();
        MongoCollection<Document> commentCols = MongoConnector.getCommentCols();
        org.bson.Document bson = new org.bson.Document();

        int startVersion = 0;

        while(startVersion < 5){
            bson.put("version", startVersion);
            FindIterable<Document> documents = commentCols.find(bson).limit(count);

            if(documents.iterator().hasNext()){
                documents.forEach((Block<? super Document>) (doc)->{
                    CsdnComment c = new CsdnComment();

                    String s = JSON.toJSONString(doc, new ValueFilter() {
                        @Override
                        public Object process(Object object, String name, Object value) {
                            if("_id".equals(name) && value instanceof ObjectId){
                                return value.toString();
                            }
                            return value;
                        }
                    });

                    CsdnComment csdnComment = JSON.parseObject(s, CsdnComment.class);

                    commentList.add(csdnComment);

                });
                break;
            }else{
                startVersion ++;
            }
        }

        if (CollectionUtils.isNotEmpty(commentList)) {
//            upgradeVersion(commentList,CsdnComment.class);
        }

        return commentList;

    }

    /**
     * 根据文章删除评论
     * @param articleId
     * @return
     */
    public boolean deleteCommentByArticle(Integer articleId){

        MongoCollection <Document> commentCols = MongoConnector.getCommentCols();
        DeleteResult deleteResult = commentCols.deleteMany(new Document("ArticleId", articleId));
        return deleteResult.wasAcknowledged();

    }

    /**
     * save comments to db
     * @param articleList
     */
    public void saveComment(List <CsdnComment> commentList) {

        MongoCollection <org.bson.Document> collection = MongoConnector.getCommentCols();

        List <org.bson.Document> docList = new ArrayList <>();
        commentList.forEach((comment)->{
            org.bson.Document doc = new org.bson.Document();
            Map stringObjectMap = BeanUtils.copyPropertiesToMap(comment, Const.DB_ID);
            doc.putAll(stringObjectMap);
            docList.add(doc);
        });

        collection.insertMany(docList);
    }

}
