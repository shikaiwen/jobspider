package com.kevin.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.kevin.constant.Const;
import com.kevin.utils.BeanUtils;
import com.kevin.db.MongoConnector;
import com.kevin.entity.BlogArticle;
import com.kevin.entity.CsdnComment;
import com.kevin.utils.JsoupOk;
import com.mongodb.client.MongoCollection;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
     * save comments to db
     * @param articleList
     */
    public void saveComment(List <CsdnComment> commentList) {

        MongoCollection <org.bson.Document> collection = MongoConnector.getCommentCols();

        List <org.bson.Document> docList = new ArrayList <>();
        commentList.forEach((comment)->{
            org.bson.Document doc = new org.bson.Document();
            Map stringObjectMap = BeanUtils.copyPropertiesToMap(comment,Const.DB_ID);
            doc.putAll(stringObjectMap);
            docList.add(doc);
        });

        collection.insertMany(docList);
    }


    public List<BlogArticle> getAllArticleByUserName(String username){

        List<BlogArticle> articleList = new ArrayList<>();

        String listUrl = "http://blog.csdn.net/"+username;

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
        int count = NumberUtils.toInt(recordCnt, 0);
        String getAllListUrl = listUrl + "/article/list/" + count;

        Document articleDoc = JsoupOk.getDocumentWithRetry(getAllListUrl);

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
            try {
                article.setCreateDate(DateUtils.parseDate(createDateTxt, "yyyy-MM-dd HH:mm"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            article.setViewCount(NumberUtils.toInt(viewCntStr, 0));
            article.setCommentCount(NumberUtils.toInt(commentCntStr, 0));
            articleList.add(article);
        });


//            if(select == null || select.size() == 0) return;
//            int maxPage = 1;
//            int currentpage = 1;
//            if(select != null){
//                //获取文章页数
//                Element lastpage = select.get(select.size() - 1);
//                String href = lastpage.attr("href");
//                String lastPageStr = href.substring(href.lastIndexOf("/") + 1);
//                maxPage = NumberUtils.toInt(lastPageStr, 1);
//            }
//            while (currentpage <= maxPage) {
//                String pageUrl = listUrl + "/article/list/" + currentpage;
//                Document articleDoc = null;
//                if (currentpage == 1) {
//                    articleDoc = document;
//                } else {
//                    articleDoc = Jsoup.connect(pageUrl).header("User-Agent", "neznen").get();
//                }
//            }

        return articleList;
    }


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

}
