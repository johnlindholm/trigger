package se.trigger.device.onewire.filesystem;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by x61h on 26-04-2017.
 */
public class OWFSUtils {

    public static Map<Path, OWFSAbstractFile> parseOWFS(Path deviceFolder, String family) {
        return parseDirectoryStructure(deviceFolder, Paths.get("/mnt/1wire/structure/" + family), family);
    }

    private static Map<Path, OWFSAbstractFile> parseDirectoryStructure(Path deviceFolder, Path deviceStructureFolder, String family) {
        Map<Path, OWFSAbstractFile> pathToOWFSFileMap = new HashMap<>();
        DirectoryStream<Path> directoryStream;
        try {
            directoryStream = Files.newDirectoryStream(deviceStructureFolder);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        Iterator<Path> itr = directoryStream.iterator();
        while (itr.hasNext()) {
            try {
                Path structurePath = itr.next();
                if (Files.isDirectory(structurePath)) {
                    Map<Path, OWFSAbstractFile> subPathToOWFSFileMap = parseDirectoryStructure(deviceFolder, structurePath, family);
                    pathToOWFSFileMap.putAll(subPathToOWFSFileMap);
                } else {
                    OWFSAbstractFile owfsFile = parseFileStructure(structurePath, structurePathToDevicePath(deviceFolder, structurePath, family));
                    pathToOWFSFileMap.put(structurePathToDevicePath(deviceFolder, structurePath, family), owfsFile);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return pathToOWFSFileMap;
    }

    private static Path structurePathToDevicePath(Path deviceFolder, Path structureFilePath, String family) {
        String devicePathStr = structureFilePath.toString().replaceAll("/structure/" + family, "/" + deviceFolder.getFileName().toString());
        return FileSystems.getDefault().getPath(devicePathStr);
    }

    private static OWFSAbstractFile parseFileStructure(Path structurePath, Path deviceFilePath) throws IOException {
        //   D - directory (or subdirectory)
        //   i- integer, read as string
        //   u - unsigned integer, read as string
        //   f - floating point
        //   l - alias, read as string
        //   a - ascii, read as string
        //   b - binary
        //   y - yes/no, read as string
        //   d - date
        //   t - temperature
        //   g -temperature gap (delta)
        //   p - pressure

        String line = Files.readAllLines(structurePath).get(0);

        String[] ss = line.split(",");
        String type = ss[0];
        String index = ss[1];
        String elements = ss[2];
        String access = ss[3];
        String size = ss[4];
        String changeability = ss[5];

        OWFSAbstractFile owfsAbstractFile = null;

        if (type.equals("i")) {
            owfsAbstractFile = new OWFSIntegerFile();
        } else if (type.equals("u")) {
            owfsAbstractFile = new OWFSUnsignedIntegerFile();
        } else if (type.equals("f")) {
            owfsAbstractFile = new OWFSFloatingPointFile();
        } else if (type.equals("l")) {
            owfsAbstractFile = new OWFSAliasFile();
        } else if (type.equals("a")) {
            owfsAbstractFile = new OWFSAsciiFile();
        } else if (type.equals("b")) {
            owfsAbstractFile = new OWFSBinaryFile();
        } else if (type.equals("y")) {
            owfsAbstractFile = new OWFSBooleanFile();
        } else if (type.equals("d")) {
            owfsAbstractFile = new OWFSDateFile();
        } else if (type.equals("t")) {
            owfsAbstractFile = new OWFSTemperatureFile();
        } else if (type.equals("g")) {
            owfsAbstractFile = new OWFSTemperatureGapFile();
        } else if (type.equals("p")) {
            owfsAbstractFile = new OWFSPressureFile();
        }

        owfsAbstractFile.setPath(deviceFilePath);
        owfsAbstractFile.setType(type);
        owfsAbstractFile.setIndex(index);
        owfsAbstractFile.setElements(elements);
        owfsAbstractFile.setAccess(access);
        owfsAbstractFile.setSize(size);
        owfsAbstractFile.setChangeability(changeability);

        return owfsAbstractFile;
    }

}
