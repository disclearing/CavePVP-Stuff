package net.frozenorb.foxtrot.gameplay.clickitem.type;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.AbilityHandler;
import net.frozenorb.foxtrot.gameplay.ability.Category;
import net.frozenorb.foxtrot.gameplay.bosses.BossHandler;
import net.frozenorb.foxtrot.gameplay.clickitem.ClickItem;
import org.bukkit.Bukkit;
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

public class RandomAbilityItem extends ClickItem {

    @Override
    public String getId() {
        return "random-ability-item";
    }

    @Override
    public String getDisplayName() {
        return ChatColor.AQUA + ChatColor.BOLD.toString() + "Random Ability Item";
    }

    @Override
    public List<String> getLore() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add("");
        toReturn.add(ChatColor.GREEN + "Right Click to receive a random ability item");

        return toReturn;
    }

    @Override
    public Material getMaterial() {
        return Material.NETHER_STAR;
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();

        boolean activate = this.redeem(event.getPlayer(), event);

        if (!activate) {
            return;
        }

        final AbilityHandler abilityHandler = Foxtrot.getInstance().getMapHandler().getAbilityHandler();
        final List<Ability> abilities = abilityHandler.getAbilities().values().stream().filter(it -> it.getCategory() != Category.KIT_MAP).collect(Collectors.toList());

        final Ability randomAbility = abilities.get(ThreadLocalRandom.current().nextInt(abilities.size()));

        player.sendMessage(ChatColor.GREEN + "You have been given the " + randomAbility.getDisplayName() + ChatColor.GREEN + " ability!");
        player.getInventory().addItem(randomAbility.hassanStack.clone());
    }
}
