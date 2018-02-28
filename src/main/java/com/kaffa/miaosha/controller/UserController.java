package com.kaffa.miaosha.controller;

import com.kaffa.miaosha.domain.GoodsVo;
import com.kaffa.miaosha.domain.MiaoshaUser;
import com.kaffa.miaosha.result.Result;
import com.kaffa.miaosha.service.GoodsService;
import com.kaffa.miaosha.service.MiaoshaUserService;
import com.kaffa.miaosha.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {


    /**
     * 查看用户信息
     * @param model
     * @param user
     * @return
     */
    @RequestMapping("/info")
    @ResponseBody
    public Result<MiaoshaUser> info(Model model, MiaoshaUser user) {
        model.addAttribute("user",user);

        return ResultUtil.success(user);
    }


}
