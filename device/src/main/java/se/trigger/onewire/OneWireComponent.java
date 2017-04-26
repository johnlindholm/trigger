package se.trigger.onewire;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import se.trigger.onewire.filesystem.OWFSAbstractFile;
import se.trigger.onewire.filesystem.OWFSUtils;

import javax.annotation.PostConstruct;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Created by john on 2017-04-12.
 */
@Component
public class OneWireComponent {


    @Value("${1wire.address}")
    private String oneWireAddress;

    private Path deviceFolder;

    private Map<Path, OWFSAbstractFile> pathToOWFSFileMap;

    @PostConstruct
    public void init() {
        System.out.println("OneWireComponent.init() oneWireAddress: " + oneWireAddress);
        deviceFolder = Paths.get("/mnt/1wire/" + oneWireAddress);
        pathToOWFSFileMap = OWFSUtils.parseOWFS(deviceFolder, getFamily(oneWireAddress));
    }

    public OWFSAbstractFile getOWFSFile(Path path) {
        return pathToOWFSFileMap.get(path);
    }

    public OWFSAbstractFile getOWFSFile(String filename) {
        return pathToOWFSFileMap.get(deviceFolder.resolve(filename));
    }

    private static String getFamily(String address) {
        return address.split("[.]")[0];
    }

    public String getAddress() {
        return oneWireAddress;
    }

    public Path getDeviceFolder() {
        return deviceFolder;
    }

}
