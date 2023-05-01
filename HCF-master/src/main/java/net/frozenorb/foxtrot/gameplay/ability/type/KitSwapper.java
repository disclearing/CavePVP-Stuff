package net.frozenorb.foxtrot.gameplay.ability.type;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.Category;
import net.frozenorb.foxtrot.gameplay.ability.listener.events.AbilityUseEvent;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.Item;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class KitSwapper extends Ability {

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Material getMaterial() {
        return Material.YELLOW_FLOWER;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.YELLOW.toString() + ChatColor.BOLD + "Kit Swapper";
    }

    @Override
    public List<String> getLore() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add("");
        toReturn.add(ChatColor.translate("&6❙ &fRight Click to switch your armor"));
        toReturn.add(ChatColor.translate("&6❙ &fto another armor type that's in your inventory!"));
        toReturn.add("");
        toReturn.add(ChatColor.translate("&fCan be found in the &d&lAbility Crate&f!"));

        return toReturn;
    }

    @Override
    public Boolean isAllowedAtLocation(Location location) {
        if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
            return !DTRBitmask.KOTH.appliesAt(location) && !DTRBitmask.CONQUEST.appliesAt(location) && !DTRBitmask.CITADEL.appliesAt(location) && !DTRBitmask.DTC.appliesAt(location) && !DTRBitmask.SAFE_ZONE.appliesAt(location);
        }

        return !Foxtrot.getInstance().getServerHandler().isWarzone(location) && location.getWorld().getEnvironment() == World.Environment.NORMAL && !DTRBitmask.KOTH.appliesAt(location) && !DTRBitmask.CONQUEST.appliesAt(location) && !DTRBitmask.CITADEL.appliesAt(location) && !DTRBitmask.DTC.appliesAt(location) && !DTRBitmask.SAFE_ZONE.appliesAt(location);
    }

    @Override
    public long getCooldown() {
        return 30_000L;
    }

    @Override
    public Category getCategory() {
        return Category.KIT_MAP;
    }

    @Override
    public String getDescription() {
        return "Your armor has switched to another kit!";
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        final Player player = event.getPlayer();

        if (!this.isSimilar(event.getItem())) {
            return;
        }

        event.setCancelled(true);

        final AbilityUseEvent abilityUseEvent = new AbilityUseEvent(player, null, player.getLocation(), this, false);
        Foxtrot.getInstance().getServer().getPluginManager().callEvent(abilityUseEvent);

        if (abilityUseEvent.isCancelled()) {
            return;
        }

        if (player.getItemInHand().getAmount() == 1) {
            player.setItemInHand(null);
        } else {
            player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
        }

        final HashMap<Integer,ItemStack> items = new HashMap<>();

        final Item.ArmorType current = Arrays.stream(Item.ArmorType.values()).filter(it -> Arrays.stream(player.getInventory().getArmorContents()).filter(Objects::nonNull).anyMatch(armor -> armor.getType().name().startsWith(it.name()))).findFirst().orElse(null);

        if (current == null) {
            player.sendMessage(ChatColor.RED + "You are not wearing a full set.");
            return;
        }

        for (Item.ArmorType type : Item.ArmorType.values()) {

            if (type == current) {
                continue;
            }

            final Map<ItemStack,Integer> slots = new HashMap<>();
            final Map<Item.ArmorPart,ItemStack> armor = new HashMap<>();

            for (Item.ArmorPart part : Item.ArmorPart.values()) {
                armor.put(part,null);
            }

            int i = 0;

            for (ItemStack content : player.getInventory().getContents()) {

                if (content != null) {

                    for (Item.ArmorPart part : Item.ArmorPart.values()) {

                        if (!content.getType().name().startsWith(type.name()) || !content.getType().name().endsWith(part.name())) {
                            continue;
                        }

                        if (armor.containsKey(part) && armor.get(part) != null && content.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL) < armor.get(part).getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL)) {
                            continue;
                        }

                        armor.put(part,content);
                        slots.put(content,i);
                    }

                }

                i++;
            }

            if (armor.values().stream().filter(Objects::nonNull).count() < 4) {
                continue;
            }

            armor.values().forEach(it -> items.put(slots.remove(it),it));
            break;
        }

        if (items.isEmpty()) {
            player.sendMessage(ChatColor.RED + "You have no other sets in your inventory.");
            return;
        }

        final AtomicInteger tick = new AtomicInteger(2);

        items.forEach((key,value) -> Bukkit.getScheduler().runTaskLater(Foxtrot.getInstance(),() -> {

            final Item.ArmorPart part = Item.ArmorPart.valueOf(value.getType().name().split("_")[1]);

            if (part == Item.ArmorPart.HELMET) {
                player.getInventory().setItem(key,player.getInventory().getHelmet());
                player.getInventory().setHelmet(value);
            } else if (part == Item.ArmorPart.CHESTPLATE) {
                player.getInventory().setItem(key,player.getInventory().getChestplate());
                player.getInventory().setChestplate(value);
            } else if (part == Item.ArmorPart.LEGGINGS) {
                player.getInventory().setItem(key,player.getInventory().getLeggings());
                player.getInventory().setLeggings(value);
            } else {
                player.getInventory().setItem(key,player.getInventory().getBoots());
                player.getInventory().setBoots(value);
            }

        },tick.getAndIncrement()));

        player.updateInventory();

        this.applyCooldown(player);
    }
}