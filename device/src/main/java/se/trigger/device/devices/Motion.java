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
 * Created by john on 2017-04-14.
 */
public class Motion extends AbstractDevice implements OneWireFileChangeListener {

    @Value("${1wire.value_filename}")
    private String oneWireTriggeredFilename;

    @Value("${1wire.true_value}")
    private String oneWireTriggeredTrueValue;

    @Value("${1wire.file_inspect_interval}")
    private long oneWireFileInspectInterval;

    @Autowired
    private OneWireComponent oneWireComponent;

    private OWFSAbstractFile owfsFile;

    @PostConstruct
    public void init() {
        owfsFile = oneWireComponent.getOWFSFile(oneWireTriggeredFilename);
        taskExecutor.execute(new OneWireFileChangeThread(owfsFile, this, oneWireFileInspectInterval));
    }

    public boolean isTriggered() throws IOException, OWFSException {
        String valueStr = owfsFile.readString();
        return valueStr.equals(oneWireTriggeredTrueValue);
    }

    @Override
    public String getId() {
        return oneWireComponent.getAddress().replaceAll("[.]", "_");
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
    public void onChange(OWFSAbstractFile owfsFile) {
        try {
            template.convertAndSend(getId(), "isTriggered: " + isTriggered());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OWFSException e) {
            e.printStackTrace();
        }
    }
}
