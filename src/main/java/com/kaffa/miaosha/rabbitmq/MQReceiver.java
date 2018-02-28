package com.kaffa.miaosha.rabbitmq;

import com.kaffa.miaosha.domain.*;
import com.kaffa.miaosha.redis.RedisService;
import com.kaffa.miaosha.service.GoodsService;
import com.kaffa.miaosha.service.MiaoshaService;
import com.kaffa.miaosha.service.OrderService;
import com.kaffa.miaosha.utils.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQReceiver {

    public static final Logger logger = LoggerFactory.getLogger(MQReceiver.class);
    @Autowired
    private MiaoshaService miaoshaService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private OrderService orderService;

    @RabbitListener(queues = MQConfig.QUEUE)
    public void receive(String msg){
        logger.info("receive message :"+msg);
    }

    /**
     * 接到排队中的消息时要在此处处理业务逻辑的
     * @param msg
     */
    @RabbitListener(queues = MQConfig.MIAOSHA_QUEUE)
    public void receiveMiaosha(String msg){
        logger.info("receive miaosha message :"+msg);
        MiaoshaMessage miaoshaMessage = RedisService.string2Obj(msg, MiaoshaMessage.class);
        long goodsId = miaoshaMessage.getGoodsId();
        MiaoshaUser user = miaoshaMessage.getUser();

        //这里再真正的查库看一下还有没有货
        GoodsVo goodsVoByGoodsId = goodsService.getGoodsVoByGoodsId(goodsId);
        if (goodsVoByGoodsId.getStockCount() <= 0) {
            return;
        }

        //这里判断是否秒杀到了，防止多线程时一个人多次秒杀
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if (order != null) {
            return;
        }

        // 减库存,生成订单,写入秒杀订单表
        miaoshaService.miaosha(user, goodsVoByGoodsId);

    }
}
