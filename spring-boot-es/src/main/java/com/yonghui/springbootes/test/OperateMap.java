package com.yonghui.springbootes.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yonghui.springbootes.config.EsClient;
import com.yonghui.springbootes.entity.Person;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author:lyh
 * @Description:对mapping的简单crud示例
 * @Date:Created in 2020/8/30 16:47
 */
public class OperateMap {


    RestHighLevelClient client = EsClient.getClient();

    String index = "person";
    String type = "man";

    ObjectMapper mapper = new ObjectMapper();

    /**
     * 2.1增加数据
     *
     * @throws IOException
     */
    @Test
    public void createDocTest() throws IOException {
        //  1.准备一个json数据
        Person person = new Person(1, "张三", 33, new Date());
        String json = mapper.writeValueAsString(person);
        //  2.创建一个request对象(手动指定的方式创建)
        IndexRequest request = new IndexRequest(index, type, person.getId().toString());
        request.source(json, XContentType.JSON);
        // 3.使用client 操作request对象生成doc
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        // 4.输出返回结果
        System.out.println(response.getResult().toString());

    }

    /**
     * 2.2修改数据
     *
     * @throws Exception
     */
    @Test
    public void updateDocTest() throws Exception {
        // 1.创建要跟新的Map
        Map<String, Object> doc = new HashMap<>();
        doc.put("name", "张三四");

        // 2.创建request, 将doc 封装进去
        UpdateRequest request = new UpdateRequest(index, type, "1");
        request.doc(doc);

        // 3. client 去操作 request
        UpdateResponse response = client.update(request, RequestOptions.DEFAULT);
        // 4.输出 更新结果
        System.out.println(response.getResult());
    }

    /**
     * 2.3删除文档
     *
     * @throws Exception
     */
    @Test
    public void deleteDocTest() throws Exception {
        //  1.封装删除对象
        DeleteRequest request = new DeleteRequest(index, type, "1");

        //  2 client 操作 request对象
        DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);
        //  3.输出结果
        System.out.println(response.getResult().toString());
    }

    /**
     * 2.4批量新增-如果有主键冲突会自动覆盖
     *
     * @throws Exception
     */
    @Test
    public void bulkCreateDoc() throws Exception {

        // 1.准备多个json 对象
        Person p1 = new Person(1, "张三a", 23, new Date());
        Person p2 = new Person(2, "里斯", 24, new Date());
        Person p3 = new Person(3, "王武", 24, new Date());

        String json1 = mapper.writeValueAsString(p1);
        String json2 = mapper.writeValueAsString(p2);
        String json3 = mapper.writeValueAsString(p3);

        // 2.创建request
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.add(new IndexRequest(index, type, p1.getId().toString()).source(json1, XContentType.JSON))
                .add(new IndexRequest(index, type, p2.getId().toString()).source(json1, XContentType.JSON))
                .add(new IndexRequest(index, type, p3.getId().toString()).source(json1, XContentType.JSON));

        // 3.client 执行
        BulkResponse responses = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        // 4.输出结果
        System.out.println(responses.getItems().toString());
    }


    /**
     * 批量删除
     *
     * @throws Exception
     */
    @Test
    public void bulkDelete() throws Exception {
        // 1.创建Request 对象
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.add(new DeleteRequest(index, type, "1"));
        bulkRequest.add(new DeleteRequest(index, type, "2"));
        bulkRequest.add(new DeleteRequest(index, type, "3"));
        // 2.执行
        BulkResponse re = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        // 3.输出结果
        System.out.println(re.toString());

    }
}
