package se.trigger.device.onewire.filesystem;

/**
 * Created by john on 2017-04-26.
 */
public class OWFSAsciiFile extends OWFSAbstractFile {

    public String readAscii() throws OWFSException {
        return readString();
    }
}
