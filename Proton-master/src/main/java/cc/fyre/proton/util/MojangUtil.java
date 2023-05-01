package cc.fyre.proton.util;

import cc.fyre.proton.Proton;
import cc.fyre.proton.pidgin.PidginHandler;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;

public class MojangUtil {

    private static OkHttpClient client = new OkHttpClient();

    public static UUID getFromMojang(String name) throws IOException {

        final URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
        final URLConnection conn = url.openConnection();

        conn.setDoOutput(true);

        final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        final String line = reader.readLine();

        if (line == null) {
            return null;
        }

        final String[] id = line.split(",");

        String part = id[1];
        part = part.substring(6,38);

        return UUID.fromString(part.replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
    }

    public static Pair<String, String> getSkin(UUID uuid) throws IOException {

        final Request request = new Request.Builder().url("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString() + "?unsigned=false").build();
        final Response execute = MojangUtil.client.newCall(request).execute();

        if (execute == null) {
            return null;
        }

        final JsonObject json = PidginHandler.PARSER.parse(execute.body().string()).getAsJsonObject();

        //System.out.println(json.toString());

        if (!json.has("properties")) {
            return null;
        }

        final JsonObject properties = json.getAsJsonArray("properties").get(0).getAsJsonObject();

        return new Pair<>(properties.get("value").getAsString(),properties.get("signature").getAsString());
    }

    public static String getFromMojang(UUID uuid) throws IOException {

        final URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + uuid.toString().replace("-",""));
        final URLConnection conn = url.openConnection();

        conn.setDoOutput(true);

        final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        final String line = reader.readLine();

        if (line == null) {
            return null;
        }

        return line;
    }
}
