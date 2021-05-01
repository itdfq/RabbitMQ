package com.itdfq.rabbitmq.controller;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



/**
 * @Author GocChin
 * @Date 2021/5/1 22:04
 * @Blog: itdfq.com
 * @QQ: 909256107
 */
@RestController
@RequestMapping("/rabbit")
public class RabbitProducter {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    /**
     * direct
     */
    @GetMapping("/direct")
    public String direct(@RequestParam(required = true) String Key){//mq消息的发送  true表示必须传递参数
        String sendMsg = "key("+Key+"),exchange(direct)-"+System.currentTimeMillis(); //currentTimeMillis时间戳
        System.out.println("DirectSender"+sendMsg);
        this.rabbitTemplate.convertAndSend("DirectExchange",Key,sendMsg);
        return "发送direct消息成功";

    }
    @RequestMapping("/asd")
    public String index(){
        return "jieshaole";
    }
    /**
     * topic
     */
    @GetMapping("/topic")
    public String topic(@RequestParam(required = true) String Key){//mq消息的发送  true表示必须传递参数
        String sendMsg = "key("+Key+"),exchange(topic)-"+System.currentTimeMillis();
        System.out.println("TopicSender"+sendMsg);
        this.rabbitTemplate.convertAndSend("TopicExchange",Key,sendMsg);
        return "发送topic消息成功";

    }
    /**
     * fanout
     */
    @GetMapping("/fanout")
    public String fanout(@RequestParam(required = true) String Key){//mq消息的发送  true表示必须传递参数
        String sendMsg = "key("+Key+"),exchange(fanout)-"+System.currentTimeMillis();
        System.out.println("FanoutSender"+sendMsg);
        this.rabbitTemplate.convertAndSend("FanoutExchange",Key,sendMsg);
        return "发送fanout消息成功";

    }

}
