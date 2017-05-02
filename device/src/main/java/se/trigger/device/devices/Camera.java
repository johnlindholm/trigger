package se.trigger.device.devices;

import org.springframework.beans.factory.annotation.Value;

/**
 * Created by john on 2017-04-29.
 */
public class Camera extends AbstractDevice {

    @Value("${camera.id}")
    private String id;

    @Value("${camera.led}")
    private boolean showLed;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return "Camera " + getId();
    }

    @Override
    public DeviceType getType() {
        return DeviceType.CAMERA;
    }
}
