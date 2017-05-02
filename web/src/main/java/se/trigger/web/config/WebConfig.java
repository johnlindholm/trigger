package se.trigger.web.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

/**
 * Created by john on 2017-04-27.
 */
@Configuration
@EnableRabbit
public class WebConfig {

    public final static String QUEUE_HEARTBEAT_NAME = "heartbeatQueue_" + UUID.randomUUID().toString();
    public final static String QUEUE_DEVICE_MESSAGE_NAME = "deviceMessageQueue_" + UUID.randomUUID().toString();
    public final static String EXCHANGE_NAME = "trigger.direct";
    public final static String ROUTING_KEY_HEARTBEAT = "device.heartbeat";
    public final static String ROUTING_KEY_DEVICE_MESSAGE = "device.messages";

    @Value("${mq.hostname}")
    private String mqHostname;

    @Value("${mq.username}")
    private String mqUsername;

    @Value("${mq.password}")
    private String mqPassword;

    @Bean
    Queue heartbeatQueue() {
        return new Queue(QUEUE_HEARTBEAT_NAME, false, true, true);
    }

    @Bean
    Queue deviceMessageQueue() {
        return new Queue(QUEUE_DEVICE_MESSAGE_NAME, false, true, true);
    }

    @Bean
    DirectExchange exchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    Binding heartbeatQueueBinding() {
        return BindingBuilder.bind(heartbeatQueue()).to(exchange()).with(ROUTING_KEY_HEARTBEAT);
    }

    @Bean
    Binding deviceMessageQueueBinding() {
        return BindingBuilder.bind(deviceMessageQueue()).to(exchange()).with(ROUTING_KEY_DEVICE_MESSAGE);
    }

    @Bean
    ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(mqHostname);
        connectionFactory.setUsername(mqUsername);
        connectionFactory.setPassword(mqPassword);
        return connectionFactory;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        factory.setConcurrentConsumers(3);
        factory.setMaxConcurrentConsumers(10);
        return factory;
    }

}
