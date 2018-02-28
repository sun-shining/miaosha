package com.kaffa.miaosha.exception;

import com.kaffa.miaosha.result.CodeMsg;

/**
 * Created by Administrator on 2018/1/21.
 */
public class GlobalException extends RuntimeException {

    private CodeMsg cm ;

    public GlobalException(CodeMsg cm) {
        super(cm.getMsg());
        this.cm = cm;
    }

    public CodeMsg getCm() {
        return cm;
    }
}
