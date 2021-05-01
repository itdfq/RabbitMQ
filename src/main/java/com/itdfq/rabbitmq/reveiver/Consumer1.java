package com.itdfq.rabbitmq.reveiver;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @Author GocChin
 * @Date 2021/5/1 22:26
 * @Blog: itdfq.com
 * @QQ: 909256107
 * @Desrcipt:监听类
 */
@Component
@RabbitListener(queues = "queue1") //监听的队列
public class Consumer1 {

    @RabbitHandler  //根据这个注解进行执行方法
    public void process(String msg){
        System.out.println("Consumer1-Receiver:"+msg);
    }
}
