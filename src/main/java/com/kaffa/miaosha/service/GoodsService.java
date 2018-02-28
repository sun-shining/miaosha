package com.kaffa.miaosha.service;

import com.kaffa.miaosha.dao.GoodsDao;
import com.kaffa.miaosha.domain.GoodsVo;
import com.kaffa.miaosha.domain.MiaoshaGoods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Administrator on 2018/1/17.
 */
@Service
public class GoodsService {


    @Autowired
    private GoodsDao goodsDao;

    public List<GoodsVo> listGoodsVo() {
        return goodsDao.listGoodsVo();
    }

    public GoodsVo getGoodsVoByGoodsId(long goodsId) {
        return goodsDao.getGoodsVoByGoodsId(goodsId);
    }

    public Boolean reduceStock(GoodsVo goods) {
        MiaoshaGoods goodsVo = new MiaoshaGoods();
        goodsVo.setId(goods.getId());
        return goodsDao.reduceStock(goodsVo)>0;
    }

    public void resetStock(List<GoodsVo> goodsVos) {
        for(GoodsVo goods : goodsVos ) {
            MiaoshaGoods g = new MiaoshaGoods();
            g.setGoodsId(goods.getId());
            g.setStockCount(goods.getStockCount());
            goodsDao.resetStock(g);
        }
    }
}
