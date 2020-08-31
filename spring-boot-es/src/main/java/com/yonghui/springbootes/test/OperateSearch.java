package com.yonghui.springbootes.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yonghui.springbootes.config.EsClient;
import com.yonghui.springbootes.entity.SmsLogs;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.*;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.BoostingQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author:lyh
 * @Description:查询
 * @Date:Created in 2020/8/30 17:39
 */
public class OperateSearch {


    ObjectMapper mapper = new ObjectMapper();
    RestHighLevelClient client = EsClient.getClient();
    String index = "sms-logs-index";
    String type = "sms-logs-type";

    @Test
    public void createIndex() throws Exception {
        // 1.准备关于索引的setting
        Settings.Builder settings = Settings.builder()
                .put("number_of_shards", 3)
                .put("number_of_replicas", 1);

        // 2.准备关于索引的mapping
        XContentBuilder mappings = JsonXContent.contentBuilder()
                .startObject()
                .startObject("properties")
                .startObject("corpName")
                .field("type", "keyword")
                .endObject()
                .startObject("createDate")
                .field("type", "date")
                .field("format", "yyyy-MM-dd")
                .endObject()
                .startObject("fee")
                .field("type", "long")
                .endObject()
                .startObject("ipAddr")
                .field("type", "ip")
                .endObject()
                .startObject("longCode")
                .field("type", "keyword")
                .endObject()
                .startObject("mobile")
                .field("type", "keyword")
                .endObject()
                .startObject("operatorId")
                .field("type", "integer")
                .endObject()
                .startObject("province")
                .field("type", "keyword")
                .endObject()
                .startObject("replyTotal")
                .field("type", "integer")
                .endObject()
                .startObject("sendDate")
                .field("type", "date")
                .field("format", "yyyy-MM-dd")
                .endObject()
                .startObject("smsContent")
                .field("type", "text")
                .field("analyzer", "ik_max_word")
                .endObject()
                .startObject("state")
                .field("type", "integer")
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

    }

    @Test
    public void bulkCreateDoc() throws Exception {
        // 1.准备多个json 对象
        String longcode = "1008687";
        String mobile = "138340658";
        List<String> companies = new ArrayList<>();
        companies.add("腾讯课堂");
        companies.add("阿里旺旺");
        companies.add("海尔电器");
        companies.add("海尔智家公司");
        companies.add("格力汽车");
        companies.add("苏宁易购");
        List<String> provinces = new ArrayList<>();
        provinces.add("北京");
        provinces.add("重庆");
        provinces.add("上海");
        provinces.add("晋城");

        BulkRequest bulkRequest = new BulkRequest();
        for (int i = 1; i < 16; i++) {
            Thread.sleep(1000);
            SmsLogs s1 = new SmsLogs();
            s1.setId(i);
            s1.setCreateDate(new Date());
            s1.setSendDate(new Date());
            s1.setLongCode(longcode + i);
            s1.setMobile(mobile + 2 * i);
            s1.setCorpName(companies.get(i % 5));
            s1.setSmsContent(SmsLogs.doc.substring((i - 1) * 100, i * 100));
            s1.setState(i % 2);
            s1.setOperatorId(i % 3);
            s1.setProvince(provinces.get(i % 4));
            s1.setIpAddr("127.0.0." + i);
            s1.setReplyTotal(i * 3);
            s1.setFee(i * 6 + "");
            String json1 = mapper.writeValueAsString(s1);
            bulkRequest.add(new IndexRequest(index, type, s1.getId().toString()).source(json1, XContentType.JSON));
            System.out.println("数据" + i + s1.toString());
        }

        // 3.client 执行
        BulkResponse responses = client.bulk(bulkRequest, RequestOptions.DEFAULT);

        // 4.输出结果
        System.out.println(responses.getItems().toString());
    }


//    terms 和 term 查询的机制一样，搜索之前不会对你搜索的关键字进行分词，直接拿 关键字 去文档分词库中匹配内容
//    terms:是针对一个字段包含多个值
//    term : where province =北京
//    terms: where province = 北京  or  province =?  (类似于mysql 中的 in)
//    也可针对 text,  只是在分词库中查询的时候不会进行分词

