package cc.fyre.neutron.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.experimental.UtilityClass;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.properties.Property;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.UUID;

@UtilityClass
public class MojangUtil {

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

        String part = id[0];
        part = part.substring(7, 39);

        final UUID toReturn = UUID.fromString(String.valueOf(part)
                .replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5")
        );

        return toReturn;
    }

    //TODO: haven't tested yet
    public static String getFromMojang(UUID uuid) throws IOException {

        final URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + uuid.toString().replace("-",""));
        final URLConnection conn = url.openConnection();

        conn.setDoOutput(true);

        final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        final String line = reader.readLine();

        if (line == null) {
            return null;
        }

        final String toReturn = String.valueOf(line);

        return toReturn;
    }

    public String[] getSkinFromPlayer(Player player) {

        final EntityPlayer playerNMS = ((CraftPlayer) player).getHandle();
        final GameProfile profile = playerNMS.getProfile();
        final Property property = profile.getProperties().get("textures").iterator().next();
        final String texture = property.getValue();
        final String signature = property.getSignature();

        return new String[] {texture, signature};
    }

    public String[] getSkinFromName(String name) {

        try {

            final URL url_0 = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
            final InputStreamReader reader_0 = new InputStreamReader(url_0.openStream());

            if (reader_0 == null) {
                return null;
            }

            final String uuid = new JsonParser().parse(reader_0).getAsJsonObject().get("id").getAsString();

            final URL url_1 = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
            final InputStreamReader reader_1 = new InputStreamReader(url_1.openStream());
            final JsonObject textureProperty = new JsonParser().parse(reader_1).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();

            final String texture = textureProperty.get("value").getAsString();
            final String signature = textureProperty.get("signature").getAsString();

            return new String[] {texture, signature};
        } catch (IOException e) {
            System.out.println("Failed to request skin from Mojang.");
            e.printStackTrace();
            return null;
        }
    }
}
