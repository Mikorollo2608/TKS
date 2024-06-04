package tks.gv.userservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tks.gv.userservice.config.RabbitConsts;
import tks.gv.userservice.data.dto.LoginDTO;
import tks.gv.userservice.data.mappers.dto.ClientMessageMapper;

@Slf4j
@Component
public class UserServicePublisher {
    public final static String SENDER_ID = "Producer_No1";

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public UserServicePublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendCreate(Client messageContent) {
        log.info("Sending creation message for client - {}", messageContent.getLogin());
        MessageProperties properties = new MessageProperties();
        properties.setHeader(AmqpHeaders.RECEIVED_USER_ID, SENDER_ID);
        Message message = rabbitTemplate.getMessageConverter()
                .toMessage(ClientMessageMapper.toClientRegisterInSecondServiceDTO(messageContent), properties);

        rabbitTemplate.convertAndSend(RabbitConsts.TOPIC_EXCHANGE_NAME, RabbitConsts.ROUTING_KEY_CREATE, message);
    }

    public void sendDelete(String login) {
        log.info("Sending deletion message for client - {}", login);
        MessageProperties properties = new MessageProperties();
        properties.setHeader(AmqpHeaders.RECEIVED_USER_ID, SENDER_ID);
        Message message = rabbitTemplate.getMessageConverter().toMessage(new LoginDTO(login), properties);

        rabbitTemplate.convertAndSend(RabbitConsts.TOPIC_EXCHANGE_NAME, RabbitConsts.ROUTING_KEY_DELETE, message);
    }
}
