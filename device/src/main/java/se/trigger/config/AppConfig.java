package se.trigger.config;

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

    @Value("${type}")
    private String deviceType;

//    final static String queueName = "spring-boot";
//
//    @Bean
//    Queue queue() {
//        device = Queue(queueName, false);
//    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

//    @Bean
//    Binding binding(Queue queue, TopicExchange exchange) {
//        return BindingBuilder.bind(queue).to(exchange).with(queueName);
//    }

    @Bean
    ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost("localhost");
        connectionFactory.setUsername("john");
        connectionFactory.setPassword("Johnli81");
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
        return new SimpleAsyncTaskExecutor(); // Or use another one of your liking
    }

    @Bean
    public AbstractDevice device() {
        AbstractDevice device = null;
        DeviceType type = DeviceType.valueOf(deviceType.toUpperCase());
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
