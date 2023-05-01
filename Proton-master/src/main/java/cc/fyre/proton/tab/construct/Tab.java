package cc.fyre.proton.tab.construct;

import cc.fyre.proton.Proton;
import cc.fyre.proton.tab.util.TabUtils;
import lombok.Getter;
import cc.fyre.proton.packet.PlayerInfoPacketMod;
import cc.fyre.proton.packet.ScoreboardTeamPacketMod;
import net.minecraft.server.v1_7_R4.ChatSerializer;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.spigotmc.ProtocolInjector;

import java.util.*;

public class Tab {

    private Player player;
    private Map<String,String> previousNames = new HashMap();
    private Map<String,Integer> previousPings = new HashMap();
    private String lastHeader = "{\"translate\":\"\"}";
    private String lastFooter = "{\"translate\":\"\"}";
    private Set<String> createdTeams = new HashSet();
    private TabLayout initialLayout;
    @Getter private boolean initiated = false;
    private final StringBuilder removeColorCodesBuilder = new StringBuilder();

    public Tab(Player player) {
        this.player = player;
    }

    private void createAndAddMember(String name,String member) {

        final ScoreboardTeamPacketMod scoreboardTeamAdd = new ScoreboardTeamPacketMod("$"+name,"","",Collections.singletonList(member),0);
        scoreboardTeamAdd.sendToPlayer(this.player);
    }

    private void create() {
        if (!this.initiated) {

            this.initiated = true;

            final TabLayout initialLayout = TabLayout.createEmpty(this.player);

            if (!initialLayout.is18()) {

                for (Player onlinePlayer : Proton.getInstance().getServer().getOnlinePlayers()) {
                    this.updateTabList(onlinePlayer.getName(),0,((CraftPlayer)onlinePlayer).getProfile(),4);
                }

            }

            final String[] tabNames = initialLayout.getTabNames();

            for (int i = 0; i < tabNames.length; i++) {

                final String s = tabNames[i];

                this.updateTabList(s,0,0);

                final String teamName = s.replaceAll("§","");

                if (!this.createdTeams.contains(teamName)) {
                    this.createAndAddMember(teamName,s);
                    this.createdTeams.add(teamName);
                }
            }

            this.initialLayout = initialLayout;
        }

    }

    private void updateScore(String score,String prefix,String suffix) {
        final ScoreboardTeamPacketMod scoreboardTeamModify = new ScoreboardTeamPacketMod(score,prefix,suffix,null,2);
        scoreboardTeamModify.sendToPlayer(this.player);
    }

    private void updateTabList(String name,int ping,int action) {
        this.updateTabList(name,ping,TabUtils.getOrCreateProfile(name),action);
    }

    private void updateTabList(String name,int ping,GameProfile profile,int action) {
        final PlayerInfoPacketMod playerInfoPacketMod = new PlayerInfoPacketMod("$"+name,ping,profile,action);
        playerInfoPacketMod.sendToPlayer(this.player);
    }

    private String[] splitString(String line) {
        return line.length() <= 16 ? new String[]{line,""} : new String[]{line.substring(0,16),line.substring(16)};
    }

