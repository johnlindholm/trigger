package se.trigger.onewire;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by john on 2017-04-12.
 */
@Component
public class OneWireComponent {
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

    @Value("${address}")
    private String address;

    private final Path deviceFolder;

    public OneWireComponent() {
        this.deviceFolder = Paths.get("/mnt/1wire/" + address);
    }

    public int readInt(String filename) throws IOException {
        byte[] bytes = Files.readAllBytes(deviceFolder.resolve(filename));
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        return bb.getInt();
    }

    public String getAddress() {
        return address;
    }

    public Path getDeviceFolder() {
        return deviceFolder;
    }

//    public static OneWireComponent deviceFromConfigType(String typeStr) {
//        DeviceType type = DeviceType.valueOf(typeStr.toUpperCase());
//        switch (type) {
//            case MAGNET:
//                return new Magnet();
//        }
//    }
}
