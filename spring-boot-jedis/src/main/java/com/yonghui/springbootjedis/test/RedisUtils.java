package com.yonghui.springbootjedis.test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * redis工具类
 * @author Front Ng
 * @date 2019-06-14 11:46
 **/
public class RedisUtils {

    private final static JedisPoolConfig POOL_CONFIG = new JedisPoolConfig();

    private final static JedisPool JEDIS_POOL = new JedisPool(POOL_CONFIG,"127.0.0.1", 6379, 0,"123456");
//    private final static JedisPool JEDIS_POOL = new JedisPool(POOL_CONFIG, "127.0.0.1", 6379, 0);

    public static Jedis getJedis() {
        return JEDIS_POOL.getResource();

    }
}