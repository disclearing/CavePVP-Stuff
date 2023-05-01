package cc.fyre.proton.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

// Borrowed from kohi.
public class NetworkUtils {

    // Static utility class -- cannot be created.
    private NetworkUtils() {
    }

    public static void writeVarInt(DataOutputStream out, int value) throws IOException {
        for (int i = 0; i < 3; i++) {
            if ((value & 0xFFFFFF80) == 0) {
                out.write(value);

                return;
            }

            out.write(value & 0x7F | 0x80);
            value >>>= 7;
        }
    }

    public static void writeString(DataOutputStream out, String str) throws IOException {

        final byte[] bytes = str.getBytes("UTF-8");

        writeVarInt(out, bytes.length);
        out.write(bytes);
    }

    public static void writePacket(DataOutputStream out, byte[] bytes) throws IOException {
        writeVarInt(out, bytes.length);
        out.write(bytes);
    }

    public static int readVarInt(DataInputStream in) throws IOException {
        int value = 0;

        for (int i = 0; i < 3; i++) {
            int b = in.read();

            value |= (b & 0x7F) << i * 7;

            if ((b & 0x80) == 0) {
                break;
            }
        }

        return (value);
    }

    public static String readString(DataInputStream in) throws IOException {
        int len = readVarInt(in);
        byte[] bytes = new byte[len];

        in.readFully(bytes);

        return (new String(bytes, "UTF-8"));
    }

    public static byte[] readPacket(DataInputStream in) throws IOException {
        int len = readVarInt(in);
        byte[] bytes = new byte[len];

        in.readFully(bytes);

        return (bytes);
    }

}