    public void update() {

        if (Proton.getInstance().getTabHandler().getLayoutProvider() == null) {
            return;
        }

        final TabLayout tabLayout = Proton.getInstance().getTabHandler().getLayoutProvider().provide(this.player);

        if (tabLayout == null) {

            if (this.initiated) {
                this.reset();
            }

            return;

        }

        this.create();

        String entry;

        for (int y = 0; y < TabLayout.HEIGHT; y++) {

            for (int x = 0; x < TabLayout.WIDTH; x++) {

                entry = tabLayout.getStringAt(x,y);
                int ping = tabLayout.getPingAt(x,y);

                final String entryName = this.initialLayout.getStringAt(x,y);

                this.removeColorCodesBuilder.setLength(0);
                this.removeColorCodesBuilder.append('$');
                this.removeColorCodesBuilder.append(entryName);
                int j = 0;

                for (int i = 0; i < this.removeColorCodesBuilder.length(); ++i) {

                    if (167 != this.removeColorCodesBuilder.charAt(i)) {
                        this.removeColorCodesBuilder.setCharAt(j++,this.removeColorCodesBuilder.charAt(i));
                    }
                }

                this.removeColorCodesBuilder.delete(j,this.removeColorCodesBuilder.length());
                String teamName = this.removeColorCodesBuilder.toString();
                if (this.previousNames.containsKey(entryName)) {
                    if (!(this.previousNames.get(entryName)).equals(entry)) {
                        this.update(entryName,teamName,entry,ping);
                    } else if (this.previousPings.containsKey(entryName) && this.pingToBars(this.previousPings.get(entryName)) != this.pingToBars(ping)) {
                        this.updateTabList(entryName,ping,2);
                        this.previousPings.put(entryName,ping);
                    }
                } else {
                    this.update(entryName,teamName,entry,ping);
                }

            }
        }

        boolean sendHeader = false;
        boolean sendFooter = false;
        entry = tabLayout.getHeader();
        String footer = tabLayout.getFooter();
        if (!entry.equals(this.lastHeader)) {
            sendHeader = true;
        }

        if (!footer.equals(this.lastFooter)) {
            sendFooter = true;
        }

        if (tabLayout.is18() && (sendHeader || sendFooter)) {
            ProtocolInjector.PacketTabHeader packet = new ProtocolInjector.PacketTabHeader(ChatSerializer.a(entry),ChatSerializer.a(footer));
            ((CraftPlayer)this.player).getHandle().playerConnection.sendPacket(packet);
            this.lastHeader = entry;
            this.lastFooter = footer;
        }
    }


    private void reset() {
        this.initiated = false;
        int count;

        for (int i = 0; i < this.initialLayout.getTabNames().length; i++) {

            final String s = this.initialLayout.getTabNames()[i];

            this.updateTabList(s,0,4);
        }

        EntityPlayer ePlayer = ((CraftPlayer)this.player).getHandle();

        this.updateTabList(this.player.getName(),ePlayer.ping,ePlayer.getProfile(),0);


        count = 1;

        for (Player onlinePlayer : Proton.getInstance().getServer().getOnlinePlayers()) {

            if (this.player != onlinePlayer) {

                if (count > this.initialLayout.getTabNames().length-1) {
                    break;
                }

                ePlayer = ((CraftPlayer)player).getHandle();
                this.updateTabList(player.getName(),ePlayer.ping,ePlayer.getProfile(),0);
                ++count;
            }

        }

    }

    private void update(String entryName,String teamName,String entry,int ping) {

        final String[] entryStrings = this.splitString(entry);

        String prefix = entryStrings[0];
        String suffix = entryStrings[1];

        if (!suffix.isEmpty()) {

            if (prefix.charAt(prefix.length()-1) == 167) {
                prefix = prefix.substring(0,prefix.length()-1);
                suffix = '§' + suffix;
            }

            String suffixPrefix = ChatColor.RESET.toString();

            if (!ChatColor.getLastColors(prefix).isEmpty()) {
                suffixPrefix = ChatColor.getLastColors(prefix);
            }

            if (suffix.length() <= 14) {
                suffix = suffixPrefix+suffix;
            } else {
                suffix = suffixPrefix+suffix.substring(0,14);
            }
        }

        this.updateScore(teamName,prefix,suffix);
        this.updateTabList(entryName,ping,2);
        this.previousNames.put(entryName,entry);
        this.previousPings.put(entryName,ping);
    }

    private int pingToBars(int ping) {
        if (ping < 0) {
            return 5;
        } else if (ping < 150) {
            return 0;
        } else if (ping < 300) {
            return 1;
        } else if (ping < 600) {
            return 2;
        } else if (ping < 1000) {
            return 3;
        } else {
            return ping < 32767 ? 4 : 5;
        }
    }

}
