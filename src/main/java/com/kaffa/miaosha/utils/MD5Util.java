package com.kaffa.miaosha.utils;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * Created by Administrator on 2018/1/19.
 */
public class MD5Util {

    public static String md5(String str){
        return DigestUtils.md5Hex(str);
    }

    private static String salt = "1a2b3c4d";

    /**
     * 第一次加密
     * @param inputPass 用户输入明文密码
     * @return
     */
    public static String inputPass2FormPass(String inputPass){
        String str = ""+salt.charAt(0)+salt.charAt(2) + inputPass +salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    /**
     * 第二次加密
     * @param formPass 加密后传输的密码
     * @param salt
     * @return 数据库中实际存储的密码
     */
    public static String formPass2DBPass(String formPass, String salt){
        String str = ""+salt.charAt(0)+salt.charAt(2) + formPass +salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    public static String inputPass2DBPass(String inputPass, String dbSalt){
        String str = inputPass2FormPass(inputPass);
        return formPass2DBPass(str, dbSalt);
    }

    public static void main(String[] args) {
        System.out.println(
                inputPass2DBPass("123456","1a2b3c4d")
        );
    }
}
