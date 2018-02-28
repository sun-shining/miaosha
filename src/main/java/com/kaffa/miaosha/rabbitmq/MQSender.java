package com.kaffa.miaosha.rabbitmq;

import com.kaffa.miaosha.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQSender {

    public static final Logger logger = LoggerFactory.getLogger(MQSender.class);

    @Autowired
    AmqpTemplate amqpTemplate;

    /**
     *
     * @param message
     */
    public void send(Object message){
        String msg = RedisService.obj2String(message);
        logger.info("send message:" + msg);
        //要填写发送到哪个队列啊小伙子，直接扔消息，鬼知道你扔哪儿去了
        amqpTemplate.convertAndSend(MQConfig.QUEUE, msg);
    }

    /**
     *
     * @param message
     */
    public void sendMiaosha(Object message){
        String msg = RedisService.obj2String(message);
        logger.info("send miaosha message:" + msg);
        //要填写发送到哪个队列啊小伙子，直接扔消息，鬼知道你扔哪儿去了
        amqpTemplate.convertAndSend(MQConfig.MIAOSHA_QUEUE, msg);
    }
}
