package cc.fyre.proton.util;

import cc.fyre.proton.Proton;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public final class PingUtils {

    // Static utility class -- cannot be created.
    private PingUtils() {
    }

    /**
     * Pings a remote Minecraft server.
     *
     * @param host     The host of the server to ping. Can be either a hostname (ex MineHQ.com) or an IP (ex 127.0.0.1)
     * @param port     The port of the server to ping. The default Minecraft port is 25565.
     * @param callback The callback to call with the results of pinging.
     */
    public static void ping(String host, int port, Callback callback) {
        (new PingTask(host, port, callback)).run();
    }

    public static class PingResponse {

        @Getter private Version version;
        @Getter private Players players;
        @Getter private String description;
        @Getter private String favicon;

        public static class Players {

            @Getter private int max;
            @Getter private int online;

        }

        public static class Version {

            @Getter private String name;
            @Getter private int protocol;

        }
    }

    @AllArgsConstructor
    private static class PingTask implements Runnable {

        private String host;
        private int port;
        private Callback callback;

        @Override
        public void run() {
            try (Socket socket = new Socket()) {
                SocketAddress address = new InetSocketAddress(host, port);

                socket.connect(address, 5000);    // 5 second timeout on connect and read
                socket.setSoTimeout(5000);

                DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                // construct a handshake packet
                ByteArrayOutputStream handshake = new ByteArrayOutputStream();
                DataOutputStream handshakeOut = new DataOutputStream(handshake);

                NetworkUtils.writeVarInt(handshakeOut, 0x00);    // handshake ID = 0x00
                NetworkUtils.writeVarInt(handshakeOut, 4);       // 1.7.2 - 1.7.5 = 4
                NetworkUtils.writeString(handshakeOut, host);
                handshakeOut.writeShort(port);
                NetworkUtils.writeVarInt(handshakeOut, 1);       // status protocol = 1
                NetworkUtils.writePacket(out, handshake.toByteArray());

                ByteArrayOutputStream status = new ByteArrayOutputStream();
                DataOutputStream statusOut = new DataOutputStream(status);

                NetworkUtils.writeVarInt(statusOut, 0x00);       // request status = 0
                NetworkUtils.writePacket(out, status.toByteArray());

                DataInputStream in = new DataInputStream(socket.getInputStream());
                byte[] response = NetworkUtils.readPacket(in);
                DataInputStream responseIn = new DataInputStream(new ByteArrayInputStream(response));
                int id = NetworkUtils.readVarInt(responseIn);

                if (id != 0x00) {
                    throw new Exception("Unexpected packet ID");
                }

                String jsonResponse = NetworkUtils.readString(responseIn);

                callback.success(Proton.GSON.fromJson(jsonResponse, PingResponse.class));
            } catch (Exception e) {
                callback.failure(e);
            }
        }

    }

    public interface Callback {

        void success(PingResponse response);

        void failure(Exception e);

    }

}