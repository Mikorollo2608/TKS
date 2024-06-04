package tks.gv.rentservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tks.gv.rentservice.config.RabbitConfiguration;
import tks.gv.rentservice.data.dto.LoginDTO;

@Slf4j
@Component
public class RentServicePublisher {
    public final static String SENDER_ID = "Producer_No2";

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public RentServicePublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendDelete(String login) {
        log.info("Sending delete message for client - {}", login);
        MessageProperties properties = new MessageProperties();
        properties.setHeader(AmqpHeaders.RECEIVED_USER_ID, SENDER_ID);
        Message message = rabbitTemplate.getMessageConverter().toMessage(new LoginDTO(login), properties);

        rabbitTemplate.convertAndSend(RabbitConfiguration.TOPIC_EXCHANGE_NAME, RabbitConfiguration.ROUTING_BACK_DELETE_KEY, message);
    }
}
