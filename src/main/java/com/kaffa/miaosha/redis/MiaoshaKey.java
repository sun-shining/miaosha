package com.kaffa.miaosha.redis;

/**
 * Created by Administrator on 2018/2/25.
 */
public class MiaoshaKey extends BasePrefix {

    private MiaoshaKey(String prefix) {
        super(prefix);
    }

    public static MiaoshaKey isGoodsOver = new MiaoshaKey("go");
}
