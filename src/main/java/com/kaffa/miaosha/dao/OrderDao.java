package com.kaffa.miaosha.dao;

import com.kaffa.miaosha.domain.MiaoshaOrder;
import com.kaffa.miaosha.domain.OrderInfo;
import org.apache.ibatis.annotations.*;

/**
 * Created by Administrator on 2018/2/4.
 */
@Mapper
public interface OrderDao {

    @Select("SELECT * FROM miaosha_order t WHERE t.goods_id =#{goodsId}  AND t.user_id =#{userId} ")
    MiaoshaOrder getMiaoshaOrderByUserIdGoodsId(@Param("goodsId")  Long goodsId, @Param("userId") Long userId);

    @Insert("insert into order_info(user_id, goods_id, goods_name, goods_count, goods_price, order_channel, status, create_date)values("
            + "#{userId}, #{goodsId}, #{goodsName}, #{goodsCount}, #{goodsPrice}, #{orderChannel},#{status},#{createDate} )")
    @SelectKey(keyColumn="id", keyProperty="id", resultType=long.class, before=false, statement="select last_insert_id()")
    long insert(OrderInfo orderInfo);

    @Insert("insert into miaosha_order (user_id, order_id, goods_id) values(#{userId}, #{orderId}, #{goodsId})")
    void insertMiaoshaOrder(MiaoshaOrder miaoshaOrder);

    @Select("select * from order_info where id=#{orderId}")
    OrderInfo getOrderInfoById(@Param("orderId") long orderId);

    @Delete("delete from order_info")
    void deleteOrders();

    @Delete("delete from miaosha_order")
    void deleteMiaoshaOrders();
}