    /**
     * 完全匹配
     *
     * @throws IOException
     */
    @Test
    public void termSearchTest() throws IOException {
        // 1.创建request对象
        SearchRequest request = new SearchRequest(index);
        request.types(type);

        //  2.创建查询条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.from(0);
        builder.size(5);
        builder.query(QueryBuilders.termQuery("province", "北京"));

        request.source(builder);

        //  3.执行查询
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        // 4.输出查询结果
        for (SearchHit hit : response.getHits().getHits()) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            System.out.println(sourceAsMap);

        }
    }


//    match 查询属于高级查询，会根据你查询字段的类型不一样，采用不同的查询方式
//    查询的是日期或者数值，他会将你基于字符串的查询内容转换为日期或数值对待
//    如果查询的内容是一个不能被分词的内容（keyword）,match 不会将你指定的关键字进行分词
//    如果查询的内容是一个可以被分词的内容（text）,match 查询会将你指定的内容根据一定的方式进行分词，去分词库中匹配指定的内容
//    match 查询，实际底层就是多个term 查询，将多个term查询的结果给你封装到一起

    /**
     * 查询所有
     *
     * @throws IOException
     */
    @Test
    public void matchAllSearch() throws IOException {
        // 1.创建request对象
        SearchRequest request = new SearchRequest(index);
        request.types(type);

        //  2.创建查询条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.matchAllQuery());
        //  ES 默认只查询10条数据
        builder.size(20);
        request.source(builder);

        //  3.执行查询
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        // 4.输出查询结果
        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
        System.out.println(response.getHits().getHits().length);
    }


    @Test
    public void matchSearch() throws IOException {
        // 1.创建request对象
        SearchRequest request = new SearchRequest(index);
        request.types(type);

        //  2.创建查询条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        //--------------------------------------------------------------
        builder.query(QueryBuilders.matchQuery("smsContent", "伟大战士"));
        //--------------------------------------------------------------
        builder.size(20);
        request.source(builder);

        //  3.执行查询
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        // 4.输出查询结果
        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
        System.out.println(response.getHits().getHits().length);
    }


    //基于一个field 匹配的内容，按照 and 或者or的方式连接

    /**
     * boolean查询，and
     *
     * @throws IOException
     */
    @Test
    public void booleanMatchSearch() throws IOException {
        // 1.创建request对象
        SearchRequest request = new SearchRequest(index);
        request.types(type);

        //  2.创建查询条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        //--------------------------------------------------------------
        builder.query(QueryBuilders.matchQuery("smsContent", "战士 团队").operator(Operator.AND));
        //--------------------------------------------------------------
        builder.size(20);
        request.source(builder);

        //  3.执行查询
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        // 4.输出查询结果
        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
        System.out.println(response.getHits().getHits().length);
    }

    //match 针对一个field 做检索，multi_math 针对多个field 进行检索，多个field对应一个文本。

    /**
     * 多字段查询
     *
     * @throws IOException
     */
    @Test
    public void multiMatchSearch() throws IOException {
        // 1.创建request对象
        SearchRequest request = new SearchRequest(index);
        request.types(type);

        //  2.创建查询条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        //--------------------------------------------------------------
        builder.query(QueryBuilders.multiMatchQuery("北京", "province", "smsContent"));
        //--------------------------------------------------------------
        builder.size(20);
        request.source(builder);

        //  3.执行查询
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        // 4.输出查询结果
        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
        System.out.println(response.getHits().getHits().length);
    }

