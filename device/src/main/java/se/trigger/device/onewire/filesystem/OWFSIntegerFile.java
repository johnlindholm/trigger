package se.trigger.device.onewire.filesystem;

/**
 * Created by john on 2017-04-26.
 */
public class OWFSIntegerFile extends OWFSAbstractFile {

    public int readInteger() throws OWFSException {
        String value = readString();
        return Integer.parseInt(value);
    }
}
