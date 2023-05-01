package net.frozenorb.foxtrot.gameplay.armorclass.type.kitmap;

import cc.fyre.proton.util.ItemBuilder;
import cc.fyre.proton.util.TimeUtils;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.type.Invisibility;
import net.frozenorb.foxtrot.gameplay.ability.type.kitmap.fall.PiercingShot;
import net.frozenorb.foxtrot.gameplay.armorclass.ArmorClass;
import net.frozenorb.foxtrot.gameplay.armorclass.ArmorPiece;
import net.frozenorb.foxtrot.gameplay.armorclass.Category;
import net.frozenorb.foxtrot.gameplay.pvpclasses.PvPClassHandler;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class CasperTheGhost extends ArmorClass {
    private Map<UUID, Long> cooldown = new HashMap<>();

    @Override
    public String getId() {
        return "caspertheghost";
    }

    @Override
    public String getDisplayName() {
        return ChatColor.WHITE + ChatColor.BOLD.toString() + "Casper The Ghost";
    }

    @Override
    public int getSlot() {
        return 10;
    }

    @Override
    public Material getDisplayItem() {
        return Material.IRON_INGOT;
    }

    @Override
    public ChatColor getChatColor() {
        return ChatColor.WHITE;
    }

    @Override
    public List<String> getPerks() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add("Ability to see nametags of invisible players.");
        toReturn.add("30% more damage to players with over 6 partner items.");
        toReturn.add("Shift Right Click to go fully invisible for 15 seconds.");

        return toReturn;
    }

    @Override
    public Category getCategory() {
        return Category.DIAMOND;
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onInteract(PlayerInteractEvent event) {
        if (!event.getAction().name().contains("RIGHT")) {
            return;
        }

        final Player player = event.getPlayer();

        if (!this.isWearing(player)) {
            return;
        }

        if (!player.isSneaking()) {
            return;
        }

        if (cooldown.containsKey(player.getUniqueId()) && cooldown.get(player.getUniqueId()) > System.currentTimeMillis()) {
            long millisLeft = ((cooldown.get(player.getUniqueId()) - System.currentTimeMillis()) / 1000L) * 1000L;
            String msg = TimeUtils.formatIntoDetailedString((int) (millisLeft / 1000));

            player.sendMessage(ChatColor.RED + "You cannot use this for another §c§l" + msg + "§c.");
            return;
        }

        event.getPlayer().addPotionEffect(Invisibility.EFFECT, true);
        event.getPlayer().setFireTicks(0);

        ((CraftPlayer)event.getPlayer()).getHandle().getDataWatcher().watch(9, (byte) 0);

        this.sendRestorePacket(event.getPlayer(),Foxtrot.getInstance().getServer().getOnlinePlayers(),true);
    }

    @EventHandler
    private void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) {
            return;
        }

        final Player damager = (Player) event.getDamager();
        final Player target = (Player) event.getEntity();

        if (!isWearing(damager)) {
            return;
        }

        int number = 0;

        for (ItemStack content : target.getInventory().getContents()) {
            if (content == null) {
                continue;
            }

            if (Foxtrot.getInstance().getMapHandler().getAbilityHandler().getAbilities().values().stream().noneMatch(it -> it.isSimilar(content))) {
                continue;
            }

            number++;
        }

        if (number >= 6) {
            damager.sendMessage(CC.translate(target.getName() + " &6has over 6 partner items in their inventory therefor you delt 30% more damage!"));
            target.sendMessage(CC.translate(damager.getName() + " &cis in a " + this.getDisplayName() + " &cset and you have over 6 partner items so they delt 30% more damage to you!"));
            event.setDamage(event.getDamage()*1.3);
        }
    }

    @Override
    public ItemStack createPiece(ArmorPiece armorPiece) {
        final ItemBuilder itemBuilder = ItemBuilder.of(armorPiece.getDefaultMaterial())
                .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .enchant(Enchantment.DURABILITY, 3)
                .name(getDisplayName() + " " + getPieceName(armorPiece))
                .addToLore("", getChatColor() + "Armor Class: &f" + ChatColor.stripColor(getDisplayName()), getChatColor() + "Perks:");
        for (String perk : this.getPerks()) {
            itemBuilder.addToLore(getChatColor() + "❙ &f" + perk);
        }
        itemBuilder.addToLore("");

        return itemBuilder.build();
    }

    @Override
    public void apply(Player player) {
    }

    @Override
    public void unapply(Player player) {
    }

    private void sendRestorePacket(Player player, Collection<? extends Player> players, boolean clear) {

        final List<PacketContainer> packets = new ArrayList<>();

        for (int i = 0; i < 4; i++) {

            final PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_EQUIPMENT);

            packet.getIntegers().write(0,player.getEntityId());
            packet.getIntegers().write(1,i+1);

            packet.getItemModifier().write(0,clear ? new ItemStack(Material.AIR):player.getInventory().getArmorContents()[i]);

            packets.add(packet);
        }

        players.stream().filter(it -> it.getUniqueId() != player.getUniqueId()).forEach(it -> packets.forEach(packet -> {

            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket(it,packet);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }));

    }
}
