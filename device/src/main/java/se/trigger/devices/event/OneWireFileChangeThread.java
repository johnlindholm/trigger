package se.trigger.devices.event;

import se.trigger.onewire.OneWireComponent;

import java.io.IOException;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

/**
 * Created by john on 2017-04-24.
 */
public class OneWireFileChangeThread implements Runnable {

    private final OneWireComponent oneWireComponent;
    private final String filename;
    private final OneWireFileChangeListener listener;
    private final Path folder;
    private String hash = null;
    private long sleepInMillis = -1;

    public OneWireFileChangeThread(OneWireComponent oneWireComponent, String filename, OneWireFileChangeListener listener, long sleepInMillis) {
        this.folder = oneWireComponent.getDeviceFolder();
        this.filename = filename;
        this.listener = listener;
        this.oneWireComponent = oneWireComponent;
        this.sleepInMillis = sleepInMillis;
    }

//    public Runnable runnable() {
//        System.out.println("OneWireFileChangeThread.runnable");
//        return () -> {
//            System.out.println("OneWireFileChangeThread.runnable() folder: " + folder);
//            System.out.println("OneWireFileChangeThread.runnable() filename: " + filename);
//            try (final WatchService watchService = FileSystems.getDefault().newWatchService()) {
//                folder.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
//                while (true) {
//                    final WatchKey wk = watchService.take();
//                    for (WatchEvent<?> event : wk.pollEvents()) {
//                        //we only register "ENTRY_MODIFY" so the context is always a Path.
//                        final Path changed = (Path) event.context();
//                        System.out.println("OneWireFileChangeThread.runnable() changed: " + changed);
//                        if (changed.endsWith(filename)) {
//                            listener.onChange(changed);
//                            System.out.println("My file has changed");
//                        }
//                    }
//                    // reset the key
//                    boolean valid = wk.reset();
//                    if (!valid) {
//                        System.out.println("Key has been unregisterede");
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        };
//    }

    @Override
    public void run() {
        while (true) {
            try {
                String newHash = hash(oneWireComponent.readAllBytes(filename));
                if (hash != null && !hash.equals(newHash)) {
                    //Hash changed, notify listener
                    System.out.println("OneWireFileChangeThread.run() file changed");
                    Path changed = oneWireComponent.getDeviceFolder().resolve(filename);
                    listener.onChange(changed);
                }
                hash = newHash;
                Thread.sleep(sleepInMillis);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static String hash(byte[] bytes) throws NoSuchAlgorithmException{
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
