package se.trigger.onewire.filesystem;

import java.nio.file.Path;

/**
 * Created by john on 2017-04-26.
 */
public abstract class OWFSAbstractFile {

    //   t,000000,000001,ro,000012,v,

    //   Type
    //   Where the first field ("t") is the type:
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
