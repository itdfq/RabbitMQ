package com.itdfq.rabbitmq.reveiver;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @Author GocChin
 * @Date 2021/5/1 23:56
 * @Blog: itdfq.com
 * @QQ: 909256107
 * @Decript: 消费queue1
 */
@Component
public class Receiver implements ChannelAwareMessageListener {
    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        try {
            String msg=new String(message.getBody());
            System.out.println("Receiver>>>>>>>>接收到消息："+msg);
            try {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
                System.out.println("Receiver>>>>>>>>消息已消费");
            } catch (Exception e) {
                System.out.println(e.getMessage());
                channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,false);
                System.out.println("Receiver>>>>>>拒绝消息，要求MQ重新发送");
                e.printStackTrace();
                throw e;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

    }
}
