package com.kaffa.miaosha.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * Created by Administrator on 2018/1/21.
 */
public class ValidatorUtil {

    private static final Pattern mobile_pattern = Pattern.compile("1\\d{10}");

    /**
     * 根据正则判断输入是否为手机号
     * @param mobile
     * @return
     */
    public static boolean isMobile(String mobile){
        if (StringUtils.isEmpty(mobile)){
            return false;
        }else{
            return mobile_pattern.matcher(mobile).matches();
        }
    }
}
