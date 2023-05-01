package net.frozenorb.foxtrot.gameplay.ability;

import cc.fyre.piston.util.Cooldown;
import cc.fyre.proton.Proton;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.ClassUtils;
import com.comphenix.protocol.ProtocolLibrary;
import net.frozenorb.foxtrot.team.Team;
import net.minecraft.util.com.google.common.collect.HashBasedTable;
import net.minecraft.util.com.google.common.collect.Table;
import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.cooldown.LCCooldown;
import com.lunarclient.bukkitapi.nethandler.client.LCPacketCooldown;
import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.listener.AbilityListener;
import net.frozenorb.foxtrot.gameplay.ability.menu.TradeMenu;
import net.frozenorb.foxtrot.gameplay.ability.packet.InvisibilityPacketAdapter;
import net.frozenorb.foxtrot.gameplay.ability.parameter.AbilityParameter;
import net.frozenorb.foxtrot.gameplay.armorclass.ArmorClass;
import net.frozenorb.foxtrot.gameplay.events.mini.MiniEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.cavepvp.suge.Suge;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("UnstableApiUsage")
public class AbilityHandler {

    @Getter
    private final Map<Ability, Integer> usedItems = new HashMap<>();
    @Getter
    private final Map<String, Ability> abilities = new HashMap<>();
    @Getter
    private final Table<UUID, String, Long> lastUsedItem = HashBasedTable.create();

    @Getter private final Map<UUID, Long> globalCooldowns = new HashMap<>();

    @Getter private final List<Ability> tradeAbleItems = new ArrayList<>();

    @Getter
    public static final Table<UUID, Ability, Cooldown> cooldown = HashBasedTable.create();

    private File file;
    private FileConfiguration data;

