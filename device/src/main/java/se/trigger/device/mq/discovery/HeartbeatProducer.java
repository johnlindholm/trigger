package se.trigger.device.mq.discovery;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import se.trigger.device.config.DeviceConfig;
import se.trigger.device.devices.AbstractDevice;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by john on 2017-04-28.
 */
@Component
public class HeartbeatProducer {

    @Autowired
    private AbstractDevice device;

    @Autowired
    private RabbitTemplate template;

    private final AtomicInteger counter = new AtomicInteger();

    @Scheduled(fixedDelay = 5000)
    public void sendMessage() {
        System.out.println("HeartbeatProducer.sendMessage()");
        template.convertAndSend(DeviceConfig.EXCHANGE_NAME, DeviceConfig.ROUTING_KEY_HEARTBEAT, device.getId() + " " + counter.incrementAndGet());
    }
}
