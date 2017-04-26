package se.trigger.devices.event;

import se.trigger.onewire.filesystem.OWFSAbstractFile;
import se.trigger.onewire.filesystem.OWFSException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

/**
 * Created by john on 2017-04-24.
 */
public class OneWireFileChangeThread implements Runnable {

    private final OWFSAbstractFile owfsFile;
    private final OneWireFileChangeListener listener;
    private String hash = null;
    private long sleepInMillis = -1;

    public OneWireFileChangeThread(OWFSAbstractFile owfsFile, OneWireFileChangeListener listener, long sleepInMillis) {
        this.owfsFile = owfsFile;
        this.listener = listener;
        this.sleepInMillis = sleepInMillis;
    }

    @Override
    public void run() {
        while (true) {
            try {
                byte[] data = owfsFile.readBytes();
                String newHash = hash(data);
                if (hash != null && !hash.equals(newHash)) {
                    //Hash changed, notify listener
                    listener.onChange(owfsFile);
                }
                hash = newHash;
                Thread.sleep(sleepInMillis);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (OWFSException e) {
                e.printStackTrace();
            }
        }
    }

    private static String hash(byte[] bytes) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        return byteArray2Hex(md.digest(bytes));
    }

    private static String byteArray2Hex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }


}
