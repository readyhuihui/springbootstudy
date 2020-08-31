package com.yonghui.springbootes.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yonghui.springbootes.config.EsClient;
import com.yonghui.springbootes.entity.Person;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;

public class Demo2 {

    RestHighLevelClient client =  EsClient.getClient();

    String index = "person";
    String type="man";

    ObjectMapper mapper = new ObjectMapper();
    @Test
    public void createDocTest() throws IOException {
        //  1.准备一个json数据
        Person person  = new Person(1,"张三",33,new Date());
        String json = mapper.writeValueAsString(person);
        //  2.创建一个request对象(手动指定的方式创建)
        IndexRequest request = new IndexRequest(index,type,person.getId().toString());
        request.source(json, XContentType.JSON);
        // 3.使用client 操作request对象生成doc
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        // 4.输出返回结果
        System.out.println(response.getResult().toString());

    }

}