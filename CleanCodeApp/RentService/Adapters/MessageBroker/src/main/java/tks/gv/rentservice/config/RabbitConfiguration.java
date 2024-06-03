package tks.gv.rentservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {

    public static final String TOPIC_EXCHANGE_NAME = "users-exchange";

    public static final String QUEUE_NAME = "users-queue";
    public static final String QUEUE_NAME_BACK = "users-queue-back";

    @Bean("usersQueue")
    public Queue usersQueue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean("usersQueueBack")
    public Queue usersQueueBack() {
        return new Queue(QUEUE_NAME_BACK, true);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(TOPIC_EXCHANGE_NAME);
    }

    @Bean
    public Binding binding(@Qualifier("usersQueue") Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("users.forward.#");
    }

    @Bean
    public Binding bindingBack(@Qualifier("usersQueueBack") Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("users.back.#");
    }
}
