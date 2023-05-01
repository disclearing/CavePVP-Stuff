package cc.fyre.proton.nametag;

import cc.fyre.proton.Proton;
import cc.fyre.proton.nametag.construct.NameTagComparator;
import cc.fyre.proton.nametag.construct.NameTagUpdate;
import cc.fyre.proton.nametag.listener.NameTagListener;
import cc.fyre.proton.packet.ScoreboardTeamPacketMod;
import lombok.Getter;
import lombok.Setter;
import cc.fyre.proton.nametag.construct.NameTagInfo;
import cc.fyre.proton.nametag.provider.DefaultNameTagProvider;
import cc.fyre.proton.nametag.provider.NameTagProvider;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class NameTagHandler {

    @Getter private Map<String, Map<String,NameTagInfo>> teamMap = new ConcurrentHashMap<>();
    @Getter private List<NameTagInfo> registeredTeams = Collections.synchronizedList(new ArrayList<>());
    @Getter private int teamCreateIndex = 1;
    @Getter private List<NameTagProvider> providers = new ArrayList<>();
    @Getter private boolean nametagRestrictionEnabled = false;
    @Getter private String nametagRestrictBypass = "";
    @Getter @Setter private boolean async = true;
    @Getter @Setter private int updateInterval = 2;
    @Getter @Setter private NameTagInfo INVISIBLE;

    public NameTagHandler() {

        if (Proton.getInstance().getConfig().getBoolean("disableNametags",false)) {
            return;
        }

        this.nametagRestrictionEnabled = Proton.getInstance().getConfig().getBoolean("NametagPacketRestriction.Enabled", false);
        this.nametagRestrictBypass = Proton.getInstance().getConfig().getString("NametagPacketRestriction.BypassPrefix").replace("&", "§");

        Proton.getInstance().getServer().getPluginManager().registerEvents(new NameTagListener(),Proton.getInstance());
        this.registerProvider(new DefaultNameTagProvider());

        Proton.getInstance().getServer().getScheduler().runTaskLater(Proton.getInstance(), () -> setINVISIBLE(Proton.getInstance().getNameTagHandler().getOrCreate("aaa", "aaa")), 10);

        new NameTagThread().start();
    }

    public void registerProvider(NameTagProvider newProvider) {
        this.providers.add(newProvider);
        Collections.sort(this.providers,new NameTagComparator());
    }

    public void reloadPlayer(Player toRefresh) {

        final NameTagUpdate update = new NameTagUpdate(toRefresh);

        if (this.async) {
            NameTagThread.getPendingUpdates().put(update, true);
        } else {
            this.applyUpdate(update);
        }

    }

    public void reloadOthersFor(Player refreshFor) {

        for (Player toRefresh : Proton.getInstance().getServer().getOnlinePlayers()) {

            if (refreshFor != toRefresh) {
                this.reloadPlayer(toRefresh, refreshFor);
            }

        }

    }

    public void reloadPlayer(Player toRefresh, Player refreshFor) {

        final NameTagUpdate update = new NameTagUpdate(toRefresh, refreshFor);

        if (this.async) {
            NameTagThread.getPendingUpdates().put(update, true);
        } else {
            this.applyUpdate(update);
        }

    }

    public void applyUpdate(NameTagUpdate nametagUpdate) {

        final Player toRefreshPlayer = Proton.getInstance().getServer().getPlayerExact(nametagUpdate.getToRefresh());

        if (toRefreshPlayer != null) {

            if (nametagUpdate.getRefreshFor() == null) {

                for (Player refreshFor : Proton.getInstance().getServer().getOnlinePlayers()) {
                    reloadPlayerInternal(toRefreshPlayer,refreshFor);
                }

            } else {

                final Player refreshForPlayer = Proton.getInstance().getServer().getPlayerExact(nametagUpdate.getRefreshFor());

                if (refreshForPlayer != null) {
                    reloadPlayerInternal(toRefreshPlayer,refreshForPlayer);
                }
            }
        }
    }

    public void reloadPlayerInternal(Player toRefresh, Player refreshFor) {
        if (refreshFor.hasMetadata("ProtonNametag-LoggedIn")) {
            
            NameTagInfo provided = null;

            for (int i = 0; provided == null; provided = (this.providers.get(i++).fetchNameTag(toRefresh,refreshFor))) {
                
            }

            final EntityPlayer handle = ((CraftPlayer)refreshFor).getHandle();

            if (provided == INVISIBLE || provided.getPrefix().equalsIgnoreCase("aaa") && provided.getSuffix().equalsIgnoreCase("aaa")) {
                if (handle.playerConnection.networkManager.getVersion() > 5) {
                    final Map<String, NameTagInfo> localTeamMap = teamMap.get(refreshFor.getName());

                    if (localTeamMap == null) {
                        return;
                    }
                    
                    final NameTagInfo nameTagInfo = teamMap.get(refreshFor.getName()).get(toRefresh.getName());

                    if (nameTagInfo == null) {
                        return;
                    }

                    new ScoreboardTeamPacketMod(nameTagInfo.getName(), Collections.singletonList(toRefresh.getName()), 4).sendToPlayer(refreshFor);

                    handle.playerConnection.sendPacket(PacketPlayOutPlayerInfo.removePlayer(((CraftPlayer)toRefresh).getHandle()));
                }
                return;
            }

            if (((CraftPlayer)refreshFor).getHandle().playerConnection.networkManager.getVersion() > 5 && this.nametagRestrictionEnabled) {
                
                final String prefix = provided.getPrefix();
              
                if (prefix != null && !prefix.equalsIgnoreCase(this.nametagRestrictBypass)) {
                    return;
                }
            }

            Map<String,NameTagInfo> teamInfoMap = new HashMap();
            
            if (this.teamMap.containsKey(refreshFor.getName())) {
                teamInfoMap = this.teamMap.get(refreshFor.getName());
            }

            new ScoreboardTeamPacketMod(provided.getName(), Collections.singletonList(toRefresh.getName()), 3).sendToPlayer(refreshFor);
            
            teamInfoMap.put(toRefresh.getName(),provided);

            this.teamMap.put(refreshFor.getName(), teamInfoMap);
        }
    }

    public void initiatePlayer(Player player) {

        for (NameTagInfo teamInfo : this.registeredTeams) {
            teamInfo.getTeamAddPacket().sendToPlayer(player);
        }

    }

    public NameTagInfo getOrCreate(String prefix,String suffix) {

        for (NameTagInfo teamInfo : this.registeredTeams) {

            if (teamInfo.getPrefix().equals(prefix) && teamInfo.getSuffix().equals(suffix)) {
                return teamInfo;
            }

        }

        final NameTagInfo newTeam = new NameTagInfo(String.valueOf(this.teamCreateIndex++), prefix, suffix);

        this.registeredTeams.add(newTeam);

        final ScoreboardTeamPacketMod addPacket = newTeam.getTeamAddPacket();

        for (Player player : Proton.getInstance().getServer().getOnlinePlayers()) {
            addPacket.sendToPlayer(player);
        }

        return newTeam;
    }

}