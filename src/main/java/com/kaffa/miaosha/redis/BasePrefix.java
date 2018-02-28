package com.kaffa.miaosha.redis;

/**
 * Created by Administrator on 2018/1/18.
 */
public abstract class BasePrefix implements KeyPrefix {

    //0代表永不过期
    private int expireSeconds;

    private String prefix;

    public BasePrefix(String prefix){
        this(0, prefix);
    }

    public BasePrefix(int expireSeconds, String prefix) {
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
    }

    @Override
    public int expireSeconds() {
        return expireSeconds;
    }

    @Override
    public String getPrefix() {
        //Tips 利用类名来保证每个模块的key都不同
        String simpleName = getClass().getSimpleName();

        return simpleName+":"+prefix;
    }
}
