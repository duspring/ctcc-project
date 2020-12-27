package com.atguigu.ct.producer;

import java.util.Random;

/**
 * @author springdu
 * @create 2020/12/26 11:32
 * @description TODO
 */
public class Test {
    public static void main(String[] args) {
        Math.random();
        Random random = new Random(10);

        for (int i = 0; i < 10; i++) {
            System.out.println(random.nextInt(10));
        }

        System.out.println("******************************");
        Random random1 = new Random(10);

        for (int i = 0; i < 10; i++) {
            System.out.println(random1.nextInt(10));
        }
    }
}
