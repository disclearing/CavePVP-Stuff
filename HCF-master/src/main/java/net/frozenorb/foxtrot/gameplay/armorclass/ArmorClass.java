package net.frozenorb.foxtrot.gameplay.armorclass;

import cc.fyre.proton.util.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public abstract class ArmorClass implements Listener {
    @Getter @Setter private Entity entity;
    
    public abstract String getId();
    public abstract String getDisplayName();
    public abstract int getSlot();
    public abstract Material getDisplayItem();
    public abstract ChatColor getChatColor();
    public abstract List<String> getPerks();
    public abstract ItemStack createPiece(ArmorPiece armorPiece);

    public ArmorClass() {
        Foxtrot.getInstance().getServer().getPluginManager().registerEvents(this, Foxtrot.getInstance());
    }

    public Category getCategory() {
        return Category.ALL;
    }

    public List<Material> findApplicableItems() {
        return Arrays.asList(Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS);
    }

    public boolean isAllowedAtLocation(Location location) {

        if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
            return true;
        }

        return !Foxtrot.getInstance().getServerHandler().isWarzone(location) && location.getWorld().getEnvironment() == World.Environment.NORMAL && !DTRBitmask.KOTH.appliesAt(location) && !DTRBitmask.CONQUEST.appliesAt(location) && !DTRBitmask.CITADEL.appliesAt(location) && !DTRBitmask.DTC.appliesAt(location) && !DTRBitmask.SAFE_ZONE.appliesAt(location);
    }

    public void apply(Player player) {

    }

    public void unapply(Player player) {

    }

    public ItemStack getRedeemItem() {
        final ItemBuilder itemBuilder = ItemBuilder.of(Material.CHEST).name(this.getDisplayName() + " Armor Class").addToLore("", getChatColor() + "Perks:");

        for (String perk : this.getPerks()) {
            itemBuilder.addToLore(getChatColor() + "❙ &f" + perk);
        }
        itemBuilder.addToLore("", "&aClick to redeem this armor set");

        return itemBuilder.build();
    }

    public boolean isWearing(Player player) {
        if (!ArmorClassHandler.isAllowed(player.getLocation())) {
            return false;
        }

        for (ItemStack itemStack : player.getInventory().getArmorContents()) {
            if(itemStack == null || !isPiece(itemStack)) {
                return false;
            }
        }

        return true;
    }

    public boolean isPiece(ItemStack itemStack){
        if (itemStack == null || itemStack.getItemMeta() == null || itemStack.getItemMeta().getLore() == null) {
            return false;
        }

        if (itemStack.getItemMeta().getLore() == null || itemStack.getItemMeta().getLore().isEmpty()) {
            return false;
        }

        final List<String> lore = itemStack.getItemMeta().getLore();

        return lore.stream().anyMatch(it -> ChatColor.stripColor(it).equalsIgnoreCase("Armor Class: " + ChatColor.stripColor(this.getDisplayName())));
    }

    public boolean isRedeemable(ItemStack itemStack) {

        if (itemStack == null || itemStack.getItemMeta() == null || itemStack.getType() == Material.AIR) {
            return false;
        }

        if (itemStack.getItemMeta().getDisplayName() == null) {
            return false;
        }

        if (itemStack.getItemMeta().getLore() == null || itemStack.getItemMeta().getLore().isEmpty()) {
            return false;
        }

        return itemStack.getType() == this.getRedeemItem().getType() && ChatColor.stripColor(itemStack.getItemMeta().getDisplayName()).startsWith(ChatColor.stripColor(this.getDisplayName())) && itemStack.getItemMeta().getLore().get(0).equals(this.getRedeemItem().getItemMeta().getLore().get(0));
    }

    public String getPieceName(ArmorPiece armorPiece) {
        switch(armorPiece) {
            case HELMET:
                return "Helmet";
            case CHESTPLATE:
                return "Chestplate";
            case LEGGINGS:
                return "Leggings";
            case BOOTS:
                return "Boots";
            case SWORD:
                return "Sword";
            case AXE:
                return "Axe";
            case BOW:
                return "Bow";
        }

        return "";
    }
}
