package tks.gv.rentservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import tks.gv.rentservice.security.config.RabbitConfiguration;
import tks.gv.rentservice.data.dto.ClientRegisterDTO;
import tks.gv.rentservice.data.dto.LoginDTO;
import tks.gv.rentservice.data.mappers.dto.ClientMessageMapper;
import tks.gv.rentservice.ui.clients.ports.DeleteClientUseCase;
import tks.gv.rentservice.ui.clients.ports.RegisterClientUseCase;

@Slf4j
@Component
public class RentServiceSubscriber {

    private final RentServicePublisher producer;
    private final RegisterClientUseCase registerClientUseCase;
    private final DeleteClientUseCase deleteClientUseCase;

    @Autowired
    public RentServiceSubscriber(RentServicePublisher producer,
                                 RegisterClientUseCase registerClientUseCase,
                                 DeleteClientUseCase deleteClientUseCase) {
        this.producer = producer;
        this.registerClientUseCase = registerClientUseCase;
        this.deleteClientUseCase = deleteClientUseCase;
    }

    @RabbitListener(queues = RabbitConfiguration.QUEUE_NAME_CREATE)
    public void handleCreationMessage(@Payload ClientRegisterDTO receivedDTO,
                              @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String key,
                              @Header(AmqpHeaders.RECEIVED_USER_ID) String senderId) {
        if (!senderId.equals(RentServicePublisher.SENDER_ID)) {
            try {
                log.info("Received key: {}", key);

                Client client = registerClientUseCase.registerClient(ClientMessageMapper.fromDTO(receivedDTO));
                log.info("Client {} successfully registered", client.getLogin());
            } catch (Exception e) {
                log.error("Exception during User creation occurred - {}", e.getMessage());
                producer.sendDelete(receivedDTO.getLogin());
            }
        } else {
            log.warn("Own subscriber invoked!");
        }
    }

    @RabbitListener(queues = RabbitConfiguration.QUEUE_NAME_DELETE)
    public void handleDeletionMessage(@Payload LoginDTO message,
                                      @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String key,
                                      @Header(AmqpHeaders.RECEIVED_USER_ID) String senderId) {
        if (!senderId.equals(RentServicePublisher.SENDER_ID)) {
            try {
                log.info("Received key: {}", key);

                deleteClientUseCase.deleteClient(message.login());
                log.info("Client {} successfully deleted", message.login());
            } catch (Exception e) {
                log.error("Exception during Client deletion occurred - {}", e.getMessage());
            }
        } else {
            log.warn("Own subscriber invoked!");
        }
    }
}
