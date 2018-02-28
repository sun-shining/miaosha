package com.kaffa.miaosha.redis;

import com.alibaba.fastjson.JSONArray;

/**
 * json格式的二维数组处理
 * Created by Administrator on 2018/1/19.
 */
public class MyTests {
    public static void main(String[] args){
        String str="[[\"name\",\"专业a\"],[\"notice\",\"专业B\"],[\"purchase\",\"专业C\"]]";

        //此处引入JSON jar包

        JSONArray arr = JSONArray.parseArray(str);

        for(Object o :arr){

            JSONArray a = (JSONArray)o;

            for(int i = 0 ; i < a.size() ; i++){

                String j = (String)a.get(i);

                System.out.println(j);

            }

        }
    }
}
