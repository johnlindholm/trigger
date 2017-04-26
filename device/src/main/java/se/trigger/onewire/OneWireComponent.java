package se.trigger.onewire;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by john on 2017-04-12.
 */
@Component
public class OneWireComponent {


    @Value("${address}")
    private String address;

    private Path deviceFolder;

    private Map<Path, Structure> structureMap;

    @PostConstruct
    public void init() {
        System.out.println("OneWireComponent.init() address: " + address);
        deviceFolder = Paths.get("/mnt/1wire/" + address);
        structureMap = parseFamilyStructure(getFamily(address));
    }

    private static String getFamily(String address) {
        return address.split(".")[0];
    }

    public int readInt(String filename) throws IOException {
        byte[] bytes = readAllBytes(filename);
        for (int i = 0; i < bytes.length; i++) {
            System.out.println("OneWireComponent.readInt() bytes["+i+"]: " + bytes[0]);
        }
        if (bytes.length < 4) {
            byte[] tmp = new byte[4];
            System.arraycopy(bytes, 0, tmp, tmp.length - bytes.length, bytes.length);
            bytes = tmp;
            for (int i = 0; i < bytes.length; i++) {
                System.out.println("OneWireComponent.readInt() bytes["+i+"]: " + bytes[0]);
            }
        } else if (bytes.length > 4) {
            byte[] tmp = new byte[4];
            System.arraycopy(bytes, bytes.length - 4, tmp,0, 4);
            bytes = tmp;
            for (int i = 0; i < bytes.length; i++) {
                System.out.println("OneWireComponent.readInt() bytes["+i+"]: " + bytes[0]);
            }
        }
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        return bb.getInt();
    }

    public byte[] readAllBytes(String filename) throws IOException {
        return Files.readAllBytes(deviceFolder.resolve(filename));
    }

    public String getAddress() {
        return address;
    }

    public Path getDeviceFolder() {
        return deviceFolder;
    }

    private static Map<Path, Structure> parseFamilyStructure(Path deviceFolder, String family) {
        return parseDirectoryStructure(deviceFolder, Paths.get("/mnt/1wire/structure/" + family));
    }

    private static Map<Path, Structure> parseDirectoryStructure(Path deviceFolder, Path deviceStructureFolder) {
        Map<Path, Structure> structureMap = new HashMap<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(deviceStructureFolder)) {
            for (Path path : directoryStream) {
                if (Files.isDirectory(path)) {
                    Map<Path, Structure> subStructureMap = parseDirectoryStructure(deviceFolder, path);
                    structureMap.putAll(subStructureMap);
                } else {
                    Structure structure = parseFileStructure(Files.readAllLines(path).get(0));
                    structureMap.put(structurePathToDevicePath(deviceFolder, path), structure);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return structureMap;
    }

    private static Path structurePathToDevicePath(Path deviceFolder, Path structureFilePath) {
        System.out.println("OneWireComponent.structurePathToDevicePath() structureFilePath: " + structureFilePath);
        System.out.println("OneWireComponent.structurePathToDevicePath() deviceFolder.getFileName().toString(): " + deviceFolder.getFileName().toString());
        String devicePathStr = structureFilePath.toString().replaceAll("/structure/", deviceFolder.getFileName().toString());
        return FileSystems.getDefault().getPath(devicePathStr);
    }

    private static Structure parseFileStructure(String s) {
        String[] ss = s.split(",");
        Structure structure = new Structure();
        structure.type = ss[0];
        structure.index = ss[1];
        structure.elements = ss[2];
        structure.access = ss[3];
        structure.size = ss[4];
        structure.changeability = ss[5];
        return structure;
    }

    private class Structure {
        Path path;
        String type;
        String index;
        String elements;
        String access;
        String size;
        String changeability;

        @Override
        public String toString() {
            return "path: " + path + "\n\ttype: " + type + ", index: " + index + ", elements: " + elements + ", access: " + access + ", size: " + size + ", changeability: " + changeability;
        }
    }

}
