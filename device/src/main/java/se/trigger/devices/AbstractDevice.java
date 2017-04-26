package se.trigger.devices;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import se.trigger.config.AppConfig;

/**
 * Created by john on 2017-04-24.
 */
@Component
public abstract class AbstractDevice {

    @Autowired
    protected TaskExecutor taskExecutor;

    @Autowired
    protected AmqpTemplate template;

    /**
     * Must not contain any whitespace
     *
     * @return
     */
    public abstract String getId();

    public abstract String getName();

    public abstract DeviceType getType();

    protected String getRoutingKey() {
        return AppConfig.ROUTING_KEY_DEVICE_MESSAGE_PREFIX + getId();
    }

    protected void sendMessage(Object message) {
        System.out.println("AbstractDevice.sendMessage() routingKey: " + getRoutingKey() + ", message: " + message);
        template.convertAndSend(AppConfig.EXCHANGE_NAME, getRoutingKey(), message);
    }

}
