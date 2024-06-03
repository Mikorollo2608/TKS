package tks.gv.userservice;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tks.gv.userservice.config.RabbitConsts;

@Component
public class UserServicePublisher {
    public final static String SENDER_ID = "Producer_No1";

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public UserServicePublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendCreate(String messageContent) {
        MessageProperties properties = new MessageProperties();
        properties.setHeader(AmqpHeaders.RECEIVED_USER_ID, SENDER_ID);
        Message message = MessageBuilder.withBody(messageContent.getBytes())
                .andProperties(properties)
                .build();
        rabbitTemplate.convertAndSend(RabbitConsts.TOPIC_EXCHANGE_NAME, RabbitConsts.ROUTING_KEY_CREATE, message);
    }

    public void sendDelete(String messageContent) {
        MessageProperties properties = new MessageProperties();
        properties.setHeader(AmqpHeaders.RECEIVED_USER_ID, SENDER_ID);
        Message message = MessageBuilder.withBody(messageContent.getBytes())
                .andProperties(properties)
                .build();
        rabbitTemplate.convertAndSend(RabbitConsts.TOPIC_EXCHANGE_NAME, RabbitConsts.ROUTING_KEY_DELETE, message);
    }
}
