package se.trigger.mq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import se.trigger.devices.AbstractDevice;
import se.trigger.devices.discovery.HeartbeatThread;

@Component
public class Runner implements CommandLineRunner {

    @Autowired
    private AbstractDevice device;

    @Autowired
    private HeartbeatThread heartbeatThread;

    @Autowired
    private TaskExecutor taskExecutor;

    @Override
    public void run(String... args) throws Exception {
        taskExecutor.execute(heartbeatThread);
    }

}