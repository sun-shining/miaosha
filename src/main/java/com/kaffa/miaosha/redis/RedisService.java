package com.kaffa.miaosha.redis;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2018/1/18.
 */
@Service
public class RedisService {
    @Autowired
    private JedisPool jedisPool;

    public static void main(String[] args) {
        int h ;
        System.out.println(15 & ((h="juddar".hashCode()) ^ (h >>> 16)));
    }
    /**
     * 从缓存中获取对应对象
     * @param prefix
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T get(KeyPrefix prefix, String key, Class<T> clazz){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;
            String str = jedis.get(realKey);
            T t = string2Obj(str, clazz);
            return t;
        } finally {
            return2Pool(jedis);
        }
    }

    /**
     * 向缓存中设置一个对象
     * @param prefix
     * @param key
     * @param value
     * @param <T>
     * @return
     */
    public <T> Boolean set(KeyPrefix prefix, String key, T value){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String str = obj2String(value);
            if (str == null || str.length()<=0){
                return false;
            }
            String realKey = prefix.getPrefix() + key;
            int seconds = prefix.expireSeconds();
            if (seconds <= 0){
                jedis.set(realKey, str);
            } else {
                jedis.setex(realKey, seconds, str);
            }
            return true;
        } finally {
            return2Pool(jedis);
        }
    }

    /**
     *  删除一个元素
     * @param prefix
     * @param key
     * @return
     */
    public boolean delete(KeyPrefix prefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;
            Long ret = jedis.del(realKey);

            return ret > 0;
        } finally {
            return2Pool(jedis);
        }
    }

    public boolean delete(KeyPrefix prefix) {
        if(prefix == null) {
            return false;
        }
        List<String> keys = scanKeys(prefix.getPrefix());
        if(keys==null || keys.size() <= 0) {
            return true;
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.del(keys.toArray(new String[0]));
            return true;
        } catch (final Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if(jedis != null) {
                jedis.close();
            }
        }
    }

    public List<String> scanKeys(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            List<String> keys = new ArrayList<String>();
            String cursor = "0";
            ScanParams sp = new ScanParams();
            sp.match("*"+key+"*");
            sp.count(100);
            do{
                ScanResult<String> ret = jedis.scan(cursor, sp);
                List<String> result = ret.getResult();
                if(result!=null && result.size() > 0){
                    keys.addAll(result);
                }
                //再处理cursor
                cursor = ret.getStringCursor();
            }while(!cursor.equals("0"));
            return keys;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 将key对应的值增加1,如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。
     * @param prefix
     * @param key
     * @param <T>
     * @return
     */
    public <T> Long incr(KeyPrefix prefix, String key){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix()+key;
            return jedis.incr(realKey);
        } finally {
            return2Pool(jedis);
        }

    }

    /**
     * 减少值
     * */
    public <T> Long decr(KeyPrefix prefix, String key) {
        Jedis jedis = null;
        try {
            jedis =  jedisPool.getResource();
            //生成真正的key
            String realKey  = prefix.getPrefix() + key;
            return  jedis.decr(realKey);
        }finally {
            return2Pool(jedis);
        }
    }

    public  boolean contains(KeyPrefix prefix, String key){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            if (key == null || key.length()<=0){
                return false;
            }
            String realKey = prefix.getPrefix() + key;

            return jedis.exists(realKey);
        } finally {
            return2Pool(jedis);
        }
    }

    /**
     *  对象转字符串
     * @param value
     * @param <T>
     * @return
     */
    public static  <T> String obj2String(T value) {
        if (value == null){
            return null;
        }
        Class clazz = value.getClass();
        if (clazz == int.class || clazz == Integer.class){
            return String.valueOf(value);
        }else  if (clazz == long.class || clazz == Long.class){
            return String.valueOf(value);
        }else if (clazz == String.class){
            return (String)value;
        }

        return JSON.toJSONString(value);
    }

    /**
     * 字符串转对象
     * @param str
     * @param clazz
     * @param <T>
     * @return
     */
    public static  <T> T string2Obj(String str, Class<T> clazz) {
        if (str == null || str.length()<=0 || clazz == null){
            return null;
        }
        if (clazz == int.class || clazz == Integer.class){
            return (T)Integer.valueOf(str);
        }else  if (clazz == long.class || clazz == Long.class){
            return (T)Long.valueOf(str);
        }else if (clazz == String.class){
            return (T)str;
        } else {
            return JSON.toJavaObject(JSON.parseObject(str), clazz);
        }


    }

    private void return2Pool(Jedis jedis){
        if (jedis != null){
            jedis.close();
        }
    }


}
