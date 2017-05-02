package se.trigger.device.onewire.filesystem;

/**
 * Created by john on 2017-04-26.
 */
public class OWFSBooleanFile extends OWFSAbstractFile {

    public boolean readBoolean() throws OWFSException {
        String value = readString();
        return value != null && value.trim() == "1";
    }
}
