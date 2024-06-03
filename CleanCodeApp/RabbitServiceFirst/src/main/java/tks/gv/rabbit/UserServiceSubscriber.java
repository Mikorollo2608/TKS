package tks.gv.rabbit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import tks.gv.rabbit.config.RabbitConstsFirst;

@Slf4j
@Component
public class UserServiceSubscriber {

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = RabbitConstsFirst.QUEUE_NAME_BACK, durable = "true"),
            exchange = @Exchange(value = RabbitConstsFirst.TOPIC_EXCHANGE_NAME, type = ExchangeTypes.TOPIC),
            key = RabbitConstsFirst.ROUTING_KEY_PREFIX_BACK
    ))
    public void handleMessage(@Payload String message,
                              @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String key,
                              @Header(AmqpHeaders.RECEIVED_USER_ID) String senderId) {
        if (!senderId.equals(UserServicePublisher.SENDER_ID)) {
            log.info("First invoked custom");
            log.info("Received <{}>", message);
            log.info("Key: {}", key);
            log.info("SenderId: {}", senderId);
        } else {
            log.warn("Own consumer invoked!");
        }

    }
}
