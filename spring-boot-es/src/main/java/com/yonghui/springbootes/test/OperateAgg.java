package com.yonghui.springbootes.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yonghui.springbootes.config.EsClient;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.range.Range;
import org.elasticsearch.search.aggregations.metrics.cardinality.Cardinality;
import org.elasticsearch.search.aggregations.metrics.stats.extended.ExtendedStats;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;

import java.io.IOException;

/**
 * @Author:lyh
 * @Description:聚合检索
 * @Date:Created in 2020/8/31 13:05
 */
public class OperateAgg {

    ObjectMapper mapper = new ObjectMapper();
    RestHighLevelClient client = EsClient.getClient();
    String index = "sms-logs-index";
    String type = "sms-logs-type";

    /**
     * 去重计数聚合查询
     * 去重计数，cardinality 先将返回的文档中的一个指定的field进行去重，统计一共有多少条
     * @throws IOException
     */
    @Test
    public void aggCardinalityC() throws IOException {

        // 1.创建request
        SearchRequest request = new SearchRequest(index);
        request.types(type);

        // 2. 指定使用聚合查询方式
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.aggregation(AggregationBuilders.cardinality("provinceAgg").field("province"));
        request.source(builder);

        // 3.执行查询
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        // 4.输出返回结果
        Cardinality agg = response.getAggregations().get("provinceAgg");
        System.out.println(agg.getValue());
    }


    /**
     * 统计一定范围内出现的文档个数，比如，针对某一个field 的值再0~100,100~200,200~300 之间文档出现的个数分别是多少
     * 范围统计 可以针对 普通的数值，针对时间类型，针对ip类型都可以响应。
     * 数值 rang
     * 时间  date_rang
     * ip   ip_rang
     * #针对数值方式的范围统计  from 带等于效果 ，to 不带等于效果
     * POST /sms-logs-index/sms-logs-type/_search
     * {
     *   "aggs": {
     *     "agg": {
     *       "range": {
     *         "field": "fee",
     *         "ranges": [
     *           {
     *             "to": 30
     *           },
     *            {
     *             "from": 30,
     *             "to": 60
     *           },
     *           {
     *             "from": 60
     *           }
     *         ]
     *       }
     *     }
     *   }
     * }
     * #时间方式统计
     * POST /sms-logs-index/sms-logs-type/_search
     * {
     *   "aggs": {
     *     "agg": {
     *       "date_range": {
     *         "field": "sendDate",
     *         "format": "yyyy",
     *         "ranges": [
     *           {
     *             "to": "2000"
     *           },{
     *             "from": "2000"
     *           }
     *         ]
     *       }
     *     }
     *   }
     * }
     * #ip 方式 范围统计
     * POST /sms-logs-index/sms-logs-type/_search
     * {
     *   "aggs": {
     *     "agg": {
     *       "ip_range": {
     *         "field": "ipAddr",
     *         "ranges": [
     *           {
     *             "to": "127.0.0.8"
     *           },
     *           {
     *             "from": "127.0.0.8"
     *           }
     *         ]
     *       }
     *     }
     *   }
     * }
     * @throws IOException
     */
    @Test
    public void aggRang() throws IOException {
        // 1.创建request
        SearchRequest request = new SearchRequest(index);
        request.types(type);

        // 2. 指定使用聚合查询方式
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.aggregation(AggregationBuilders.range("agg").field("fee")
                .addUnboundedTo(30)
                .addRange(30,60)
                .addUnboundedFrom(60));
        request.source(builder);

        // 3.执行查询
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        // 4.输出返回结果
        Range agg = response.getAggregations().get("agg");
        for (Range.Bucket bucket : agg.getBuckets()) {
            String key = bucket.getKeyAsString();
            Object from = bucket.getFrom();
            Object to = bucket.getTo();
            long docCount = bucket.getDocCount();
            System.out.println(String.format("key: %s ,from: %s ,to: %s ,docCount: %s",key,from,to,docCount));
        }
    }

    /**
     * 他可以帮你查询指定field 的最大值，最小值，平均值，平方和...
     * 使用 extended_stats
     * #统计聚合查询 extended_stats
     * POST /sms-logs-index/sms-logs-type/_search
     * {
     *   "aggs": {
     *     "agg": {
     *       "extended_stats": {
     *         "field": "fee"
     *       }
     *     }
     *   }
     * }
     * @throws IOException
     */
    @Test
    public void aggExtendedStats() throws IOException {
        // 1.创建request
        SearchRequest request = new SearchRequest(index);
        request.types(type);

        // 2. 指定使用聚合查询方式
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.aggregation(AggregationBuilders.extendedStats("agg").field("fee"));
        request.source(builder);

        // 3.执行查询
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        // 4.输出返回结果
        ExtendedStats extendedStats =  response.getAggregations().get("agg");
        System.out.println("最大值："+extendedStats.getMaxAsString()+",最小值："+extendedStats.getMinAsString());
    }
}
