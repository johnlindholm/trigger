package se.trigger.onewire;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by john on 2017-04-13.
 */
public class OneWireUtil {

    //   t,000000,000001,ro,000012,v,

//   Type
//   Where the first field ("t") is the type:
//   D - directory (or subdirectory)
//   i- integer
//   u - unsigned integer
//   f - floating point
//   l - alias
//   a - ascii
//   b - binary
//   y - yes/no
//   d - date
//   t - temperature
//   g -temperature gap (delta)
//   p - pressure

//   Note that i,u,y can all be treated as integer,
//   and temperature and temperature gap are floating point with scaling applied
//   and pressure is not yet implemented
//   and binary and ascii (and link) are similar except in the handling of
//   input data
//   All variables are passed around as text strings externally and within
//   owserver protocol.
//
//   Index
//   The second field is the index (if an array variable).
//           0 if not an array
//  -1 if it's the .ALL (comma separated list)
//           -2 if it's the .BYTE (yes/no array as a bitfield
//
//   Elements
//   The third field is the size of the array (in elements)
//   1 for scalar variables
//   0 sparse numbered
//  -1 sparse text
//
//   Access
//   The fourth field is the read/write mode
//   rw - read and write
//   wo - write only
//   ro - read only
//   oo - no access (really just for completeness)
//
//   Size
//   The fifth field is the size in characters (byte) of the data value.
//   Usually 12 chars for numbers
//   Actual bytes/chars for ascii and binary
//
//   Added in 2.8p9:
//   Changebility
//   sixth field single letter:
//   v - volatile (can change on it's own like voltage or temperature)
//   s - stable (changed only with input like a memory location)
//   f - fixed (like type name)
//   t - time (changes every second)

    public static void main(String... args) {
        System.out.println("args: " + args);
        new OneWireUtil().monitor(args);
//        try {
//            new OneWireUtil().parseDeviceFolder(Paths.get(args[0]));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public void monitor(String... args) {
        List<Monitor> monitors = new ArrayList<>();
        for (String pathStr : args) {
            Monitor m = new Monitor(Paths.get(pathStr));
            m.start();
            monitors.add(m);
        }
    }

    private class Monitor extends Thread {

        private final Path file;

        public Monitor(Path file) {
            this.file = file;
        }

        public void run() {
            while (true) {
                try {
                    byte[] bytes = Files.readAllBytes(file);
                    System.out.println(file.getFileName() + ": " + new String(bytes));
                    sleep(1000);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void parseDeviceFolder(Path folder) throws Exception {
        DirectoryStream<Path> stream = Files.newDirectoryStream(folder);
        Iterator<Path> itr = stream.iterator();
        while (itr.hasNext()) {
            try {
                Path path = itr.next();
                if (Files.isDirectory(path)) {
                    parseDeviceFolder(path);
                } else {
                    // t,000000,000001,ro,000012,v,
                    List<String> lines = Files.readAllLines(path);
                    if (lines.size() > 1) {
                        throw new Exception("Too many lines: " + lines);
                    }
                    String line = lines.get(0);
                    Structure structure = parseStructure(line);
                    structure.path = path;
                    System.out.println(structure);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        for (Path path : stream) {
//            if (Files.isDirectory(path)) {
//                parseDeviceFolder(path);
//            } else {
//                // t,000000,000001,ro,000012,v,
//                List<String> lines = Files.readAllLines(path);
//                if (lines.size() > 1) {
//                    throw new Exception("Too many lines: " + lines);
//                }
//                String line = lines.get(0);
//                Structure structure = parseStructure(line);
//                structure.path = path;
//                System.out.println(structure);
//            }
//        }
    }

    private Structure parseStructure(String s) {
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
