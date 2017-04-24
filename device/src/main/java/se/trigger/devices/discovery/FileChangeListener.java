package se.trigger.devices.discovery;

import java.nio.file.Path;

/**
 * Created by john on 2017-04-24.
 */
public interface FileChangeListener {

    void onChange(Path changed);
}
