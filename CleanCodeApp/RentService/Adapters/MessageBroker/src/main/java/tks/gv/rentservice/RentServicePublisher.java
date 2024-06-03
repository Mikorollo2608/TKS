package tks.gv.rentservice;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tks.gv.rentservice.config.RabbitConfiguration;

@Component
public class RentServicePublisher {
    public final static String SENDER_ID = "Producer_No2";

    private final static String ROUTING_KEY_DELETE = "users.back.delete";

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public RentServicePublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendDelete(String messageContent) {
        MessageProperties properties = new MessageProperties();
        properties.setHeader(AmqpHeaders.RECEIVED_USER_ID, SENDER_ID);
        Message message = MessageBuilder.withBody(messageContent.getBytes())
                .andProperties(properties)
                .build();
        rabbitTemplate.convertAndSend(RabbitConfiguration.TOPIC_EXCHANGE_NAME, ROUTING_KEY_DELETE, message);
    }
}
