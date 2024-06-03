package tks.gv.rabbit;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static tks.gv.rabbit.config.RabbitConstsFirst.ROUTING_KEY_CREATE;
import static tks.gv.rabbit.config.RabbitConstsFirst.ROUTING_KEY_DELETE;
import static tks.gv.rabbit.config.RabbitConstsFirst.TOPIC_EXCHANGE_NAME;

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
        rabbitTemplate.convertAndSend(TOPIC_EXCHANGE_NAME, ROUTING_KEY_CREATE, message);
    }

    public void sendDelete(String messageContent) {
        MessageProperties properties = new MessageProperties();
        properties.setHeader(AmqpHeaders.RECEIVED_USER_ID, SENDER_ID);
        Message message = MessageBuilder.withBody(messageContent.getBytes())
                .andProperties(properties)
                .build();
        rabbitTemplate.convertAndSend(TOPIC_EXCHANGE_NAME, ROUTING_KEY_DELETE, message);
    }
}
