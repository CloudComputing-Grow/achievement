package com.grow.achievement.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String USER_EVENTS_EXCHANGE = "user.events";
    public static final String USER_EVENTS_QUEUE = "achievement-service.user-events.queue";
    public static final String USER_DELETED_ROUTING_KEY = "user.deleted";

    @Bean
    public TopicExchange userEventsExchange() {
        return new TopicExchange(USER_EVENTS_EXCHANGE, true, false);
    }

    @Bean
    public Queue userEventsQueue() {
        return new Queue(USER_EVENTS_QUEUE, true);
    }

    @Bean
    public Binding userDeletedBinding() {
        return BindingBuilder
                .bind(userEventsQueue())
                .to(userEventsExchange())
                .with(USER_DELETED_ROUTING_KEY);
    }
}
