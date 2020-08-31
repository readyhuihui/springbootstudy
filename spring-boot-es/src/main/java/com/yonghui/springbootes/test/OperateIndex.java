package com.yonghui.springbootes.test;

import com.yonghui.springbootes.config.EsClient;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.junit.Test;

import java.io.IOException;

/**
 * @Author:lyh
 * @Description:对index的操作示例
 * @Date:Created in 2020/8/30 16:46
 */
public class OperateIndex {

    RestHighLevelClient client = EsClient.getClient();

    String index = "person";
    String type = "man";

    /**
     * 1.1创建索引
     * @throws Exception
     */
    @Test
    public void createIndx() throws Exception {
        try {
            // 1.准备关于索引的setting
            Settings.Builder settings = Settings.builder()
                    .put("number_of_shards", 2)
                    .put("number_of_replicas", 1);

            // 2.准备关于索引的mapping
            XContentBuilder mappings = JsonXContent.contentBuilder()
                    .startObject()
                    .startObject("properties")
                    .startObject("name")
                    .field("type", "text")
                    .field("analyzer", "ik_max_word")
                    .endObject()
                    .startObject("age")
                    .field("type", "integer")
                    .endObject()
                    .startObject("birthday")
                    .field("type", "date")
                    .field("format", "yyyy-MM-dd")
                    .endObject()
                    .endObject()
                    .endObject();
            // 3.将settings和mappings 封装到到一个Request对象中
            CreateIndexRequest request = new CreateIndexRequest(index)
                    .settings(settings)
                    .mapping(type, mappings);
            // 4.使用client 去连接ES
            CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);

            System.out.println("response:" + response.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client.close();
        }
    }

    /**
     * 1.2判断索引是否存在
     *
     * @throws IOException
     */
    @Test
    public void existTest() throws IOException {

        //  1.准备request 对象
        GetIndexRequest request = new GetIndexRequest();
        request.indices(index);
        // 2.通过client 去 操作
        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
        // 3输出结果
        System.out.println(exists);
    }

    /**
     * 1.3删除索引
     *
     * @throws IOException
     */
    @Test
    public void testDelete() throws IOException {

        //  1.准备request 对象
        DeleteIndexRequest request = new DeleteIndexRequest();
        request.indices(index);
        //  2.使用client 操作request
        AcknowledgedResponse delete = client.indices().delete(request, RequestOptions.DEFAULT);
        // 3输出结果
        System.out.println(delete.isAcknowledged());
    }

}
