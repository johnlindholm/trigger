package se.trigger.devices;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import se.trigger.devices.discovery.FileChangeListener;
import se.trigger.devices.discovery.FileChangeThread;
import se.trigger.onewire.OneWireComponent;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by john on 2017-04-14.
 */
public class Motion extends AbstractDevice implements FileChangeListener {

    @Value("${value_filename}")
    private String triggeredFilename;

    @Value("${true_value}")
    private int triggeredTrueValue;

    @Autowired
    private OneWireComponent oneWireComponent;

    @PostConstruct
    public void init() {
        taskExecutor.execute(new FileChangeThread(oneWireComponent.getDeviceFolder(), triggeredFilename, this));
    }

    public boolean isTriggered() throws IOException {
        return oneWireComponent.readInt(triggeredFilename) == triggeredTrueValue;
    }

    @Override
    public String getId() {
        return getName() + " " + oneWireComponent.getAddress();
    }

    @Override
    public String getName() {
        return "Motion trigger";
    }

    @Override
    public DeviceType getType() {
        return DeviceType.MOTION;
    }

    @Override
    public String toString() {
        return getId();
    }

    @Override
    public void onChange(Path changed) {
        try {
            template.convertAndSend(getId(), "isTriggered: " + isTriggered());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
