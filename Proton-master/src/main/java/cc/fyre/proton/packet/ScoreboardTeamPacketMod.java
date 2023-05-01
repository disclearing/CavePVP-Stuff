package cc.fyre.proton.packet;

import net.minecraft.server.v1_7_R4.PacketPlayOutScoreboardTeam;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

public final class ScoreboardTeamPacketMod {

    private PacketPlayOutScoreboardTeam packet;

    private static String fieldPrefix = "c";
    private static String fieldSuffix = "d";
    private static String fieldPlayers = "e";
    private static String fieldTeamName = "a";
    private static String fieldParamInt = "f";
    private static String fieldPackOption = "g";
    private static String fieldDisplayName = "b";

    public ScoreboardTeamPacketMod(String name,String prefix,String suffix,Collection players,int paramInt) {

        this.packet = new PacketPlayOutScoreboardTeam();

        this.setField(fieldTeamName,name);
        this.setField(fieldParamInt,paramInt);

        if (paramInt == 0 || paramInt == 2) {
            this.setField(fieldDisplayName,name);
            this.setField(fieldPrefix,prefix);
            this.setField(fieldSuffix,suffix);
            this.setField(fieldPackOption,3);
        }

        if (paramInt == 0) {
            this.addAll(players);
        }

    }

    public ScoreboardTeamPacketMod(String name,Collection players,int paramInt) {

        this.packet = new PacketPlayOutScoreboardTeam();

        if (players == null) {
            players = new ArrayList<String>();
        }

        this.setField(fieldTeamName, name);
        this.setField(fieldParamInt, paramInt);

        this.addAll(players);
    }

    public void sendToPlayer(Player bukkitPlayer) {
        ((CraftPlayer) bukkitPlayer).getHandle().playerConnection.sendPacket(this.packet);
    }

    public void setField(String field, Object value) {

        try {

            final Field fieldObject = this.packet.getClass().getDeclaredField(field);

            fieldObject.setAccessible(true);
            fieldObject.set(this.packet, value);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void addAll(Collection col) {

        try {

            final Field fieldObject = this.packet.getClass().getDeclaredField(fieldPlayers);

            fieldObject.setAccessible(true);
            ((Collection) fieldObject.get(this.packet)).addAll(col);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}