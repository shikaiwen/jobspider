package com.kevin.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.kevin.entity.BlogArticle;
import com.kevin.entity.BlogMember;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaiwen on 10/03/2017.
 */
public class MappingUtil {


    /**
     * 映射对象和对应的Document
     * @param documents
     * @return
     */
    public static <T> List<T> documentArticleMapping(MongoCursor<Document> documents, Class<T> t) {

        List <T> reseultList = new ArrayList<>();

        if(!documents.hasNext()) return reseultList;

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

            T m = JSON.parseObject(s, (Class <T>) t.getClass());
            reseultList.add(m);

        });
        return reseultList;
    }

}
