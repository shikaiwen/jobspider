package com.kevin.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.kevin.constant.Const;
import com.kevin.db.MongoConnector;
import com.kevin.entity.BlogArticle;
import com.kevin.entity.CsdnComment;
import com.kevin.utils.BeanUtils;
import com.kevin.utils.JsoupOk;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.kevin.constant.Const.CSDN_ARTICLE_LIST_PAGE_URL;

/**
 * Created by kaiwen on 07/03/2017.
 */
public class ArticleService {


    private static ArticleService instance = null;

    public static ArticleService getInstance() {

        if (instance == null) {
            instance =  new ArticleService();
        }
        return instance;
    }

    /**
     * save articles to db
     * @param articleList
     */
    public void saveArticle(List <BlogArticle> articleList) {

        MongoCollection <org.bson.Document> collection = MongoConnector.getArticleCols();

        List <org.bson.Document> docList = new ArrayList <>();

        articleList.forEach((comment)->{
            org.bson.Document doc = new org.bson.Document();
            Map stringObjectMap = BeanUtils.copyPropertiesToMap(comment, Const.DB_ID);
            doc.putAll(stringObjectMap);
            docList.add(doc);
        });

        collection.insertMany(docList);

    }




    /**
     * 通用更新版本号
     * @param objectList
     * @param objType
     */
    public void upgradeVersion(List objectList,Class objType){

        if (CsdnComment.class.equals(objType)) {

            List <org.bson.Document> documentList = new ArrayList <>();
            List idList = new ArrayList();
            objectList.forEach((obj)->{

                CsdnComment comment = (CsdnComment)obj;
//                comment.setVersion(comment.getVersion() + 1);
//                org.bson.Document doc = new org.bson.Document();
//                doc.putAll(BeanUtils.copyPropertiesToMap(comment));
//                documentList.add(doc);
                idList.add(new ObjectId(comment.get_id()));
            });


            MongoCollection <org.bson.Document> commentCols = MongoConnector.getCommentCols();
//            QueryBuilder builder = new QueryBuilder();
            Bson query = Filters.in("_id", idList);
//            org.bson.Document queryDoc = new org.bson.Document();


            org.bson.Document updateDoc = new org.bson.Document();
            updateDoc.put("$inc",new org.bson.Document("version", 1));

            commentCols.updateMany(query, updateDoc);

        }

    }

    /**
     * 获取用户的新文章
     * @param username
     * @return
     */
    public List<BlogArticle> getUserNewArticle(String username){

        List<BlogArticle> resultList = new ArrayList<>();

        List <BlogArticle> articleByPage = getArticleByPage(username, 1);


        org.bson.Document document = new org.bson.Document();
        document.put("$eq", new org.bson.Document("username", username));


        MongoCollection <org.bson.Document> articleCols = MongoConnector.getArticleCols();
        MongoCursor <org.bson.Document> dateSortCurosr = articleCols.find().sort(new org.bson.Document("createDate", -1)).limit(1).iterator();

        List <BlogArticle> dbArticleList = documentArticleMapping(dateSortCurosr);
        BlogArticle dbArticle = dbArticleList.get(0);


        int eqIndex = -1;
        for (int i = 0; i < articleByPage.size(); i++) {
            BlogArticle siteArticle = articleByPage.get(i);
            if(dbArticle.getCreateDate().equals(siteArticle.getCreateDate())){
                eqIndex = i;
            }
        }

        if (eqIndex != -1) {
            resultList.addAll(articleByPage.subList(0, eqIndex + 1));
        }

        return resultList;
    }

    /**
     * 映射BlogArticle和对应的Document
     * @param documents
     * @return
     */
    public List <BlogArticle> documentArticleMapping(MongoCursor <org.bson.Document> documents) {

        List <BlogArticle> articleList = new ArrayList <>();


        if(!documents.hasNext()) return articleList;

        documents.forEachRemaining((doc)->{
            BlogArticle c = new BlogArticle();

            String s = JSON.toJSONString(doc, new ValueFilter() {
                @Override
                public Object process(Object object, String name, Object value) {
                    if ("_id".equals(name) && value instanceof ObjectId) {
                        return value.toString();
                    }
                    return value;
                }
            });

            BlogArticle article = JSON.parseObject(s, BlogArticle.class);
            articleList.add(article);

        });
        return articleList;
    }


    /**
     * 获取一个用户的所有文章
     * @param username
     * @return
     */
    public List<BlogArticle> getAllArticleByUserName(String username){

        List<BlogArticle> articleList = new ArrayList<>();

        String listUrl = Const.CSDN_BLOG_LIST_DOMAIN+username;

        Document document = JsoupOk.getDocumentWithRetry(listUrl);
        if(document == null) return articleList;

        Elements select = document.select("#papelist a");

        String pageInfoStr = document.select("#papelist span").text();
        Matcher matcher1 = Pattern.compile("(.*)条").matcher(pageInfoStr);
        String recordCnt = "";
        if(matcher1.find()){
            recordCnt = matcher1.group(1).trim();
        }

        /**
         * 这里有一个hack方式，可以将pagesize设置为一个大于实际页数的，csdn会返回所有的文章
         */
        int pageNo = NumberUtils.toInt(recordCnt, 0);
        articleList = getArticleByPage(username, pageNo);

        return articleList;
    }


    /**
     * 按页数获取文章列表
     * http://blog.csdn.net/shikaiwencn/article/list/1
     */
    public List<BlogArticle> getArticleByPage(String username,int pageNo){

        List<BlogArticle> articleList = new ArrayList<>();
        String pageUrl = CSDN_ARTICLE_LIST_PAGE_URL.replace("username", username).replace("pageNo", pageNo+"");
        Document articleDoc = JsoupOk.getDocumentWithRetry(pageUrl);

        Elements articleElts = articleDoc.select(".list_item_new .list_item");
        articleElts.forEach((item)->{
            Elements titleElt = item.select(".link_title a");
            String articleUrl = titleElt.attr("href");
            String articleIdStr = articleUrl.substring(articleUrl.lastIndexOf("/") + 1);
            String title = titleElt.text();

            String createDateTxt = item.select(".link_postdate").text();
            String viewCountHtmlText = item.select(".link_view").text();
            String viewCntStr = "";
            Matcher matcher = Pattern.compile("(\\()([0-9]+)\\)").matcher(viewCountHtmlText);
            if(matcher.find()){
                viewCntStr = matcher.group(2);
            }

            String commentCntStr = "";
            Matcher commentCntMatcher = Pattern.compile("(\\()([0-9]+)\\)").matcher(item.select(".link_comments").text());
            if(commentCntMatcher.find()){
                commentCntStr = commentCntMatcher.group(2);
            }

            BlogArticle article = new BlogArticle();
            article.setArticleId(NumberUtils.toInt(articleIdStr));
            article.setTitle(title);
            article.setUsername(username);
            try {
                article.setCreateDate(DateUtils.parseDate(createDateTxt, "yyyy-MM-dd HH:mm"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            article.setViewCount(NumberUtils.toInt(viewCntStr, 0));
            article.setCommentCount(NumberUtils.toInt(commentCntStr, 0));
            articleList.add(article);
        });

        return articleList;
    }





    /**
     * 从数据库获取用户文章
     */


}
