package cc.fyre.neutron.vault;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.profile.ProfileHandler;
import cc.fyre.neutron.profile.attributes.grant.Grant;
import cc.fyre.neutron.rank.Rank;
import cc.fyre.neutron.rank.RankHandler;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.entity.Player;
import sun.security.krb5.internal.crypto.NullEType;

import java.util.Arrays;
import java.util.function.Predicate;

public class NeutronPermissionProvider extends Permission {
    @Override
    public String getName() {
        return "Neutron";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean hasSuperPermsCompat() {
        return false;
    }

    @Override
    public boolean playerHas(String world, String player, String permission) {
//        Neutron.getInstance().getLogger().info("[Vault Check] Checking perms for: " + player + " perm: " + permission);
        Player user = Neutron.getInstance().getServer().getPlayer(player);
        if(user != null)
            return user.hasPermission(permission);

        Profile profile = Neutron.getInstance().getProfileHandler().fromName(player);
        if(profile != null)
            return profile.getEffectivePermissions().contains(permission) || profile.getEffectivePermissions().contains("*");

        return false;
    }

    @Override
    public boolean playerAdd(String world, String player, String permission) {
        Profile profile = Neutron.getInstance().getProfileHandler().fromName(player);
        if(profile != null) {
            profile.getEffectivePermissions().add(permission);
            return true;
        }
        return false;
    }

    @Override
    public boolean playerRemove(String world, String player, String permission) {
        return false;
    }

    @Override
    public boolean groupHas(String world, String group, String permission) {
        return false;
    }

    @Override
    public boolean groupAdd(String world, String group, String permission) {
        return false;
    }

    @Override
    public boolean groupRemove(String world, String group, String permission) {
        return false;
    }

    @Override
    public boolean playerInGroup(String world, String player, String group) {
        return Arrays.stream(this.getPlayerGroups(world, player)).anyMatch(s -> s.equalsIgnoreCase(group));
    }

    @Override
    public boolean playerAddGroup(String world, String player, String group) {
        return false;
    }

    @Override
    public boolean playerRemoveGroup(String world, String player, String group) {
        return false;
    }

    @Override
    public String[] getPlayerGroups(String world, String player) {
        Profile profile = Neutron.getInstance().getProfileHandler().fromName(player);

        StringBuilder sb = new StringBuilder();
        for(Grant grant : profile.getActiveGrants()) {
            if (sb.length() != 0) {
                sb.append(",");
            }
            sb.append(grant.getRank().getName());
        }
        return sb.toString().split(",");
    }

    @Override
    public String getPrimaryGroup(String world, String player) {
        Profile profile = Neutron.getInstance().getProfileHandler().fromName(player);
        return profile.getActiveRank().getName();
    }

    @Override
    public String[] getGroups() {
        StringBuilder sb = new StringBuilder();
        for(Rank rank : Neutron.getInstance().getRankHandler().getCache().values()) {
            if (sb.length() != 0) {
                sb.append(",");
            }
            sb.append(rank.getName());
        }
        return sb.toString().split(",");
    }

    @Override
    public boolean hasGroupSupport() {
        return true;
    }
}
