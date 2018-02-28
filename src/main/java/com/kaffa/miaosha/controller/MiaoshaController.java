package com.kaffa.miaosha.controller;

import com.kaffa.miaosha.domain.*;
import com.kaffa.miaosha.rabbitmq.MQSender;
import com.kaffa.miaosha.redis.GoodsKey;
import com.kaffa.miaosha.redis.MiaoshaKey;
import com.kaffa.miaosha.redis.OrderKey;
import com.kaffa.miaosha.redis.RedisService;
import com.kaffa.miaosha.result.CodeMsg;
import com.kaffa.miaosha.result.Result;
import com.kaffa.miaosha.service.GoodsService;
import com.kaffa.miaosha.service.MiaoshaService;
import com.kaffa.miaosha.service.OrderService;
import com.kaffa.miaosha.service.MiaoshaUserService;
import com.kaffa.miaosha.utils.ResultUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean{

    @Autowired
    private MiaoshaUserService miaoshaUserService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private MiaoshaService miaoshaService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private MQSender mqSender;

    //在内存里加个标识,可以在redis里的库存已经减少到0时,通过内存中的标识减少对redis的访问
    //以进一步优化程序
    private HashMap<Long, Boolean> localOverMap = new HashMap<>();
    /**
     * spring在初始化bean后允许用户对bean就行修改
     * 在初始化该bean时，将商品库存信息缓存进redis
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsVos = goodsService.listGoodsVo();
        if (goodsVos == null || goodsVos.size() ==0) {
            return;
        }

        for (GoodsVo goodsVo : goodsVos) {
            redisService.set(GoodsKey.goodsStock, ""+goodsVo.getId(), goodsVo.getStockCount());
            localOverMap.put(goodsVo.getId(), false);
        }


    }

    /**
     * GET:是幂等的  POST:是对数据产生影响的
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/do_miaosha", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> doMiaosha(Model model, MiaoshaUser user, @RequestParam("goodsId") long goodsId) {
        model.addAttribute("user",user);
        //1 判断用户是否登录,未登录直接踢到登录页面
        if (user == null) {
            return  ResultUtil.error(CodeMsg.SESSION_ERROR.getMsg());
        }

        if (localOverMap.get(goodsId)) {
            return ResultUtil.error(CodeMsg.MIAO_SHA_OVER);
        }

        //2 将商品库存在项目启动时加载到redis中，通过这样的方式来减少对数据库的访问
        // 此处是预减库存，就是减少启动时加载在rendis中的商品数量
        Long goodStock = redisService.decr(GoodsKey.goodsStock, ""+goodsId);
        if (goodStock < 0) {
            // 此处有个小问题，就是虽然redis访问速度很快，但是还是有一定的网络开销，假如待秒杀商品
            //在redis里的库存已经减到0了，就没有必要再访问redis了，所以可以搞个内存标记
            localOverMap.put(goodsId, true);
            return ResultUtil.error(CodeMsg.MIAO_SHA_OVER);
        }

        //3 判断是否秒杀成功,防止一人秒杀多个订单（判断是否重复秒杀）
        MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if (miaoshaOrder != null) {
            return ResultUtil.error(CodeMsg.REPEAT_FAIL.getMsg());
        }

        //4 将请求扔入rabbitmq中就行排队. 页面进行轮询，看是否有结果,一会儿再配合的建一个查询秒杀是否成功的处理器
        // 如果用户排队成功了，那就需要实际操作数据库，加入订单表响应数据了，所以光扔个商品id是不够的
        MiaoshaMessage miaoshaMessage = new MiaoshaMessage();
        miaoshaMessage.setGoodsId(goodsId);
        miaoshaMessage.setUser(user);
        mqSender.sendMiaosha(miaoshaMessage);
        return Result.success(0);//排队中

    }

    /**
     *  取秒杀结果
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> getMiaoshaResult(Model model, MiaoshaUser user, @RequestParam("goodsId") long goodsId){
        model.addAttribute("user", user);
        if(user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        long res = miaoshaService.getMiaoshaResult(user.getId(), goodsId);
        return ResultUtil.success(res);
    }

    /**
     * GET:是幂等的  POST:是对数据产生影响的
     * 对秒杀接口进行静态化优化
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/do_miaosha3", method = RequestMethod.POST)
    @ResponseBody
    public Result<OrderInfo> doMiaosha3(Model model, MiaoshaUser user, @RequestParam("goodsId") long goodsId) {
        /*model.addAttribute("user",user);
        //1 判断用户是否登录,未登录直接踢到登录页面
        if (user == null) {
            return  ResultUtil.error(CodeMsg.SESSION_ERROR.getMsg());
        }
        //2 判断商品库存是否还有
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        if (goodsVo.getStockCount() <= 0) {//拿秒杀商品库存来算，用小于等于0，是为了防止库存减多了？暂时还没明白。是否和缓存击穿有关？
            return ResultUtil.error(CodeMsg.MIAO_SHA_OVER.getMsg());
        }

        //3 判断是否秒杀成功,防止一人秒杀多个订单（判断是否重复秒杀）
        MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsVo.getId());
        if (miaoshaOrder != null) {
            return ResultUtil.error(CodeMsg.REPEAT_FAIL.getMsg());
        }

        //4 减库存,生成订单,写入秒杀订单表
        OrderInfo orderInfo = miaoshaService.miaosha(user, goodsVo);
        return ResultUtil.success(orderInfo);*/
        return null;//此行没用，就是为了不报错
    }

    /**
     * 将每步都需要从缓存中验证用户是否存在的操作提出到自定义的参数解析器
     * @param model
     * @param user
     * @return
     */
    @RequestMapping("/do_miaosha2")
    public String doMiaosha2(Model model, MiaoshaUser user, @RequestParam("goodsId") long goodsId) {
        model.addAttribute("user",user);
        //1 判断用户是否登录,未登录直接踢到登录页面
        if (user == null) {
            return  "login";
        }
        //2 判断商品库存是否还有
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        if (goodsVo.getStockCount() <= 0) {//拿秒杀商品库存来算
            model.addAttribute("errMsg", CodeMsg.MIAO_SHA_OVER.getMsg());
            return "miaosha_fail";
        }

        //3 判断是否秒杀成功,防止一人秒杀多个订单
        MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsVo.getId());
        if (miaoshaOrder != null) {
            model.addAttribute("errMsg", CodeMsg.REPEAT_FAIL.getMsg());
            return "miaosha_fail";
        }

        //4 减库存,生成订单,写入秒杀订单表
        OrderInfo orderInfo = miaoshaService.miaosha(user, goodsVo);
        model.addAttribute("orderInfo", orderInfo);
        model.addAttribute("goods", goodsVo);
        return "order_detail";
    }

    @RequestMapping("/reset")
    @ResponseBody
    public Result<Boolean> reset(Model model){
        List<GoodsVo> goodsVos = goodsService.listGoodsVo();
        for (GoodsVo goodsVo: goodsVos) {
            goodsVo.setStockCount(10);
            redisService.set(GoodsKey.goodsStock, ""+goodsVo.getId(), 10);
            localOverMap.put(goodsVo.getId(), false);
        }

        redisService.delete(OrderKey.getOrderInfoByUserIdGoodsId);
        redisService.delete(MiaoshaKey.isGoodsOver);
        miaoshaService.reset(goodsVos);
        return ResultUtil.success(true);
    }

}
