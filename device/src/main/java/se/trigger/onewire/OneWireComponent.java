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


    @Value("${address}")
    private String address;

    private Path deviceFolder;

    private Map<Path, OWFSAbstractFile> pathToOWFSFileMap;

    @PostConstruct
    public void init() {
        System.out.println("OneWireComponent.init() address: " + address);
        deviceFolder = Paths.get("/mnt/1wire/" + address);
        pathToOWFSFileMap = OWFSUtils.parseOWFS(deviceFolder, getFamily(address));
    }

    public OWFSAbstractFile getOWFSFile(Path path) {
        return pathToOWFSFileMap.get(path);
    }

    public OWFSAbstractFile getOWFSFile(String filename) {
        return pathToOWFSFileMap.get(deviceFolder.resolve(filename));
    }

    private static String getFamily(String address) {
        return address.split(".")[0];
    }

//    public int readInt(String filename) throws IOException {
//        byte[] bytes = readAllBytes(filename);
//        for (int i = 0; i < bytes.length; i++) {
//            System.out.println("OneWireComponent.readInt() bytes["+i+"]: " + bytes[0]);
//        }
//        if (bytes.length < 4) {
//            byte[] tmp = new byte[4];
//            System.arraycopy(bytes, 0, tmp, tmp.length - bytes.length, bytes.length);
//            bytes = tmp;
//            for (int i = 0; i < bytes.length; i++) {
//                System.out.println("OneWireComponent.readInt() bytes["+i+"]: " + bytes[0]);
//            }
//        } else if (bytes.length > 4) {
//            byte[] tmp = new byte[4];
//            System.arraycopy(bytes, bytes.length - 4, tmp,0, 4);
//            bytes = tmp;
//            for (int i = 0; i < bytes.length; i++) {
//                System.out.println("OneWireComponent.readInt() bytes["+i+"]: " + bytes[0]);
//            }
//        }
//        ByteBuffer bb = ByteBuffer.wrap(bytes);
//        return bb.getInt();
//    }
//
//    public byte[] readAllBytes(String filename) throws IOException {
//        return Files.readAllBytes(deviceFolder.resolve(filename));
//    }

    public String getAddress() {
        return address;
    }

    public Path getDeviceFolder() {
        return deviceFolder;
    }

}
