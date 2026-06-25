package com.dataforge.core.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String QUEUE_NAME = "dataforge.generation.queue";
    public static final String EXCHANGE_NAME = "dataforge.exchange";
    public static final String ROUTING_KEY = "dataforge.routing.key";

    @Bean
    public Queue generationQueue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding binding(Queue generationQueue, DirectExchange exchange) {
        return BindingBuilder.bind(generationQueue).to(exchange).with(ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}