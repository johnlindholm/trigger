package se.trigger.device.devices;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import se.trigger.device.config.DeviceConfig;

/**
 * Created by john on 2017-04-24.
 */
@Component
public abstract class AbstractDevice {

    @Autowired
    protected TaskExecutor taskExecutor;

    @Autowired
    protected AmqpTemplate template;

    public abstract String getId();

    public abstract String getName();

    public abstract DeviceType getType();

    protected void sendMessage(Object message) {
        System.out.println("AbstractDevice.sendMessage() message: " + message);
        template.convertAndSend(DeviceConfig.EXCHANGE_NAME, DeviceConfig.ROUTING_KEY_DEVICE_MESSAGE, message);
    }

}
