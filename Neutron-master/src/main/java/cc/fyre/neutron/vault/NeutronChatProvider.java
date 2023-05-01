package cc.fyre.neutron.vault;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.rank.Rank;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.ChatColor;

public class NeutronChatProvider extends Chat {

    public NeutronChatProvider(Permission perms) {
        super(perms);
    }

    @Override
    public String getName() {
        return "Neutron";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getPlayerPrefix(String world, String player) {
        final Profile profile = Neutron.getInstance().getProfileHandler().fromName(player);

        if (profile.getHolidayType() != null) {
            return ChatColor.translateAlternateColorCodes('&',profile.getHolidayType().getPrefix());
        }

        return ChatColor.translateAlternateColorCodes('&', profile.getActiveRank().getName());
    }

    @Override
    public void setPlayerPrefix(String world, String player, String prefix) {

    }

    @Override
    public String getPlayerSuffix(String world, String player) {
        return "";
    }

    @Override
    public void setPlayerSuffix(String world, String player, String suffix) {

    }

    @Override
    public String getGroupPrefix(String world, String group) {
        Rank rank = Neutron.getInstance().getRankHandler().fromName(group);
        if(rank != null) {
            return ChatColor.translateAlternateColorCodes('&', rank.getPrefix());
        }
        return  ChatColor.translateAlternateColorCodes('&',Neutron.getInstance().getRankHandler().getDefaultRank().getPrefix());

    }

    @Override
    public void setGroupPrefix(String world, String group, String prefix) {

    }

    @Override
    public String getGroupSuffix(String world, String group) {
        return "";
    }

    @Override
    public void setGroupSuffix(String world, String group, String suffix) {

    }

    @Override
    public int getPlayerInfoInteger(String world, String player, String node, int defaultValue) {
        return 0;
    }

    @Override
    public void setPlayerInfoInteger(String world, String player, String node, int value) {

    }

    @Override
    public int getGroupInfoInteger(String world, String group, String node, int defaultValue) {
        return 0;
    }

    @Override
    public void setGroupInfoInteger(String world, String group, String node, int value) {

    }

    @Override
    public double getPlayerInfoDouble(String world, String player, String node, double defaultValue) {
        return 0;
    }

    @Override
    public void setPlayerInfoDouble(String world, String player, String node, double value) {

    }

    @Override
    public double getGroupInfoDouble(String world, String group, String node, double defaultValue) {
        return 0;
    }

    @Override
    public void setGroupInfoDouble(String world, String group, String node, double value) {

    }

    @Override
    public boolean getPlayerInfoBoolean(String world, String player, String node, boolean defaultValue) {
        return false;
    }

    @Override
    public void setPlayerInfoBoolean(String world, String player, String node, boolean value) {

    }

    @Override
    public boolean getGroupInfoBoolean(String world, String group, String node, boolean defaultValue) {
        return false;
    }

    @Override
    public void setGroupInfoBoolean(String world, String group, String node, boolean value) {

    }

    @Override
    public String getPlayerInfoString(String world, String player, String node, String defaultValue) {
        return null;
    }

    @Override
    public void setPlayerInfoString(String world, String player, String node, String value) {

    }

    @Override
    public String getGroupInfoString(String world, String group, String node, String defaultValue) {
        return null;
    }

    @Override
    public void setGroupInfoString(String world, String group, String node, String value) {

    }
}
