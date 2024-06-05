package tks.gv.rentservice.security.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {

    public static final String TOPIC_EXCHANGE_NAME = "users-exchange";

    public static final String QUEUE_NAME_CREATE = "users-queue-create";
    public static final String ROUTING_CREATE_KEY = "users.forward.create";


    public static final String QUEUE_NAME_DELETE = "users-queue-delete";
    public static final String ROUTING_DELETE_KEY = "users.forward.delete";


    public static final String QUEUE_NAME_BACK = "users-queue-back-delete";
    public static final String ROUTING_BACK_DELETE_KEY = "users.back.delete";


    @Bean("usersQueueCreation")
    public Queue usersQueueCreate() {
        return new Queue(QUEUE_NAME_CREATE, true);
    }

    @Bean("usersQueueDeletion")
    public Queue usersQueueDelete() {
        return new Queue(QUEUE_NAME_DELETE, true);
    }

    @Bean("usersQueueBackDeletion")
    public Queue usersQueueBackDelete() {
        return new Queue(QUEUE_NAME_BACK, true);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(TOPIC_EXCHANGE_NAME);
    }

    @Bean
    public Binding bindingCreation(@Qualifier("usersQueueCreation") Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_CREATE_KEY);
    }

    @Bean
    public Binding bindingDeletion(@Qualifier("usersQueueDeletion") Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_DELETE_KEY);
    }

    @Bean
    public Binding bindingBackDeletion(@Qualifier("usersQueueBackDeletion") Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_BACK_DELETE_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
