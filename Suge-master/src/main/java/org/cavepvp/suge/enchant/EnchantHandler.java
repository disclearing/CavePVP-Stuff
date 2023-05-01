package org.cavepvp.suge.enchant;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.cavepvp.suge.Suge;
import org.cavepvp.suge.enchant.data.CustomEnchant;
import org.cavepvp.suge.enchant.data.type.*;
import org.cavepvp.suge.enchant.listener.EnchantListener;
import org.cavepvp.suge.util.RomanUtil;

import java.util.*;
import java.util.stream.Collectors;

public class EnchantHandler {
    private Suge instance;

    @Getter
    private EnchantBookHandler enchantBookHandler;
    @Getter
    private List<CustomEnchant> customEnchants = new ArrayList<>();

    public EnchantHandler(Suge instance) {
        this.instance = instance;

        customEnchants.add(new FireResistanceEnchant());
        customEnchants.add(new GlowingEnchant());
        customEnchants.add(new HellForgedEnchant());
        customEnchants.add(new ImplantsEnchant());
        customEnchants.add(new MermaidEnchant());
        customEnchants.add(new SpeedEnchant());
        customEnchants.add(new InvisibilityEnchant());
        customEnchants.add(new GreedEnchant());
        customEnchants.add(new FuryEnchant());
        customEnchants.add(new ResistorEnchant());
        customEnchants.add(new FarmerEnchant());
        customEnchants.add(new EvaderEnchant());
        customEnchants.add(new GuardianAngelEnchant());

        this.enchantBookHandler = new EnchantBookHandler(this.instance);
        this.instance.getServer().getPluginManager().registerEvents(new EnchantListener(this.instance), this.instance);
    }

    public Map<CustomEnchant, Integer> findAllCustomEnchants(Player player, boolean skipNetherCitadel) {
        final Map<CustomEnchant, Integer> customEnchants = new HashMap<>();

        if (!skipNetherCitadel && isNetherCitadel(player)) {
            return new HashMap<>();
        }

        for (ItemStack itemStack : Arrays.stream(player.getInventory().getArmorContents()).filter(it -> it != null && it.getItemMeta() != null && it.getItemMeta().getLore() != null).collect(Collectors.toList())) {
            customEnchants.putAll(this.instance.getEnchantHandler().findAllCustomEnchants(itemStack.getItemMeta().getLore(), skipNetherCitadel, player));
        }

        return customEnchants;
    }

    public CustomEnchant findCustomEnchant(String name) {
        return customEnchants.stream().filter(it -> name.contains(it.getName())).findFirst().orElse(null);
    }

    public Map<CustomEnchant, Integer> findAllCustomEnchants(Player player) {
        return findAllCustomEnchants(player, false);
    }

    public Map<CustomEnchant, Integer> findAllCustomEnchants(List<String> lore, boolean skipNetherCitadel, Player player) {
        if (lore == null || lore.isEmpty()) {
            return new HashMap<>();
        }

        if (!skipNetherCitadel && isNetherCitadel(player)) {
            return new HashMap<>();
        }

        final Map<CustomEnchant, Integer> customEnchants = new HashMap<>();

        for (String line : lore) {
            if (!line.contains(" ")) {
                continue;
            }

            final String[] splitLine = ChatColor.stripColor(line).split(" ");

            if (splitLine.length < 2) {
                continue;
            }

            final CustomEnchant customEnchant = this.findCustomEnchant(splitLine[0]);

            if (customEnchant == null) {
                continue;
            }

            final String romanNumber = splitLine[1];

            if (!RomanUtil.map.containsKey(romanNumber)) {
                continue;
            }

            customEnchants.put(customEnchant, RomanUtil.map.get(romanNumber));
        }

        return customEnchants;
    }

    private boolean isNetherCitadel(Player player) {
        return false;
    }
}
