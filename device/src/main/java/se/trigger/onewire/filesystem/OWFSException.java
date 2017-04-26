package se.trigger.onewire.filesystem;

/**
 * Created by x61h on 26-04-2017.
 */
public class OWFSException extends Exception {

    public OWFSException(String msg) {
        super(msg);
    }

    public OWFSException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
