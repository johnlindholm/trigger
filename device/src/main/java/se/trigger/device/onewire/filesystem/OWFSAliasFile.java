package se.trigger.device.onewire.filesystem;

/**
 * Created by john on 2017-04-26.
 */
public class OWFSAliasFile extends OWFSAbstractFile {

    public String readAlias() throws OWFSException {
        return readString();
    }
}
