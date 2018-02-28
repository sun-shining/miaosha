package com.kaffa.miaosha.redis;

/**
 * Created by Administrator on 2018/1/18.
 */
public class MiaoShaUserKey extends BasePrefix {

    public static int TOKEN_EXPIRE = 3600*24*2;//token过期时间

    private MiaoShaUserKey(int expireSeconds,String prefix) {
        super(prefix);
    }

    public static MiaoShaUserKey token = new MiaoShaUserKey(TOKEN_EXPIRE, "tk");
    public static MiaoShaUserKey getById = new MiaoShaUserKey(0, "id");


}
