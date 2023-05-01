package net.frozenorb.foxtrot.gameplay.loot.itemboxes.type;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.AbilityHandler;
import net.frozenorb.foxtrot.gameplay.ability.Category;
import net.frozenorb.foxtrot.gameplay.loot.itemboxes.ItemBox;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class AbilityBox extends ItemBox {

    @Override
    public String getId() {
        return "AbilityBox";
    }

    @Override
    public String getDisplayName() {
        return ChatColor.AQUA + ChatColor.BOLD.toString() + "Ability Box";
    }

    @Override
    public List<String> getLore() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add(ChatColor.GRAY + "Get a random ability item!");
        toReturn.add("");
        toReturn.add(ChatColor.GREEN + "Right Click to redeem this box");

        return toReturn;
    }

    @Override
    public Material getMaterial() {
        return Material.CHEST;
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();

        boolean activate = this.redeem(event.getPlayer(), event);

        if (!activate) {
            return;
        }

        final AbilityHandler abilityHandler = Foxtrot.getInstance().getMapHandler().getAbilityHandler();
        final List<Ability> eligibleItems = abilityHandler.getAbilities().values().stream().filter(it -> it.getCategory() != Category.KIT_MAP).collect(Collectors.toList());

        final Ability finalAbility = eligibleItems.get(ThreadLocalRandom.current().nextInt(eligibleItems.size()));

        player.getInventory().addItem(finalAbility.hassanStack.clone());
    }
}
