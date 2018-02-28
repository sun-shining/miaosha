package com.kaffa.miaosha.redis;

/**
 * Created by Administrator on 2018/1/18.
 */
public class OrderKey extends BasePrefix {

    private OrderKey(String prefix) {
        super(prefix);
    }

    public static OrderKey getOrderInfoByUserIdGoodsId = new OrderKey("oug");
}
