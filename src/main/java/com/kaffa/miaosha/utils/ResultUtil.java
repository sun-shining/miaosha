package com.kaffa.miaosha.utils;

import com.kaffa.miaosha.result.CodeMsg;
import com.kaffa.miaosha.result.Result;

/**
 * Created by Administrator on 2018/1/18.
 */
public class ResultUtil {

    public static  Result success(Object obj){
        Result result = new Result();
        result.setCode(0);
        result.setMsg("success");
        result.setData(obj);
        return result;
    }

    public static Result success(){
        return success(null);
    }

    public static Result error(){
        Result result = new Result();
        result.setCode(500100);
        result.setMsg("服务端异常");
        result.setData(null);
        return result;
    }

    public static Result error(String object){
        Result result = new Result();
        result.setCode(500100);
        result.setMsg(object);
        result.setData(null);
        return result;
    }

    public static Result error(CodeMsg codeMsg){
        Result result = new Result();
        result.setCode(codeMsg.getCode());
        result.setMsg(codeMsg.getMsg());
        result.setData(null);
        return result;
    }
}
