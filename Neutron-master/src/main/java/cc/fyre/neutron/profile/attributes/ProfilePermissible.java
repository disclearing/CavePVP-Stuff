package cc.fyre.neutron.profile.attributes;

import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.Neutron;
import com.google.common.collect.Maps;
import lombok.Getter;

import com.google.common.collect.ImmutableSet;
import org.bukkit.Server;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftHumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.*;

import java.lang.reflect.Field;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author xanderume@gmail (JavaProject)
 */
public class ProfilePermissible extends PermissibleBase {

    @Getter private Profile profile;

    public ProfilePermissible(ServerOperator serverOperator) {
        super(serverOperator);

        if (!(serverOperator instanceof Player)) {
            throw new IllegalArgumentException("Cannot inject permissible.");
        }

        this.profile = Neutron.getInstance().getProfileHandler().fromUuid(((Player)serverOperator).getUniqueId());

        try {
            this.inject((Player)serverOperator);
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public boolean isPermissionSet(Permission permission) {
        return this.isPermissionSet(permission.getName());
    }
    @Override
    public boolean isPermissionSet(String name) {
        return profile.getEffectivePermissions().contains(name);
    }

    @Override
    public boolean hasPermission(String permission) {
//        Neutron.getInstance().getLogger().info("[ProfilePermissible Check] Checking perms for: " + profile.getName() + " perm: " + permission);

        final List<String> permissions = this.profile.getEffectivePermissions();

        permissions.add(Server.BROADCAST_CHANNEL_USERS);

        if (this.isOp()) {
            permissions.add(Server.BROADCAST_CHANNEL_ADMINISTRATIVE);
        }

        if (permissions.contains("-" + permission) && !permissions.contains("+" + permission)) {
            return false;
        }

        if (this.isOp()) {
            return true;
        }

        return permissions.contains(permission) || permissions.contains("*");
    }

    @Override
    public boolean hasPermission(Permission perm) {

        final List<String> permissions = this.profile.getEffectivePermissions();

        permissions.add(Server.BROADCAST_CHANNEL_USERS);

        if (this.isOp()) {
            permissions.add(Server.BROADCAST_CHANNEL_ADMINISTRATIVE);
        }

        if (permissions.contains("-" + perm.getName()) && !permissions.contains("+" + perm.getName())) {
            return false;
        }

        if (this.isOp()) {
            return true;
        }

        return permissions.contains(perm.getName()) || permissions.contains("*");
    }

    public void inject(Player player) throws NoSuchFieldException,IllegalAccessException {
        final Field permField = this.getPermissibleField(player);

        if (permField == null) {
            return;
        }

        permField.set(player,this);
        Map<String, Boolean> permissionMap = Maps.newHashMap();
        for(String perm : profile.getEffectivePermissions()) {
            if (perm == null) {
                continue;
            }

            if(perm.contains("-" + perm) && !profile.getEffectivePermissions().contains("+" + perm)) {
                permissionMap.put(perm,false);
            } else {
                permissionMap.put(perm, true);
            }

        }

    }

    private Field getPermissibleField(Player player) throws NoSuchFieldException {

        if (!CraftHumanEntity.class.isAssignableFrom(player.getClass())) {
            return null;
        }

        final Field permField = CraftHumanEntity.class.getDeclaredField("perm");

        permField.setAccessible(true);

        return permField;
    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        Map<String, Boolean> permissionMap = Maps.newHashMap();
        for(String perm : profile.getEffectivePermissions()) {
            if(perm.contains("-" + perm) && !profile.getEffectivePermissions().contains("+" + perm)) {
                permissionMap.put(perm,false);
            } else {
                permissionMap.put(perm, true);
            }

        }

        ImmutableSet.Builder<PermissionAttachmentInfo> builder = ImmutableSet.builder();
        permissionMap.forEach((key, value) -> builder.add(new PermissionAttachmentInfo(this, key, null, value)));
        return builder.build();
    }

}
