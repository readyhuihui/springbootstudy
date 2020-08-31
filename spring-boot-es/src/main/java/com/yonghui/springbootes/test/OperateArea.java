package com.yonghui.springbootes.test;

import com.yonghui.springbootes.config.EsClient;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author:lyh
 * @Description:地图检索
 * @Date:Created in 2020/8/31 13:09
 */
public class OperateArea {
    RestHighLevelClient client = EsClient.getClient();
    String index = "map";
    String type = "map";

    /**
     * 地图检索
     * geo_distance :直线距离检索方式
     * geo_bounding_box: 以2个点确定一个矩形，获取再矩形内的数据
     * geo_polygon:以多个点，确定一个多边形，获取多边形的全部数据
     * @throws IOException
     */
    @Test
    public void GeoPolygon() throws IOException {
        //  1.创建searchRequest
        SearchRequest request = new SearchRequest(index);
        request.types(type);

        //  2.指定 检索方式

        SearchSourceBuilder builder = new SearchSourceBuilder();
        List<GeoPoint> points = new ArrayList<>();
        points.add(new GeoPoint(40.075013, 116.220296));
        points.add(new GeoPoint(40.044751, 116.346777));
        points.add(new GeoPoint(39.981533, 116.236106));
        builder.query(QueryBuilders.geoPolygonQuery("location", points));
        request.source(builder);
        // 3.执行
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        // 4.输出结果
        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
    }
}
