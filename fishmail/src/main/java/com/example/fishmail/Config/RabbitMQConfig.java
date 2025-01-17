package com.example.fishmail.Config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;

import java.util.TimeZone;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Konfiguracja RabbitaMQ
@Configuration
public class RabbitMQConfig {


    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
    return builder -> builder.timeZone(TimeZone.getTimeZone("Europe/Warsaw"));
    }
    // Bean do zdefiniowania message convertera!
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // Message konwerter
    @Bean
    public MessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.addTrustedPackages("com.example.fishmail.Models");
        converter.setJavaTypeMapper(typeMapper);
        return converter;
    }

    // BEAN Tworzący kolejke
    @Bean
    public Queue emailQueue(){
        return new Queue("email-queue", true);
    }
    
    // BEAN nasłuchujący wymianę wiadomości
    @Bean
    public DirectExchange emailExchange(){
        return new DirectExchange("email-exchange");
    }

    // BEAN przydzielający kolejkę do nasłuchiwania
    @Bean
    public Binding emailBinding(Queue emailQueue, DirectExchange emailExchange){
        return BindingBuilder.bind(emailQueue).to(emailExchange).with("email-routing-key");
    }
}
