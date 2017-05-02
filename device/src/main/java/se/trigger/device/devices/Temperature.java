package se.trigger.device.devices;

import org.springframework.beans.factory.annotation.Autowired;
import se.trigger.device.onewire.OneWireComponent;

/**
 * Created by john on 2017-04-14.
 */
public class Temperature extends AbstractDevice {

    @Autowired
    private OneWireComponent oneWireComponent;

    public double getTemperature() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public String getId() {
        return oneWireComponent.getAddress().replaceAll("[.]", "_");
    }

    @Override
    public String getName() {
        return "Temperature";
    }

    @Override
    public DeviceType getType() {
        return DeviceType.TEMPERATURE;
    }

    @Override
    public String toString() {
        return getId();
    }
}
