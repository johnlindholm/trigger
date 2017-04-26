package se.trigger.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import se.trigger.devices.AbstractDevice;
import se.trigger.devices.BreakBeam;
import se.trigger.devices.DeviceType;
import se.trigger.devices.Magnet;
import se.trigger.devices.Motion;
import se.trigger.devices.Temperature;

/**
 * Created by john on 2017-04-21.
 */
@Configuration
@EnableRabbit
public class AppConfig {

    public final static String EXCHANGE_NAME = "trigger.devices";
    public final static String ROUTING_KEY_HEARTBEAT = "heartbeat";
    public final static String ROUTING_KEY_DEVICE_MESSAGE_PREFIX = "device.";
    public final static String ROUTING_KEY_DEVICE_MESSAGE_PATTERN = ROUTING_KEY_DEVICE_MESSAGE_PREFIX + "*";

    @Value("${mq.hostname}")
    private String mqHostname;

    @Value("${mq.username}")
    private String mqUsername;

    @Value("${mq.password}")
    private String mqPassword;

    @Value("${1wire.type}")
    private String oneWireDeviceType;

    @Bean
    Queue heartbeatQueue() {
        return new Queue("heartbeatQueue");
    }

    @Bean
    Queue deviceMessageQueue() {
        return new Queue("deviceMessageQueue");
    }

    @Bean
    TopicExchange deviceExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    Binding heartbeatQueueBinding() {
        return BindingBuilder.bind(heartbeatQueue()).to(deviceExchange()).with(ROUTING_KEY_HEARTBEAT);
    }

    @Bean
    Binding deviceMessageQueueBinding() {
        return BindingBuilder.bind(deviceMessageQueue()).to(deviceExchange()).with(ROUTING_KEY_DEVICE_MESSAGE_PATTERN);
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

    @Bean
    public TaskExecutor taskExecutor() {
        return new SimpleAsyncTaskExecutor();
    }

    @Bean
    public AbstractDevice device() {
        AbstractDevice device = null;
        DeviceType type = DeviceType.valueOf(oneWireDeviceType.toUpperCase());
        switch (type) {
            case MAGNET:
                device = new Magnet();
                break;
            case MOTION:
                device = new Motion();
                break;
            case BREAK_BEAM:
                device = new BreakBeam();
                break;
            case TEMPERATURE:
                device = new Temperature();
                break;
        }
        return device;
    }

//    @Bean
//    SimpleMessageListenerContainer container(MessageListenerAdapter listenerAdapter) {
//        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
//        container.setConnectionFactory(connectionFactory());
//        container.setQueueNames(queueName);
//        container.setMessageListener(listenerAdapter);
//        return container;
//    }

//    @Bean
//    MessageListenerAdapter listenerAdapter(Receiver receiver) {
//        device = MessageListenerAdapter(receiver, "receiveMessage");
//    }

}
