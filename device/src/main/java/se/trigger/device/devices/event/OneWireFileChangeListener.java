package se.trigger.device.devices.event;

import se.trigger.device.onewire.filesystem.OWFSAbstractFile;

/**
 * Created by john on 2017-04-24.
 */
public interface OneWireFileChangeListener {

    void onChange(OWFSAbstractFile changed);
}
