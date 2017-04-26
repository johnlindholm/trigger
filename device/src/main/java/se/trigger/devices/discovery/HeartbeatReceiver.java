package se.trigger.devices.discovery;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Created by john on 2017-04-26.
 */
@Component
public class HeartbeatReceiver {

    @RabbitListener(queues = "#{heartbeatQueue.name}")
    public void receiveHeartbeat(String data) {
        System.out.println("HeartbeatReceiver.receiveHeartbeat() data: " + data);
    }

    @RabbitListener(queues = "#{deviceMessageQueue.name}")
    public void receiveDeviceMessage(String data) {
        System.out.println("HeartbeatReceiver.receiveDeviceMessage() data: " + data);
    }

}
