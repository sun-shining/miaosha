package com.kaffa.miaosha.controller;

import com.kaffa.miaosha.domain.*;
import com.kaffa.miaosha.result.CodeMsg;
import com.kaffa.miaosha.result.Result;
import com.kaffa.miaosha.service.GoodsService;
import com.kaffa.miaosha.service.MiaoshaService;
import com.kaffa.miaosha.service.MiaoshaUserService;
import com.kaffa.miaosha.service.OrderService;
import com.kaffa.miaosha.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private MiaoshaUserService miaoshaUserService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private OrderService orderService;

    /**
     *
     * @param model
     * @param user
     * @param orderId
     * @return
     */
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    @ResponseBody
    public Result<OrderInfo> orderDetail(Model model, MiaoshaUser user, @RequestParam("orderId") long orderId) {
        model.addAttribute("user",user);
        //1 判断用户是否登录,未登录直接踢到登录页面
        if (user == null) {
            return  ResultUtil.error(CodeMsg.SESSION_ERROR.getMsg());
        }
        OrderInfo orderInfo = orderService.getOrderById(orderId);
        if (orderInfo == null){
            return ResultUtil.error(CodeMsg.ORDER_NOT_EXIST);
        }

        Long goodsId = orderInfo.getGoodsId();
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        OrderDetailVo vo = new OrderDetailVo();
        vo.setGoodsVo(goodsVo);
        vo.setOrderInfo(orderInfo);

        return ResultUtil.success(vo);
    }


}
