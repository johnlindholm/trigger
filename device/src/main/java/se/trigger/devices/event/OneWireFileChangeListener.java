package se.trigger.devices.event;

import java.nio.file.Path;

/**
 * Created by john on 2017-04-24.
 */
public interface OneWireFileChangeListener {

    void onChange(Path changed);
}
