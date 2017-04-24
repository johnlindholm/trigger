package se.trigger.devices.discovery;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

/**
 * Created by john on 2017-04-24.
 */
public class FileChangeThread implements Runnable {

    private final Path folder;
    private final String filename;
    private final FileChangeListener listener;

    public FileChangeThread(Path folder, String filename, FileChangeListener listener) {
        this.folder = folder;
        this.filename = filename;
        this.listener = listener;
    }

    @Override
    public void run() {
        System.out.println("FileChangeThread.run() folder: " + folder);
        System.out.println("FileChangeThread.run() filename: " + filename);
        try (final WatchService watchService = FileSystems.getDefault().newWatchService()) {
            folder.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
            while (true) {
                final WatchKey wk = watchService.take();
                for (WatchEvent<?> event : wk.pollEvents()) {
                    //we only register "ENTRY_MODIFY" so the context is always a Path.
                    final Path changed = (Path) event.context();
                    if (changed.endsWith(filename)) {
                        listener.onChange(changed);
                        System.out.println("My file has changed");
                    }
                }
                // reset the key
                boolean valid = wk.reset();
                if (!valid) {
                    System.out.println("Key has been unregisterede");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
