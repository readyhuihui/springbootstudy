package com.yonghui.springbootjedis.test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

/**
 * 订阅
 *
 * @author Front Ng
 * @date 2019-06-14 10:46
 **/
public class Sub extends JedisPubSub {

    private static String channel = "hello";

    @Override
    public void onMessage(String channel, String message) {
        System.out.println(" [x] 接收到 '" + message + "'");
    }

    public static void main(String[] args) {

        Jedis subscriberJedis = RedisUtils.getJedis();
        final Sub subscriber = new Sub();
        new Thread(() -> {
            try {
                subscriberJedis.subscribe(subscriber, channel);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}