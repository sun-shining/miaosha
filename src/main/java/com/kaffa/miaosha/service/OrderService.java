package com.kaffa.miaosha.service;

import com.kaffa.miaosha.dao.OrderDao;
import com.kaffa.miaosha.domain.GoodsVo;
import com.kaffa.miaosha.domain.MiaoshaOrder;
import com.kaffa.miaosha.domain.MiaoshaUser;
import com.kaffa.miaosha.domain.OrderInfo;
import com.kaffa.miaosha.redis.OrderKey;
import com.kaffa.miaosha.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created by Administrator on 2018/2/4.
 */
@Service
public class OrderService {

    @Autowired
    private OrderDao orderDao;
    @Autowired
    private RedisService redisService;

    /**
     * 查询用户是否已经秒杀过了
     * @param userId
     * @param goodsId
     * @return 秒杀订单对像
     */
    public MiaoshaOrder getMiaoshaOrderByUserIdGoodsId(Long userId, Long goodsId) {
//        return orderDao.getMiaoshaOrderByUserIdGoodsId(userId, GoodsId);
        return redisService.get(OrderKey.getOrderInfoByUserIdGoodsId, ""+userId+"_"+goodsId, MiaoshaOrder.class);
    }

    @Transactional
    public OrderInfo createOrder(MiaoshaUser user, GoodsVo goodsVo) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goodsVo.getId());
        orderInfo.setGoodsName(goodsVo.getGoodsName());
        orderInfo.setGoodsPrice(goodsVo.getMiaoshaPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setUserId(user.getId());
        //mybatis insert返回的是什么啊？难道不是插入成功后那条数据的id？
        //
        orderDao.insert(orderInfo);

        MiaoshaOrder miaoshaOrder = new MiaoshaOrder();
        miaoshaOrder.setGoodsId(goodsVo.getId());
        miaoshaOrder.setUserId(user.getId());
        miaoshaOrder.setOrderId(orderInfo.getId());
        orderDao.insertMiaoshaOrder(miaoshaOrder);

        //创建秒杀订单成功后，将秒杀订单信息存入redis，用户取订单信息判断是否重复秒杀时，可少查一次数据库
        redisService.set(OrderKey.getOrderInfoByUserIdGoodsId, ""+user.getId()+"_"+goodsVo.getId(), miaoshaOrder);
        return orderInfo;
    }

    public OrderInfo getOrderById(long orderId) {
        if (orderId == 0) {
            return null;
        }
        return orderDao.getOrderInfoById(orderId);
    }

    public void deleteOrders() {
        orderDao.deleteOrders();
        orderDao.deleteMiaoshaOrders();
    }
}
