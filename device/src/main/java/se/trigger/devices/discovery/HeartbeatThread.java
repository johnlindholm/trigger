package se.trigger.devices.discovery;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import se.trigger.devices.AbstractDevice;

/**
 * Created by john on 2017-04-24.
 */
@Component
public class HeartbeatThread extends Thread {

    @Autowired
    private AbstractDevice device;

    @Autowired
    private AmqpTemplate template;

    @Value("${heartbeat_interval_millis}")
    private long heartbeatIntervalMillis;

    @Override
    public void run() {
        System.out.println("HeartbeatThread.start()");
        while (true) {
            try {
                template.convertAndSend("heartbeat", device.getId());
                System.out.println("Sending heartbeat device: " + device);
                Thread.sleep(heartbeatIntervalMillis);
            } catch (AmqpException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
