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
 * Created by john on 2017-04-12.
 */
public class Magnet extends AbstractDevice implements OneWireFileChangeListener {

    @Value("${value_filename}")
    private String connectedFilename;

    @Value("${true_value}")
    private int connectedTrueValue;

    @Value("${file_inspect_interval}")
    private long fileInspectInterval;

    @Autowired
    private OneWireComponent oneWireComponent;

    @PostConstruct
    public void init() {
        System.out.println("Magnet.init");
        taskExecutor.execute(new OneWireFileChangeThread(oneWireComponent, connectedFilename, this, fileInspectInterval));
    }

    public boolean connected() throws IOException {
        System.out.println("Magnet.connected() connectedTrueValue: " + connectedTrueValue);
        int value = oneWireComponent.readInt(connectedFilename);
        System.out.println("Magnet.connected() value: " + value);
        return  value == connectedTrueValue;
    }

    @Override
    public String getId() {
        return getName() + " " + oneWireComponent.getAddress();
    }

    @Override
    public String getName() {
        return "Magnet trigger";
    }

    @Override
    public DeviceType getType() {
        return DeviceType.MAGNET;
    }

    @Override
    public String toString() {
        return getId();
    }

    @Override
    public void onChange(Path changed) {
        try {
            System.out.println("Magnet.onChange() connected: " + connected());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            template.convertAndSend(getId(), "connected: " + connected());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
