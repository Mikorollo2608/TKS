package tks.gv.userservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import tks.gv.userservice.config.RabbitConsts;
import tks.gv.userservice.data.dto.LoginDTO;
import tks.gv.userservice.userinterface.ports.clients.DeleteClientUseCase;

@Slf4j
@Component
public class UserServiceSubscriber {

    private final DeleteClientUseCase deleteClientUseCase;

    @Autowired
    public UserServiceSubscriber(DeleteClientUseCase deleteClientUseCase) {
        this.deleteClientUseCase = deleteClientUseCase;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = RabbitConsts.QUEUE_NAME_BACK, durable = "true"),
            exchange = @Exchange(value = RabbitConsts.TOPIC_EXCHANGE_NAME, type = ExchangeTypes.TOPIC),
            key = RabbitConsts.ROUTING_KEY_BACK_DELETE
    ))
    public void handleDeletionMessage(@Payload LoginDTO message,
                              @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String key,
                              @Header(AmqpHeaders.RECEIVED_USER_ID) String senderId) {
        if (!senderId.equals(UserServicePublisher.SENDER_ID)) {
            try {
                log.info("Received key: {}", key);

                deleteClientUseCase.deleteClient(message.login());
                log.info("Client {} successfully deleted", message.login());
            } catch (Exception e) {
                log.error("Exception during User deletion occurred - {}", e.getMessage());
            }
        } else {
            log.warn("Own subscriber invoked!");
        }
    }
}