//    #id 查询
//    GET /sms-logs-index/sms-logs-type/1

    /**
     * ID查询
     *
     * @throws IOException
     */
    @Test
    public void findById() throws IOException {
        // 创建GetRequest对象
        GetRequest request = new GetRequest(index, type, "1");

        //  执行查询
        GetResponse response = client.get(request, RequestOptions.DEFAULT);

        // 输出结果
        System.out.println(response.getSourceAsMap());
    }




    /**
     * 根据多个id 查询,类似 mysql 中的 where in (id1,id2...)
     *     POST /sms-logs-index/sms-logs-type/_search
     *     {
     *         "query": {
     *           "ids": {
     *               "values": ["1","2","3"]
     *           }
     *        }
     *     }
     *
     * @throws IOException
     */
    @Test
    public void findByIds() throws IOException {
        //  创建request对象
        SearchRequest request = new SearchRequest(index);
        request.types(type);

        //  指定查询条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        //--------------------------------------------------
        builder.query(QueryBuilders.idsQuery().addIds("1", "2", "3"));
        //------------------------------------------------------
        request.source(builder);

        // 执行
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        // 输出结果
        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
    }

    /**
     * 前缀查询，可以通过一个关键字去指定一个field 的前缀，从而查询到指定文档
     *
     * POST /sms-logs-index/sms-logs-type/_search
     * {
     *   "query": {
     *     "prefix": {
     *       "corpName": {
     *         "value": "海"
     *       }
     *     }
     *   }
     * }
     * @throws IOException
     */
    @Test
    public void findByPrefix() throws IOException {
        //  创建request对象
        SearchRequest request = new SearchRequest(index);
        request.types(type);

        //  指定查询条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        //--------------------------------------------------
        builder.query(QueryBuilders.prefixQuery("corpName", "阿"));
        //------------------------------------------------------
        request.source(builder);

        // 执行
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        // 输出结果
        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
    }

    /**
     * 模糊查询，我们可以输入一个字符的大概，ES 可以根据输入的大概去匹配内容。查询结果不稳定
     * #fuzzy 查询
     * POST /sms-logs-index/sms-logs-type/_search
     * {
     *   "query": {
     *     "fuzzy": {
     *       "corpName": {
     *         "value": "腾讯客堂",
     *           #指定前边几个字符是不允许出现错误的
     *         "prefix_length": 2
     *       }
     *     }
     *   }
     * }
     * @throws IOException
     */
    @Test
    public  void findByFuzzy() throws IOException {
        //  创建request对象
        SearchRequest request = new SearchRequest(index);
        request.types(type);

        //  指定查询条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        //--------------------------------------------------
        builder.query(QueryBuilders.fuzzyQuery("corpName","腾讯客堂").prefixLength(2));
        //------------------------------------------------------
        request.source(builder);

        // 执行
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        // 输出结果
        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
    }

    /**
     * 正则查询，通过你编写的正则表达式去匹配内容
     * Ps:prefix wildcard  fuzzy 和regexp 查询效率比较低 ,在要求效率比较高时，避免使用
     * #regexp 查询
     * POST /sms-logs-index/sms-logs-type/_search
     * {
     *   "query": {
     *     "regexp": {
     *       "mobile": "138[0-9]{8}"
     *     }
     *   }
     * }
     * @throws IOException
     */
    @Test
    public  void findByRegexp() throws IOException {
        //  创建request对象
        SearchRequest request = new SearchRequest(index);
        request.types(type);

        //  指定查询条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        //--------------------------------------------------
        builder.query(QueryBuilders.regexpQuery("mobile","138[0-9]{8}"));
        //------------------------------------------------------
        request.source(builder);

        // 执行
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        // 输出结果
        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
    }

    /**
     * 通配查询，同mysql中的like 是一样的，可以在查询时，在字符串中指定通配符*和占位符？
     * #wildcard 查询
     * POST /sms-logs-index/sms-logs-type/_search
     * {
     *   "query": {
     *     "wildcard": {
     *       "corpName": {
     *         "value": "海尔*"
     *       }
     *     }
     *   }
     * }
     *
     * #wildcard 查询
     * POST /sms-logs-index/sms-logs-type/_search
     * {
     *   "query": {
     *     "wildcard": {
     *       "corpName": {
     *         "value": "海尔??"
     *       }
     *     }
     *   }
     * }
     * @throws IOException
     */
    @Test
    public  void findByWildCard() throws IOException {
        //  创建request对象
        SearchRequest request = new SearchRequest(index);
        request.types(type);

        //  指定查询条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        //--------------------------------------------------
        builder.query(QueryBuilders.wildcardQuery("corpName","海尔*"));
        //------------------------------------------------------
        request.source(builder);

        // 执行
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        // 输出结果
        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
    }

    /**
     * 范围查询，只针对数值类型，对一个field 进行大于或者小于的范围指定
     *
     * #rang 查询
     * POST /sms-logs-index/sms-logs-type/_search
     * {
     *   "query": {
     *     "range": {
     *       "fee": {
     *         "gte": 10,
     *         "lte": 20
     *       }
     *     }
     *   }
     * }
     * @throws IOException
     */
    @Test
    public  void findByRang() throws IOException {
        //  创建request对象
        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.types(type);


        //  指定查询条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.rangeQuery("fee").gt(10).lte(30));
        searchRequest.source(searchSourceBuilder);

        // 执行
        SearchResponse response =client.search(searchRequest,RequestOptions.DEFAULT);

        // 输出结果
        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
    }


    /**
     * ES 对from +size时又限制的，from +size 之和 不能大于1W,超过后 效率会十分低下
     * 原理：
     *   from+size  ES查询数据的方式，
     *   第一步将用户指定的关键词进行分词，
     *   第二部将词汇去分词库中进行检索，得到多个文档id,
     *   第三步去各个分片中拉去数据， 耗时相对较长
     *   第四步根据score 将数据进行排序， 耗时相对较长
     *   第五步根据from 和size 的值 将部分数据舍弃，
     *   第六步，返回结果。
     *
     *   scroll +size ES 查询数据的方式
     *   第一步将用户指定的关键词进行分词，
     *   第二部将词汇去分词库中进行检索，得到多个文档id,
     *   第三步，将文档的id放在一个上下文中
     *   第四步，根据指定的size去ES中检索指定个数数据，拿完数据的文档id,会从上下文中移除
     *   第五步，如果需要下一页的数据，直接去ES的上下文中找后续内容。
     *   第六步，循环第四步和第五步
     *   scroll 不适合做实时查询。
     * @throws IOException
     */
    @Test
    public void scrollSearch() throws IOException {

        // 1.创建request
        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.types(type);

        //  2.指定scroll信息,过期时间
        searchRequest.scroll(TimeValue.timeValueMinutes(1L));

        //  3.指定查询条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.size(4);
        builder.sort("fee", SortOrder.DESC);
        searchRequest.source(builder);
        // 4.获取返回结果scrollId,获取source
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        String scrollId = response.getScrollId();
        System.out.println("-------------首页数据---------------------");
        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println("输出-->"+hit.getSourceAsMap());
        }

        while (true){
            // 5.创建scroll request

            SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);

            // 6.指定scroll 有效时间
            scrollRequest.scroll(TimeValue.timeValueMinutes(1L));

            // 7.执行查询，返回查询结果
            SearchResponse scroll = client.scroll(scrollRequest, RequestOptions.DEFAULT);

            // 8.判断是否查询到数据，查询到输出
            SearchHit[] searchHits =  scroll.getHits().getHits();
            if(searchHits!=null && searchHits.length >0){
                System.out.println("-------------下一页数据---------------------");
                for (SearchHit hit : searchHits) {
                    System.out.println("打印：---->"+hit.getSourceAsMap());
                }
            }else{
                //  9.没有数据，结束
                System.out.println("-------------结束---------------------");
                break;
            }
        }

        // 10.创建 clearScrollRequest
        ClearScrollRequest clearScrollRequest = new ClearScrollRequest();

        // 11.指定scrollId
        clearScrollRequest.addScrollId(scrollId);

        //12.删除scroll
        ClearScrollResponse clearScrollResponse = client.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);

        // 13.输出结果
        System.out.println("删除scroll:"+clearScrollResponse.isSucceeded());

    }

    /**
     * 根据term,match 等查询方式去删除大量索引
     * PS:如果你要删除的内容，时index下的大部分数据，推荐创建一个新的index,然后把保留的文档内容，添加到全新的索引
     * #Delet-by-query 删除
     * POST /sms-logs-index/sms-logs-type/_delete_by_query
     * {
     *    "query": {
     *     "range": {
     *       "fee": {
     *         "lt": 20
     *       }
     *     }
     *   }
     * }
     * @throws IOException
     */
    @Test
    public void deleteByQuery() throws IOException {
        // 1.创建DeleteByQueryRequest
        DeleteByQueryRequest request = new DeleteByQueryRequest(index);
        request.types(type);

        // 2.指定条件
        request.setQuery(QueryBuilders.rangeQuery("fee").lt(20));

        // 3.执行
        BulkByScrollResponse response = client.deleteByQuery(request, RequestOptions.DEFAULT);

        // 4.输出返回结果
        System.out.println(response.toString());
    }

    /**
     * 复合过滤器，将你的多个查询条件 以一定的逻辑组合在一起，
     *
     * must:所有条件组合在一起，表示 and 的意思
     * must_not: 将must_not中的条件，全部都不匹配，表示not的意思
     * should:所有条件用should 组合在一起，表示or 的意思
     *
     * #省是 晋城 或者北京
     * # 运营商不能是联通
     * #smsContent 包含 战士 和的
     * POST /sms-logs-index/sms-logs-type/_search
     * {
     *   "query": {
     *     "bool": {
     *       "should": [
     *         {
     *           "term": {
     *             "province": {
     *               "value": "晋城"
     *             }
     *           }
     *
     *         },
     *          {
     *           "term": {
     *             "province": {
     *               "value": "北京"
     *             }
     *           }
     *
     *         }
     *       ],
     *       "must_not": [
     *         {
     *           "term": {
     *             "operatorId": {
     *               "value": "2"
     *             }
     *           }
     *         }
     *       ],
     *       "must": [
     *         {
     *           "match": {
     *             "smsContent": "战士"
     *           }
     *         },
     *         {
     *           "match": {
     *             "smsContent": "的"
     *           }
     *         }
     *       ]
     *     }
     *   }
     * }
     * @throws IOException
     */
    @Test
    public void  boolSearch() throws IOException {

        //  1.创建 searchRequest
        SearchRequest request = new SearchRequest(index);
        request.types(type);
        // 2.指定查询条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        // #省是 晋城 或者北京
        boolQueryBuilder.should(QueryBuilders.termQuery("province","北京"));
        boolQueryBuilder.should(QueryBuilders.termQuery("province","晋城"));

        //# 运营商不能是联通
        boolQueryBuilder.mustNot(QueryBuilders.termQuery("operatorId",2));

        //#smsContent 包含 战士 和的
        boolQueryBuilder.must(QueryBuilders.matchQuery("smsContent","战士"));
        boolQueryBuilder.must(QueryBuilders.matchQuery("smsContent","的"));

        builder.query(boolQueryBuilder);
        request.source(builder);
        //  3.执行查询
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        // 4.输出结果
        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
    }

    /**
     * boosting 查询可以帮助我们去影响查询后的score
     *    positive:只有匹配上positive 查询的内容，才会被放到返回的结果集中
     *    negative: 如果匹配上了positive 也匹配上了negative, 就可以 降低这样的文档score.
     *    negative_boost:指定系数,必须小于1   0.5
     * 关于查询时，分数时如何计算的：
     * 	搜索的关键字再文档中出现的频次越高，分数越高
     * 	指定的文档内容越短，分数越高。
     * 	我们再搜索时，指定的关键字也会被分词，这个被分词的内容，被分词库匹配的个数越多，分数就越高。
     *
     * 	#boosting 查询
     * POST /sms-logs-index/sms-logs-type/_search
     * {
     *   "query": {
     *     "boosting": {
     *       "positive": {
     *         "match": {
     *           "smsContent": "战士"
     *         }
     *       },
     *       "negative": {
     *         "match": {
     *           "smsContent": "团队"
     *         }
     *       },
     *       "negative_boost": 0.2
     *     }
     *   }
     * }
     * @throws IOException
     */
    @Test
    public void  boostSearch() throws IOException {

        //  1.创建 searchRequest
        SearchRequest request = new SearchRequest(index);
        request.types(type);
        // 2.指定查询条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoostingQueryBuilder boost = QueryBuilders.boostingQuery(
                QueryBuilders.matchQuery("smsContent", "战士"),
                QueryBuilders.matchQuery("smsContent", "团队")
        ).negativeBoost(0.2f);
        builder.query(boost);
        request.source(builder);
        //  3.执行查询
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        // 4.输出结果
        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
    }

    /**
     * query 查询：根据你的查询条件，去计算文档的匹配度得到一个分数，并根据分数排序，不会做缓存的。
     *
     * filter 查询：根据查询条件去查询文档，不去计算分数，而且filter会对经常被过滤的数据进行缓存。
     * #filter 查询
     * POST /sms-logs-index/sms-logs-type/_search
     * {
     *   "query": {
     *     "bool": {
     *       "filter": [
     *         {
     *           "term": {
     *             "corpName": "海尔智家公司"
     *            }
     *         },
     *         {
     *           "range":{
     *             "fee":{
     *               "lte":50
     *             }
     *           }
     *         }
     *       ]
     *     }
     *   }
     * }
     * @throws IOException
     */
    @Test
    public void filter() throws IOException {

        // 1.searchRequest
        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.types(type);

        // 2.指定查询条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        boolBuilder.filter(QueryBuilders.termQuery("corpName","海尔智家公司"));
        boolBuilder.filter(QueryBuilders.rangeQuery("fee").gt(20));
        sourceBuilder.query(boolBuilder);
        searchRequest.source(sourceBuilder);

        //  3.执行
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

        //  4. 输出结果
        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit.getSourceAsMap());
            System.out.println(hit.getId()+"的分数是："+hit.getScore());
        }
    }

    /**
     * 高亮查询就是用户输入的关键字，以一定特殊样式展示给用户，让用户知道为什么这个结果被检索出来
     * 高亮展示的数据，本身就是文档中的一个field,单独将field以highlight的形式返回给用户
     * ES提供了一个highlight 属性，他和query 同级别。
     *  frament_size: 指定高亮数据展示多少个字符回来
     *  pre_tags:指定前缀标签<front color="red">
     *  post_tags:指定后缀标签 </font>
     *
     * #highlight 高亮查询
     * POST /sms-logs-index/sms-logs-type/_search
     * {
     *   "query": {
     *     "match": {
     *       "smsContent": "团队"
     *     }
     *   },
     *   "highlight": {
     *     "fields": {
     *       "smsContent":{}
     *     },
     *     "pre_tags":"<font color='red'>",
     *     "post_tags":"</font>",
     *     "fragment_size":10
     *   }
     * }
     * @throws IOException
     */
    @Test
    public void highLightQuery() throws IOException {
        // 1.创建request
        SearchRequest request = new SearchRequest(index);
        request.types(type);

        // 2.指定查询条件，指定高亮
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.matchQuery("smsContent","团队"));
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("smsContent",10)
                .preTags("<font colr='red'>")
                .postTags("</font>");
        builder.highlighter(highlightBuilder);
        request.source(builder);

        // 3.执行
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        //4. 输出结果
        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit.getHighlightFields().get("smsContent"));
        }
    }

}

