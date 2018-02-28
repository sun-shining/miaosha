package com.kaffa.miaosha.redis;

/**
 * Created by Administrator on 2018/1/18.
 */
public class GoodsKey extends BasePrefix {

    private GoodsKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static GoodsKey goodsList = new GoodsKey(60,"gl");
    public static GoodsKey goodsDetail = new GoodsKey(60,"gd");
    public static GoodsKey goodsStock = new GoodsKey(0,"gs");


}
