package com.kaffa.miaosha.controller;

import com.kaffa.miaosha.domain.LoginVO;
import com.kaffa.miaosha.result.Result;
import com.kaffa.miaosha.service.MiaoshaUserService;
import com.kaffa.miaosha.utils.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * Created by Administrator on 2018/1/19.
 */
@Controller
@RequestMapping("/login")
public class LoginController {
    private Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private MiaoshaUserService miaoshaUserService;

    @RequestMapping("/to_login")
    public String toLogin() {
        return "login";
    }

    @RequestMapping("/do_login")
    @ResponseBody
    public Result doLogin(HttpServletResponse response, @Valid LoginVO loginVO) {
        logger.info(loginVO.toString());

        miaoshaUserService.login(response,loginVO);
        return ResultUtil.success();
    }
}
