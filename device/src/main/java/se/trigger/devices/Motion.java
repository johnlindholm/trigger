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
public class Motion extends AbstractDevice implements OneWireFileChangeListener {

    @Value("${value_filename}")
    private String triggeredFilename;

    @Value("${true_value}")
    private String triggeredTrueValue;

    @Value("${file_inspect_interval}")
    private long fileInspectInterval;

    @Autowired
    private OneWireComponent oneWireComponent;

    private OWFSAbstractFile owfsFile;

    @PostConstruct
    public void init() {
        owfsFile = oneWireComponent.getOWFSFile(triggeredFilename);
        taskExecutor.execute(new OneWireFileChangeThread(owfsFile, this, fileInspectInterval));
    }

    public boolean isTriggered() throws IOException, OWFSException {
        String valueStr = owfsFile.readString();
        return valueStr.equals(triggeredTrueValue);
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
