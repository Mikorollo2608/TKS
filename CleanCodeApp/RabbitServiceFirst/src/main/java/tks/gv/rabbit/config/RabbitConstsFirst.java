package tks.gv.rabbit.config;

public class RabbitConstsFirst {

    public static final String TOPIC_EXCHANGE_NAME = "users-exchange";
    public static final String QUEUE_NAME = "users-queue";
    public static final String QUEUE_NAME_BACK = "users-queue-back";
    public final static String ROUTING_KEY_PREFIX_FORWARD = "users.forward.#";
    public final static String ROUTING_KEY_PREFIX_BACK = "users.back.#";
    public final static String ROUTING_KEY_CREATE = "users.forward.create";
    public final static String ROUTING_KEY_DELETE = "users.forward.delete";
}
