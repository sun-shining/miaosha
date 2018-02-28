package com.kaffa.miaosha.service;

import com.kaffa.miaosha.domain.GoodsVo;
import com.kaffa.miaosha.domain.MiaoshaOrder;
import com.kaffa.miaosha.domain.MiaoshaUser;
import com.kaffa.miaosha.domain.OrderInfo;
import com.kaffa.miaosha.redis.MiaoshaKey;
import com.kaffa.miaosha.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Administrator on 2018/2/4.
 */
@Service
public class MiaoshaService {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private OrderService orderService;
    @Autowired
    private RedisService redisService;

    /**
     *  减库存,生成订单,写入秒杀订单表
     * @param user
     * @param goodsVo
     * @return
     */
    @Transactional
    public OrderInfo miaosha(MiaoshaUser user, GoodsVo goodsVo) {
        Boolean res = goodsService.reduceStock(goodsVo);
        if (res) {
            return orderService.createOrder(user, goodsVo);
        }else {
            setGoodsOver(goodsVo.getId());
            return null;
        }

    }

    public long getMiaoshaResult(Long userId, long goodsId) {
        MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdGoodsId(userId, goodsId);
        if (miaoshaOrder != null) {
            return miaoshaOrder.getOrderId(); //秒杀成功了
        }else {
            boolean goodsOver = getGoodsOver(goodsId);
            if (goodsOver){//有就是减库存失败了
                return -1;
            }else {
                return 0;
            }
        }
    }

    /**
     * 假如redis中有减库存失败的这个key,就证明减库存失败了
     * @param goodsId
     * @return
     */
    private boolean getGoodsOver(long goodsId) {
        return redisService.contains(MiaoshaKey.isGoodsOver, ""+goodsId);
    }

    /**
     * 标识是否减库存成功的
     * @param goodsId
     */
    private void setGoodsOver(Long goodsId) {
        redisService.set(MiaoshaKey.isGoodsOver, ""+goodsId, true);
    }

    /**
     * 重置环境数据：0，还原秒杀商品库存 1，删除订单 2，删除秒杀订单
     * @param goodsVos
     */
    public void reset(List<GoodsVo> goodsVos) {
        goodsService.resetStock(goodsVos);
        orderService.deleteOrders();
    }
}
