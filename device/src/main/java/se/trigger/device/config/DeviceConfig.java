package se.trigger.device.config;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;
import se.trigger.device.devices.AbstractDevice;
import se.trigger.device.devices.BreakBeam;
import se.trigger.device.devices.DeviceType;
import se.trigger.device.devices.Magnet;
import se.trigger.device.devices.Motion;
import se.trigger.device.devices.Temperature;
import se.trigger.device.mq.discovery.HeartbeatProducer;

/**
 * Created by john on 2017-04-21.
 */
@Configuration
@EnableRabbit
@EnableScheduling
public class DeviceConfig {

    public final static String EXCHANGE_NAME = "trigger.direct";
    public final static String ROUTING_KEY_HEARTBEAT = "device.heartbeat";
    public final static String ROUTING_KEY_DEVICE_MESSAGE = "device.messages";

    @Value("${mq.hostname}")
    private String mqHostname;

    @Value("${mq.username}")
    private String mqUsername;

    @Value("${mq.password}")
    private String mqPassword;

    @Value("${1wire.type}")
    private String oneWireDeviceType;

    @Bean
    DirectExchange exchange() {
        return new DirectExchange(EXCHANGE_NAME);
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
    public HeartbeatProducer heartbeatProducer() {
        return new HeartbeatProducer();
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


}
