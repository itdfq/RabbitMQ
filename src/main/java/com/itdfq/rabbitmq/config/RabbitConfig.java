package com.itdfq.rabbitmq.config;



import com.itdfq.rabbitmq.reveiver.Receiver;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



/**
 * @Author GocChin
 * @Date 2021/5/1 21:22
 * @Blog: itdfq.com
 * @QQ: 909256107
 * @Descript: 配置类
 */
@Configuration
public class RabbitConfig {
    @Value("${spring.rabbitmq.addresses}")
    private String address;
    @Value("${spring.rabbitmq.port}")
    private String port;
    @Value("${spring.rabbitmq.username}")
    private String username;
    @Value("${spring.rabbitmq.password}")
    private String password;
    @Value("${spring.rabbitmq.virtual-host}")
    private String virtualHost;

    @Autowired
    private Receiver receiver;

    //连接工厂
    @Bean
    public ConnectionFactory connectionFactory(){
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses(address+":"+port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(virtualHost);
        //TODO  消息发送确认--回调
        connectionFactory.setPublisherConfirms(true);
        return  connectionFactory;

    }

    //RabbitAdmin类封装对RabbitMQ的管理操作
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory){

        return new RabbitAdmin(connectionFactory);
    }
    //使用Template
    @Bean
    public RabbitTemplate newRabbitTemplate(){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
        //设置监听确认mq（交换器）接受到信息
        rabbitTemplate.setConfirmCallback(confirmCallback());
        //添加监听 失败鉴定（路由没有收到）
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setReturnCallback(returnCallback());
        return rabbitTemplate;
    }

    //声明交换器
    //Direct交换器
    @Bean
    public DirectExchange DirectExchange(){
        return new DirectExchange("DirectExchange");
    }
    //topic交换器
    @Bean
    public TopicExchange TopicExchange(){
        return new TopicExchange("TopicExchange");
    }
    //Fanout交换器
    @Bean
    public FanoutExchange FanoutExchange(){
        return new FanoutExchange("FanoutExchange");
    }
    //申明队列
    @Bean
    public Queue queue1(){
        return new Queue("queue1");
    }
    @Bean
    public Queue queue2(){
        return new Queue("queue2");
    }
    //绑定关系
    //queue1与direct绑定
    @Bean
    public Binding bindingQueue1Direct(){
        return BindingBuilder.bind(queue1())
                .to(DirectExchange())
                .with("red");
    }
    //queue2与direct绑定
    @Bean
    public Binding bindingQueue2Direct(){
        return BindingBuilder.bind(queue1())
                .to(DirectExchange())
                .with("while");
    }
    //queue1与fanout绑定
    @Bean
    public Binding bindingQueue1Fanout(){
        return BindingBuilder.bind(queue1())
                .to(FanoutExchange());

    }
    //queue2与fanout绑定
    @Bean
    public Binding bindingQueue2Fanout(){
        return BindingBuilder.bind(queue1())
                .to(FanoutExchange());
    }
    //queue1与Topic绑定
    @Bean
    public Binding bindingQueue1Topic(){
        return BindingBuilder.bind(queue1())
                .to(TopicExchange())
                .with("red.*");

    }
    //queue2与Topic绑定
    @Bean
    public Binding bindingQueue2Topic(){
        return BindingBuilder.bind(queue1())
                .to(TopicExchange())
                .with("white.82");
    }
    //****************生产者发送确认********************
    @Bean
    public RabbitTemplate.ConfirmCallback confirmCallback(){
        return new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean b, String s) {
                if (b){
                    System.out.println("发送者确认发送给mq成功");
                }else{
                    System.out.println("发送者发送失败，考虑重发"+s);
                }
            }
        };
    }
    //****************失败通知********************
    //失败才通知，成功不通知
    @Bean
    public RabbitTemplate.ReturnCallback returnCallback(){
        return new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int i, String replayText, String exchange, String rountingKey) {
                System.out.println("无效路由信息，需要考虑另外处理");
                System.out.println("Returned replayText:"+replayText);
                System.out.println("Returned exchange:"+exchange);
                System.out.println("Returned rountingKey:"+rountingKey);
                String s = new String(message.getBody());
                System.out.println("Returned Message:"+s);
            }
        };
    }
    //****************消费者确认********************
    @Bean
    public SimpleMessageListenerContainer messageListenerContainer(){
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory());
        //绑定队列
        container.setQueues(queue1());
        //手动提交
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        //消费者确认方法
        container.setMessageListener(receiver);
        return container;
    }




}
