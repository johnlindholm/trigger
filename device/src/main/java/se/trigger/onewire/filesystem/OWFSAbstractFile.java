package se.trigger.onewire.filesystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

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

    private Path path;
    private String type;
    private String index;
    private String elements;
    private String access;
    private String size;
    private String changeability;

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getElements() {
        return elements;
    }

    public void setElements(String elements) {
        this.elements = elements;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getChangeability() {
        return changeability;
    }

    public void setChangeability(String changeability) {
        this.changeability = changeability;
    }

    @Override
    public String toString() {
        return "path: " + path + "\n\ttype: " + type + ", index: " + index + ", elements: " + elements + ", access: " + access + ", size: " + size + ", changeability: " + changeability;
    }

    public String readString() throws OWFSException {
        try {
            List<String> lines = Files.readAllLines(getPath());
            if (lines != null && lines.size() == 1) {
                return lines.get(0);
            }
            throw new OWFSException("Incorrect format lines: " + Arrays.toString(lines.toArray()));
        } catch (IOException e) {
            throw new OWFSException("Unable to read from file: " + e.getMessage(), e);
        }
    }

    public byte[] readBytes() throws OWFSException {
        try {
            return Files.readAllBytes(getPath());
        } catch (IOException e) {
            throw new OWFSException("Unable to read from file: " + e.getMessage(), e);
        }
    }
}
