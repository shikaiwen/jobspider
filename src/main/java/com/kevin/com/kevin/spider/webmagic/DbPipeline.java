package com.kevin.com.kevin.spider.webmagic;

import com.alibaba.fastjson.JSON;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.List;
import java.util.Map;

/**
 * Created by kaiwen on 08/03/2017.
 */
public class DbPipeline implements Pipeline {

    private List<Map> resultHolder = null;

    public DbPipeline(List <Map> resultHolder) {
        this.resultHolder = resultHolder;
    }

    @Override
    public void process(ResultItems resultItems, Task task) {

        String s = JSON.toJSONString(resultItems);
        System.out.println(s);
        List<Map> list = (List <Map>) resultItems.get("list");
        resultHolder.addAll(list);

    }

}
