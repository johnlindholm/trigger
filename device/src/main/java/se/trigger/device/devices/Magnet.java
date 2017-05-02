package se.trigger.device.devices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import se.trigger.device.devices.event.OneWireFileChangeListener;
import se.trigger.device.devices.event.OneWireFileChangeThread;
import se.trigger.device.onewire.OneWireComponent;
import se.trigger.device.onewire.filesystem.OWFSAbstractFile;
import se.trigger.device.onewire.filesystem.OWFSException;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * Created by john on 2017-04-12.
 */
public class Magnet extends AbstractDevice implements OneWireFileChangeListener {

    @Value("${1wire.value_filename}")
    private String oneWireConnectedFilename;

    @Value("${1wire.true_value}")
    private String oneWireConnectedTrueValue;

    @Value("${1wire.file_inspect_interval}")
    private long oneWireFileInspectInterval;

    @Autowired
    private OneWireComponent oneWireComponent;

    private OWFSAbstractFile owfsFile;

    @PostConstruct
    public void init() {
        owfsFile = oneWireComponent.getOWFSFile(oneWireConnectedFilename);
        taskExecutor.execute(new OneWireFileChangeThread(owfsFile, this, oneWireFileInspectInterval));
    }

    public boolean connected() throws IOException, OWFSException {
        String valueStr = owfsFile.readString();
        return valueStr.trim().equals(oneWireConnectedTrueValue);
    }

    @Override
    public String getId() {
        return oneWireComponent.getAddress().replaceAll("[.]", "_");
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
            boolean connected = connected();
            System.out.println("Magnet.onChange() connected: " + connected);
            sendMessage("connected: " + connected);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OWFSException e) {
            e.printStackTrace();
        }
    }
}
