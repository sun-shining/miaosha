package com.kaffa.miaosha.controller;

import com.kaffa.miaosha.domain.GoodsDetailVo;
import com.kaffa.miaosha.domain.GoodsVo;
import com.kaffa.miaosha.domain.MiaoshaUser;
import com.kaffa.miaosha.redis.GoodsKey;
import com.kaffa.miaosha.redis.RedisService;
import com.kaffa.miaosha.result.Result;
import com.kaffa.miaosha.service.GoodsService;
import com.kaffa.miaosha.service.MiaoshaUserService;
import com.kaffa.miaosha.utils.ResultUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring4.context.SpringWebContext;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private MiaoshaUserService miaoshaUserService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private RedisService redisService;
    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;
    @Autowired
    ApplicationContext appllicationContext;


    /**
     * 将每步都需要从缓存中验证用户是否存在的操作提出到自定义的参数解析器
     * @param model
     * @param user
     * @return
     */
    @RequestMapping(value = "/to_list", produces = "text/html")
    @ResponseBody
    public String toGoods(HttpServletRequest request, HttpServletResponse response, Model model, MiaoshaUser user) {
        model.addAttribute("user",user);
        //1 从缓存中取页面
        String html = redisService.get(GoodsKey.goodsList, "", String.class);
        if (!StringUtils.isEmpty(html)) {
            return html;
        }

        List<GoodsVo> goodsVoList = goodsService.listGoodsVo();
        model.addAttribute("goodsList",goodsVoList);
//        return "goods_list";


        //2 缓存中没有,手动渲染
        //这种用法是spring5+springBoot2.0.7M的方式
//        WebContext webContext = new WebContext(request,response,
//                request.getServletContext(),request.getLocale(),model.asMap());
        SpringWebContext springWebContext = new SpringWebContext(request,response,request.getServletContext(),request.getLocale(),model.asMap(), appllicationContext);
        String newHtml = thymeleafViewResolver.getTemplateEngine().process("goods_list", springWebContext);
        if (!StringUtils.isEmpty(newHtml)){
            //3 将手动渲染的页面放入缓存中
            redisService.set(GoodsKey.goodsList,"", newHtml);
        }
        return newHtml;
    }

    /**
     * 服务端仅返回所需要的数据，其他均由页面动态组装
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/detail/{goodsId}")
    @ResponseBody
    public Result<GoodsDetailVo> goodsDetail(Model model, MiaoshaUser user,
                                             @PathVariable("goodsId")long goodsId) {

        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        long startAt = goodsVo.getStartDate().getTime();
        long endAt = goodsVo.getEndDate().getTime();
        long now = System.currentTimeMillis();

        int miaoshaStatus = 0;
        int remainSeconds = 0;
        if (now < startAt){
            miaoshaStatus = 0; //秒杀未开始
            remainSeconds = (int)(startAt - now)/1000;
        }else if (now > endAt){
            miaoshaStatus = 2; //秒杀结束
            remainSeconds = -1;
        } else {
            miaoshaStatus = 1; //秒杀进行中
            remainSeconds = 0;
        }
        GoodsDetailVo goodsDetailVo = new GoodsDetailVo();
        goodsDetailVo.setGoodsVo(goodsVo);
        goodsDetailVo.setMiaoshaStatus(miaoshaStatus);
        goodsDetailVo.setRemainSeconds(remainSeconds);
        goodsDetailVo.setUser(user);
        return ResultUtil.success(goodsDetailVo);
    }

    @RequestMapping(value = "/to_detail2/{goodsId}", produces = "text/html")
    @ResponseBody
    public String goodsDetail2(HttpServletRequest request, HttpServletResponse response
            ,Model model, MiaoshaUser user,
                              @PathVariable("goodsId")long goodsId) {
        model.addAttribute("user",user);
        //        return "goods_detail";
        //1 从缓存中获取页面
        String html = redisService.get(GoodsKey.goodsDetail, String.valueOf(goodsId), String.class);
        if (!StringUtils.isEmpty(html)) {
            return html;
        }
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        long startAt = goodsVo.getStartDate().getTime();
        long endAt = goodsVo.getEndDate().getTime();
        long now = System.currentTimeMillis();

        int miaoshaStatus = 0;
        int remainSeconds = 0;
        if (now < startAt){
            miaoshaStatus = 0; //秒杀未开始
            remainSeconds = (int)(startAt - now)/1000;
        }else if (now > endAt){
            miaoshaStatus = 2; //秒杀结束
            remainSeconds = -1;
        } else {
            miaoshaStatus = 1; //秒杀进行中
            remainSeconds = 0;
        }
        model.addAttribute("miaoshaStatus",miaoshaStatus);
        model.addAttribute("remainSeconds",remainSeconds);
        model.addAttribute("goods",goodsVo);


        //2 如果缓存里没有,手动渲染返回,并将结果扔入缓存
        WebContext webContext = new WebContext(request,response,request.getServletContext(),request.getLocale(),model.asMap());
        String newHtml = thymeleafViewResolver.getTemplateEngine().process("goods_detail", webContext);
        if (!StringUtils.isEmpty(newHtml)){
            redisService.set(GoodsKey.goodsDetail,String.valueOf(goodsId),newHtml);
        }
        return newHtml;
    }

}