    public AbilityHandler(Foxtrot instance) {
        for (Class<?> clazz : ClassUtils.getClassesInPackage(Foxtrot.getInstance(), "net.frozenorb.foxtrot.gameplay.ability.type")) {

            if (!Ability.class.isAssignableFrom(clazz)) {
                continue;
            }

            try {
                final Ability ability = (Ability) clazz.newInstance();

                this.abilities.put(ability.getName(), ability);
            } catch (InstantiationException | IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }

        instance.getServer().getPluginManager().registerEvents(new AbilityListener(Foxtrot.getInstance(), this), instance);
        Proton.getInstance().getCommandHandler().registerParameterType(Ability.class, new AbilityParameter());
        ProtocolLibrary.getProtocolManager().addPacketListener(new InvisibilityPacketAdapter());
        instance.getServer().getScheduler().runTaskLater(instance, this::loadStatistics, 20);
    }

    public void loadStatistics() {
        this.file = new File(Foxtrot.getInstance().getDataFolder(), "data/ability-stats.yml");
        this.data = YamlConfiguration.loadConfiguration(this.file);

        if (!this.file.exists()) {
            try {
                this.file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        if (this.data.get("abilities") == null) {
            return;
        }

        for (String key : data.getConfigurationSection("abilities").getKeys(false)) {
            final Ability ability = this.fromName(key);

            if (ability == null) {
                continue;
            }

            this.usedItems.put(ability, data.getInt("abilities." + key));
        }
    }

    public void saveStatistics() {
        Map<String, Object> configValues = this.data.getValues(false);
        for (Map.Entry<String, Object> entry : configValues.entrySet())
            this.data.set(entry.getKey(), null);

        for (Map.Entry<Ability, Integer> abilityIntegerEntry : this.usedItems.entrySet()) {
            this.data.set("abilities." + abilityIntegerEntry.getKey().getName(), +abilityIntegerEntry.getValue());
        }

        try {
            this.data.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Ability fromName(String name) {
        return this.abilities.values().stream().filter(ability -> ability.getName().equalsIgnoreCase(name)).findAny().orElse(null);
    }

    @Command(
            names = {"ability"},
            permission = "foxtrot.command.ability"
    )
    public static void execute(CommandSender sender,
                               @Parameter(name = "ability") Ability ability,
                               @Parameter(name = "amount", defaultValue = "1") int amount,
                               @Parameter(name = "player", defaultValue = "self") Player target) {
        final ItemStack itemStack = ability.hassanStack.clone();

        if (ability.getName().equalsIgnoreCase("Backpack")) {

            for (int i = 0; i < amount; i++) {
                final ItemStack theStack = ability.hassanStack.clone();
                final ItemMeta itemMeta = theStack.getItemMeta();
                final List<String> lore = itemMeta.getLore();
                lore.add(ChatColor.GRAY + "Identifier: " + ChatColor.WHITE + UUID.randomUUID());
                itemMeta.setLore(lore);
                theStack.setItemMeta(itemMeta);
                target.getInventory().addItem(theStack.clone());
            }
            return;
        }

        itemStack.setAmount(amount);
        target.getInventory().addItem(itemStack);

    }

    @Command(names = {"ability reset"}, permission = "foxtrot.command.ability.reset")
    public static void reset(Player player, @Parameter(name = "ability") Ability ability) {
        if (!getCooldown().contains(player.getUniqueId(), ability)) {
            player.sendMessage(ChatColor.RED + "You don't have a cooldown for " + ability.getDisplayName() + ChatColor.RED + ".");
            return;
        }

        getCooldown().remove(player.getUniqueId(), ability);

        player.sendMessage(ChatColor.RED + "Removed cooldown for the " + ability.getDisplayName() + ChatColor.RED + ".");
    }

    public void applyCooldown(Ability ability, Player player) {
        applyCooldown(ability, player, ability.getCooldown());
    }

    public void applyCooldown(Ability ability, Player player, long cooldownTime) {
        this.usedItems.putIfAbsent(ability, 0);
        this.usedItems.replace(ability, this.usedItems.get(ability) + 1);

        if (ability.getCooldown() <= 0) {
            return;
        }

        final Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);
        final ArmorClass armorClass = Foxtrot.getInstance().getArmorClassHandler().findWearing(player);

        if (Foxtrot.getInstance().getMiniEventsHandler() != null && Foxtrot.getInstance().getMiniEventsHandler().getActiveEvent() != null) {
            final MiniEvent miniEvent = Foxtrot.getInstance().getMiniEventsHandler().getActiveEvent();

            cooldownTime = miniEvent.getEventID().equalsIgnoreCase("Rage") ? cooldownTime/2 : cooldownTime;
        } else if (armorClass != null && armorClass.getId().equalsIgnoreCase("Raider")) {
            cooldownTime *= 0.85;
        } else if (Foxtrot.getInstance().getNetworkBoosterHandler() != null && Foxtrot.getInstance().getNetworkBoosterHandler().isFrenzy()) {
            cooldownTime *= 0.75;
        }

        if (team != null && team.hasRoadOutpost()) {
            cooldownTime *= 0.75;
        }

        if (player.hasMetadata("NO_COOLDOWN")) {
            cooldownTime = 0;
        }

        LunarClientAPI.getInstance().sendPacket(player, new LCPacketCooldown(ability.getName(), cooldownTime, ability.getMaterial().getId()));;

        cooldown.put(player.getUniqueId(), ability, new Cooldown(cooldownTime));
    }

    @Command(names = {"tradeitem"}, permission = "")
    public static void tradeItem(Player player) {
        final ItemStack itemStack = player.getItemInHand().clone();

        if (itemStack == null || itemStack.getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "You don't have an item in your hand!");
            return;
        }

        final Ability ability = Foxtrot.getInstance().getMapHandler().getAbilityHandler().getAbilities().values().stream().filter(it -> it.isSimilar(itemStack)).findFirst().orElse(null);

        if (ability == null) {
            player.sendMessage(ChatColor.RED + "You must have an ability item in your hand!");
            return;
        }

        if (!Foxtrot.getInstance().getMapHandler().getAbilityHandler().getTradeAbleItems().contains(ability)) {
            player.sendMessage(ChatColor.RED + "This ability item may not be traded!");
            return;
        }

        new TradeMenu(itemStack, ability).openMenu(player);
    }

    public boolean hasCooldown(Ability ability, Player player) {
        if (!cooldown.contains(player.getUniqueId(), ability)) {
            return false;
        }

        return !cooldown.get(player.getUniqueId(), ability).hasExpired();
    }

    public long getRemaining(Ability ability, Player player) {
        if (!cooldown.contains(player.getUniqueId(), ability)) {
            return 0L;
        }

        return cooldown.get(player.getUniqueId(), ability).getRemaining();
    }
}
