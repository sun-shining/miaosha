package com.kaffa.miaosha.utils;

import java.util.UUID;

/**
 * Created by Administrator on 2018/1/21.
 */
public class UUIDUtil {

    public static String uuid(){
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static void main(String[] args) {
        System.out.println(UUIDUtil.uuid());
    }
}
