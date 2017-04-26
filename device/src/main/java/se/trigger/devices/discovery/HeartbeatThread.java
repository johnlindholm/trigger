package se.trigger.devices.discovery;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import se.trigger.config.AppConfig;
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

    @Value("${mq.heartbeat_interval_millis}")
    private long mqHeartbeatIntervalMillis;

    @Override
    public void run() {
        System.out.println("HeartbeatThread.start()");
        while (true) {
            try {
                System.out.println("HeartbeatThread.run() sending heartbeat: " + device.getId());
                template.convertAndSend(AppConfig.EXCHANGE_NAME, AppConfig.ROUTING_KEY_HEARTBEAT, device.getId());
                Thread.sleep(mqHeartbeatIntervalMillis);
            } catch (AmqpException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
