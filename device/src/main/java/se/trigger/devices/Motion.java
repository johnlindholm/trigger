package se.trigger.devices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import se.trigger.devices.event.OneWireFileChangeListener;
import se.trigger.devices.event.OneWireFileChangeThread;
import se.trigger.onewire.OneWireComponent;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by john on 2017-04-14.
 */
public class Motion extends AbstractDevice implements OneWireFileChangeListener {

    @Value("${value_filename}")
    private String triggeredFilename;

    @Value("${true_value}")
    private int triggeredTrueValue;

    @Value("${file_inspect_interval}")
    private long fileInspectInterval;

    @Autowired
    private OneWireComponent oneWireComponent;

    @PostConstruct
    public void init() {
        taskExecutor.execute(new OneWireFileChangeThread(oneWireComponent, triggeredFilename, this, fileInspectInterval));
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
