package com.kaffa.miaosha.dao;

import com.kaffa.miaosha.domain.GoodsVo;
import com.kaffa.miaosha.domain.MiaoshaGoods;
import com.kaffa.miaosha.domain.MiaoshaUser;
import org.apache.ibatis.annotations.*;
import org.springframework.web.bind.annotation.PathVariable;

import javax.websocket.server.PathParam;
import java.util.List;

/**
 * Created by Administrator on 2018/1/17.
 */
@Mapper
public interface GoodsDao {

    @Select("SELECT g.*,mg.miaosha_price,mg.stock_count,mg.start_date,mg.end_date  FROM miaosha_goods mg LEFT JOIN goods g ON mg.goods_id=g.id")
    public List<GoodsVo> listGoodsVo();

    @Select("SELECT g.*,mg.miaosha_price,mg.stock_count,mg.start_date,mg.end_date  FROM miaosha_goods mg LEFT JOIN goods g ON mg.goods_id=g.id where g.id=#{goodsId}")
    public GoodsVo getGoodsVoByGoodsId(@PathParam("goodsId") long goodsId);

    @Update("UPDATE miaosha_goods t SET t.stock_count = t.stock_count-1 WHERE t.goods_id = #{id} and t.stock_count>0")
    public int reduceStock(MiaoshaGoods goodsVo);

    @Update("UPDATE miaosha_goods t SET t.stock_count = #{stockCount} WHERE t.goods_id = #{goodsId}")
    public void resetStock(MiaoshaGoods g);
}
