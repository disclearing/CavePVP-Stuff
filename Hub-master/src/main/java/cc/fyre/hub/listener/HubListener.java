package cc.fyre.hub.listener;

import cc.fyre.hub.Hub;
import cc.fyre.hub.util.HubItem;
import cc.fyre.hub.util.NMSUtil;
import cc.fyre.hub.util.UtilParticle;
import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.NeutronConstants;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.profile.attributes.grant.Grant;
import cc.fyre.piston.Piston;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.util.ItemBuilder;
import cc.fyre.proton.util.PlayerUtils;
import cc.fyre.proton.uuid.UUIDCache;
import cc.fyre.universe.util.BungeeUtil;
import net.minecraft.server.v1_8_R3.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.UUID;

public class HubListener implements Listener {
    @EventHandler(priority = EventPriority.NORMAL)
    private void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        PlayerUtils.resetInventory(player, GameMode.ADVENTURE);

        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 3));
        player.teleport(Hub.getInstance().getSpawnPoint());

        Hub.getInstance().getJoinMessage().forEach(player::sendMessage);
        Hub.getInstance().getItems().forEach((key, value) -> player.getInventory().setItem(key, value.getItemStack()));

        player.updateInventory();

        event.setJoinMessage(null);

        final Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(player.getUniqueId());

        if (!profile.getActiveRank().getName().equalsIgnoreCase("Holiday")) {
            return;
        }

        final Grant activeGrant = profile.getActiveGrant();

        if (activeGrant.getExecutedReason().contains("Transfer from Valor")) {
            activeGrant.setPardoner(UUIDCache.CONSOLE_UUID);
            activeGrant.setPardonedAt(System.currentTimeMillis());
            activeGrant.setPardonedReason("No scoped ranks on Neutron.");

            profile.recalculateGrants();
            profile.save();
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onPortal(PlayerMoveEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final Player player = event.getPlayer();
        final Material to = event.getTo().getBlock().getType();

        if (to.equals(Material.PORTAL)) {
            Hub.getInstance().getServer().getScheduler().runTaskLater(Hub.getInstance(), () -> Hub.getInstance().getMenus().get("main").openMenu(player), 15);
            player.teleport(Hub.getInstance().getSpawnPoint());
        }

    }

    @EventHandler(priority = EventPriority.LOW)
    private void onFireSpread(BlockSpreadEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onPhysics(BlockPhysicsEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onBurn(BlockBurnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onIgnite(BlockIgniteEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onEvent(BlockDamageEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onForm(EntityBlockFormEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onBlockFromTo(BlockFromToEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onGrow(BlockGrowEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();

        if (event.getAction() == Action.PHYSICAL) {
            player.setVelocity(new org.bukkit.util.Vector(-4,2.2,0));
            Hub.getInstance().getServer().getScheduler().runTaskLater(Hub.getInstance(), () -> {
                player.setVelocity(new Vector(-4,0,0));
                player.playSound(player.getLocation(),Sound.EXPLODE,1.0F,2.0F);
                player.playSound(player.getLocation(),Sound.WITHER_SHOOT,1.0F,2.0F);
                player.getWorld().playEffect(player.getLocation(), Effect.MOBSPAWNER_FLAMES,20);
            }, 10);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onForm(BlockFormEvent event) {
        if (event.getNewState().getType() == Material.FIRE) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    private void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    private void onEntityDamage(EntityDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.VOID && event.getEntity() instanceof Player) {
            final Player player = (Player) event.getEntity();

            player.teleport(Hub.getInstance().getSpawnPoint());
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onBlockBreak(BlockBreakEvent event) {

        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onBlockPlace(BlockPlaceEvent event) {

        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    private void onInteract(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Player)) {
            return;
        }

        final Player player = event.getPlayer();

        if (player.getItemInHand() != null && player.getItemInHand().getType() != Material.AIR) {
            return;
        }

        final Player target = (Player) event.getRightClicked();

        player.chat("/profiles check " + target.getName());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    private void onPlayerInteract(PlayerInteractEvent event) {

        if (!event.hasItem()) {
            return;
        }

        if (!event.getAction().name().contains("RIGHT")) {
            return;
        }

        int heldItemSlot = event.getPlayer().getInventory().getHeldItemSlot();

        if (heldItemSlot == 4) {
            event.getPlayer().chat("/myprofile");
            return;
        }

        final HubItem hubItem = Hub.getInstance().getItems().get(heldItemSlot);

        if (hubItem == null) {
            return;
        }

        if (hubItem.getItemStack().getType() == Material.ENDER_CHEST) {
            event.getPlayer().chat("/cosmetics");
            return;
        }

        if (!hubItem.hasMenu()) {
            return;
        }

        hubItem.getMenu().openMenu(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    private void onInventoryClick(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        if (event.getWhoClicked().getGameMode() == GameMode.CREATIVE) {
            return;
        }

        final Menu menu = Menu.getCurrentlyOpenedMenus().get(event.getWhoClicked().getUniqueId());

        if (menu != null && !Hub.getInstance().getMenus().containsValue(menu)) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onDecay(LeavesDecayEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onAsyncChat(AsyncPlayerChatEvent event) {

        if (event.isCancelled()) {
            return;
        }

        event.setCancelled(true);

        for (Player loopPlayer : Hub.getInstance().getServer().getOnlinePlayers()) {
            loopPlayer.sendMessage(NeutronConstants.formatChatDisplay(event.getPlayer(), event.getMessage()));
        }

    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onDropItem(PlayerDropItemEvent event) {

        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onWeatherChange(WeatherChangeEvent event) {
        event.getWorld().setTime(6000);
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onBlockForm(BlockFormEvent event) {
        event.setCancelled(true);
    }

    public ItemStack getBook() {
        final ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        final BookMeta bookMeta = (BookMeta) book.getItemMeta();
        bookMeta.addPage(ChatColor.translate(
                "&cHosting a &lHUGE &cgiveaway in our Discord! &aOver 11 prizes&c! Join now!                       &rhttps://discord.gg/cavepvp"
        ));
        bookMeta.setAuthor("SimplyTrash");
        bookMeta.setTitle("Discord");
        book.setItemMeta(bookMeta);
        return book.clone();
    }

}
