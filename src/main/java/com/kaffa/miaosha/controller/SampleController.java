package com.kaffa.miaosha.controller;

import com.kaffa.miaosha.domain.User;
import com.kaffa.miaosha.rabbitmq.MQSender;
import com.kaffa.miaosha.redis.RedisService;
import com.kaffa.miaosha.redis.UserKey;
import com.kaffa.miaosha.result.Result;
import com.kaffa.miaosha.service.MiaoshaUserService;
import com.kaffa.miaosha.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Administrator on 2018/1/17.
 */
@Controller
@RequestMapping("/demo")
public class SampleController {

    @Autowired
    private MiaoshaUserService miaoshaUserService;

    @Autowired
    private RedisService redisService;
    @Autowired
    private MQSender mqSender;

    @RequestMapping("/thymeleaf")
    public String thymeleaf(Model model){
        model.addAttribute("name", "Juddar");
        return "hello";

    }
//    @GetMapping("/mq")
    @RequestMapping("/mq")
    @ResponseBody
    public Result<Boolean> mq(){
        mqSender.send("hello cute tiny rabbit");
        return  ResultUtil.success("hello cute tiny rabbit");
    }

    @PutMapping("/user/put")
    @ResponseBody
    public Result<Boolean> getUserById(User user){
        Boolean res  = redisService.set(UserKey.getById, String.valueOf(user.getId()), user);
        return  ResultUtil.success(res);
    }

    @GetMapping("/user/get")
    @ResponseBody
    public Result<User> getUserById(@RequestParam("id") int id){
        User user = redisService.get(UserKey.getById, String.valueOf(id), User.class);
        return  ResultUtil.success(user);
    }

    /*@GetMapping("/user/tx")
    @ResponseBody
    public void tx(){
        userService.inserUser();
    }*/

   /* @GetMapping("/redis/get")
    @ResponseBody
    public Result<String> testRedis(@RequestParam("key") String key,
                                    @RequestParam("value") String value){
        Boolean set = redisService.set(key, value);
        String s = redisService.get(key, String.class);
        return ResultUtil.success(s);
    }*/
}
