package tks.gv.rabbit.second;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import tks.gv.rabbit.second.config.RabbitConfiguration;

@Slf4j
@Component
public class RentServiceSubscriber {

    private final RentServicePublisher producer;

    @Autowired
    public RentServiceSubscriber(RentServicePublisher producer) {
        this.producer = producer;
    }

    @RabbitListener(queues = RabbitConfiguration.QUEUE_NAME)
    public void handleMessage(@Payload String message,
                              @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String key) {
        try {
//            throw new RuntimeException("Dua Lipa");
            log.info("Second invoked custom");
            log.info("Received <{}>", message);
            log.info("Key: {}", key);
        } catch (Exception e) {
            log.error("Exception during User creation occurred - {}", e.getMessage());
            producer.sendDelete("Hello RabbitSecond - Deletion :>");
        }

    }
}
