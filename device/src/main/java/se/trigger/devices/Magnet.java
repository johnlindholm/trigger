package se.trigger.devices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import se.trigger.devices.event.OneWireFileChangeListener;
import se.trigger.devices.event.OneWireFileChangeThread;
import se.trigger.onewire.OneWireComponent;
import se.trigger.onewire.filesystem.OWFSAbstractFile;
import se.trigger.onewire.filesystem.OWFSException;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * Created by john on 2017-04-12.
 */
public class Magnet extends AbstractDevice implements OneWireFileChangeListener {

    @Value("${value_filename}")
    private String connectedFilename;

    @Value("${true_value}")
    private String connectedTrueValue;

    @Value("${file_inspect_interval}")
    private long fileInspectInterval;

    @Autowired
    private OneWireComponent oneWireComponent;

    private OWFSAbstractFile owfsFile;

    @PostConstruct
    public void init() {
        owfsFile = oneWireComponent.getOWFSFile(connectedFilename);
        taskExecutor.execute(new OneWireFileChangeThread(owfsFile, this, fileInspectInterval));
    }

    public boolean connected() throws IOException, OWFSException {
        System.out.println("Magnet.connected() connectedTrueValue: " + connectedTrueValue);
        String valueStr = owfsFile.readString();
        System.out.println("Magnet.connected() value: " + valueStr);
        return valueStr.trim().equals(connectedTrueValue);
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
    public void onChange(OWFSAbstractFile owfsFile) {
        try {
            System.out.println("Magnet.onChange() connected: " + connected());
            template.convertAndSend(getId(), "connected: " + connected());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OWFSException e) {
            e.printStackTrace();
        }
    }
}
