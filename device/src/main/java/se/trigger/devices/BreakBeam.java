package se.trigger.devices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import se.trigger.devices.discovery.FileChangeListener;
import se.trigger.devices.discovery.FileChangeThread;
import se.trigger.onewire.OneWireComponent;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by john on 2017-04-14.
 */
@Component
public class BreakBeam extends AbstractDevice implements FileChangeListener {

    @Value("${value_filename}")
    private String connectedFilename;

    @Value("${true_value}")
    private int connectedTrueValue;

    @Autowired
    private OneWireComponent oneWireComponent;

    @PostConstruct
    public void init() {
        taskExecutor.execute(new FileChangeThread(oneWireComponent.getDeviceFolder(), connectedFilename, this));
    }

    public boolean connected() throws IOException {
        return oneWireComponent.readInt(connectedFilename) == connectedTrueValue;
    }

    @Override
    public String getId() {
        return getName() + " " + oneWireComponent.getAddress();
    }

    @Override
    public String getName() {
        return "Break Beam";
    }

    @Override
    public DeviceType getType() {
        return DeviceType.BREAK_BEAM;
    }

    @Override
    public String toString() {
        return getId();
    }

    @Override
    public void onChange(Path changed) {
        try {
            template.convertAndSend(getId(), "connected: " + connected());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
