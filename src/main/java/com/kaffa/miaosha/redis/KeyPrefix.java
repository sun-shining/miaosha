package com.kaffa.miaosha.redis;

/**
 * Created by Administrator on 2018/1/18.
 */
public interface KeyPrefix {

    public int expireSeconds();

    public String getPrefix();
}
