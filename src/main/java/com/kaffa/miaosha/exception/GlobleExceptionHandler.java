package com.kaffa.miaosha.exception;

import com.kaffa.miaosha.result.CodeMsg;
import com.kaffa.miaosha.result.Result;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


/**
 * 处理全局异常的处理器,可以将异常封装后,携带异常信息返回给页面
 * 该controller和普通控制器没啥区别了
 */
@ControllerAdvice
@ResponseBody
public class GlobleExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public Result<String> exceptionHandler(Exception e){
        e.printStackTrace();
        if (e instanceof GlobalException){
            GlobalException globalException = (GlobalException)e;
            return new Result<>(globalException.getCm());
        }else if (e instanceof BindException){
            BindException bindException = (BindException)e;
            List<ObjectError> allErrors = bindException.getAllErrors();
            ObjectError objectError = allErrors.get(0);

            return new Result<>(CodeMsg.BIND_ERROR.fillArgs(objectError.getDefaultMessage()));
        }else if (e instanceof NullPointerException){
            NullPointerException nullPointerException = (NullPointerException)e;
            return new Result<>(CodeMsg.MOBILE_NOT_EXIST);
        } else return new Result<>(CodeMsg.SUCCESS);
    }

}
