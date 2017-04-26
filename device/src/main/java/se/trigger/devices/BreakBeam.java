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
 * Created by john on 2017-04-14.
 */
public class BreakBeam extends AbstractDevice implements OneWireFileChangeListener {

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
        return valueStr.equals(oneWireConnectedTrueValue);
    }

    @Override
    public String getId() {
        return oneWireComponent.getAddress().replaceAll("[.]", "_");
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
    public void onChange(OWFSAbstractFile owfsFile) {
        try {
            template.convertAndSend(getId(), "connected: " + connected());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OWFSException e) {
            e.printStackTrace();
        }
    }
}
