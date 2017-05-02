package se.trigger.device.onewire.filesystem;

/**
 * Created by john on 2017-04-26.
 */
public class OWFSUnsignedIntegerFile extends OWFSAbstractFile {

    public int readInteger() throws OWFSException {
        String value = readString();
        return Integer.parseInt(value);
    }
}
