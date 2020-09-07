package com.yonghui.springbootjedis.test;

import redis.clients.jedis.Jedis;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 发布
 *
 * @author Front Ng
 * @date 2019-06-14 10:46
 **/
public class Pub {

    private static String channel = "hello";

    public static void main(String[] args) {


        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                Jedis publisherJedis = RedisUtils.getJedis();

                String message = "Hello World! " + System.currentTimeMillis();
                System.out.println(" [x] 发送 '" + message + "'");
                publisherJedis.publish(channel, message);
            }
        }, 1000, 1000);
    }
}