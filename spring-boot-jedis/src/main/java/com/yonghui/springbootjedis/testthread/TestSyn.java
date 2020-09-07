package com.yonghui.springbootjedis.testthread;

/**
 * @Author:lyh
 * @Description:
 * @Date:Created in 2020/9/3 19:39
 */
public class TestSyn {
    public static void main(String[] args) throws InterruptedException {

        byte [] bt = new  byte[0];
        class Demo1 {

            private int count = 0;

            public void incre() {
                synchronized (bt){
                    count++;
                }
            }

            public int getCount() {
                return count;
            }
        }
        final Demo1 dm = new Demo1();


        class Demo2 extends Thread {

            @Override
            public void run() {
                for (int i = 0; i < 1000; i++) {
                    dm.incre();
                }
            }
        }
        Demo2 dm1 = new Demo2();
        Demo2 dm2 = new Demo2();
        dm1.start();
        dm2.start();
        dm1.join();
        dm2.join();
        System.out.println("输出：" + dm.getCount());

    }
}
