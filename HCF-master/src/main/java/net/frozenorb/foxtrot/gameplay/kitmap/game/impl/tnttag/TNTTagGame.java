package net.frozenorb.foxtrot.gameplay.kitmap.game.impl.tnttag;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.util.ColorUtil;
import cc.fyre.proton.util.ItemBuilder;
import lombok.Getter;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.game.Game;
import net.frozenorb.foxtrot.gameplay.kitmap.game.GameState;
import net.frozenorb.foxtrot.gameplay.kitmap.game.GameType;
import net.frozenorb.foxtrot.gameplay.kitmap.game.arena.GameArena;
import net.frozenorb.foxtrot.server.voucher.VoucherCommand;
import net.frozenorb.foxtrot.util.InventoryUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class TNTTagGame extends Game {

    @Getter private boolean started = false;
    @Getter private List<UUID> tagged = new ArrayList<>();
    @Getter private int startingSeconds = 60;
    @Getter private int nextExplosion = 0;
    @Getter private int round = 0;

    public TNTTagGame(UUID host, List<GameArena> arenaOptions) {
        super(host, GameType.TNT_TAG, arenaOptions);
    }

    @Override
    public void startGame() {
        super.startGame();

        started = false;

        // just in case whoever made the arena forgets to set bounds, null check so no npe
        if (getVotedArena().getBounds() != null) {
            getVotedArena().createSnapshot();
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (state == GameState.ENDED) {
                    cancel();
                    return;
                }

                if (state == GameState.RUNNING) {
                    startTNTTag();
                    cancel();
                }
            }
        }.runTaskTimer(Foxtrot.getInstance(), 10L, 10L);
    }

    public List<Player> findTaggedPlayers() {
        return this.tagged.stream().filter(it -> Foxtrot.getInstance().getServer().getPlayer(it) != null).map(it -> Foxtrot.getInstance().getServer().getPlayer(it)).collect(Collectors.toList());
    }

    @Override
    public void endGame() {
        super.endGame();

        if (getVotedArena().getBounds() != null) {
            Bukkit.getScheduler().runTaskLater(Foxtrot.getInstance(), getVotedArena()::restoreSnapshot, 5 * 20L);
        }
    }

    public List<Player> selectTagged() {
        final List<Player> nonTagged = new ArrayList<>(getPlayers());

        for (int i = 0; i < Math.ceil(this.getPlayers().size()) / 5; i++) {
            final Player player = nonTagged.get(ThreadLocalRandom.current().nextInt(nonTagged.size()));

            if (this.tagged.contains(player.getUniqueId())) {
                i--;
                continue;
            }

            nonTagged.remove(player);

            setTagged(player, true);
        }

        final StringBuilder stringBuilder = new StringBuilder();

        for (Player onlinePlayer : findTaggedPlayers()) {
            stringBuilder.append(stringBuilder.length() == 0 ? "" : ChatColor.WHITE + ", ").append(Neutron.getInstance().getProfileHandler().findDisplayName(onlinePlayer.getUniqueId()));
        }

        sendMessages("", "&4&lTNT Tag", "&7A new round has started!", stringBuilder.toString() + " &f" + (findTaggedPlayers().size() > 1 ? "are" : "is") + " now &cit&f!", "");

        for (Player player : nonTagged) {
            setTagged(player, false);
        }

        return this.findTaggedPlayers();
    }

    public void setTagged(Player player, boolean state) {

        if (!state) {
            this.tagged.remove(player.getUniqueId());
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            player.updateInventory();
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1), true);
            return;
        }

        this.tagged.add(player.getUniqueId());

        player.getInventory().clear();
        player.getEquipment().setHelmet(new ItemStack(Material.TNT));
        player.getEquipment().setChestplate(ItemBuilder.of(Material.LEATHER_CHESTPLATE).color(ColorUtil.COLOR_MAP.get(ChatColor.RED).getColor()).build());
        player.getEquipment().setLeggings(ItemBuilder.of(Material.LEATHER_LEGGINGS).color(ColorUtil.COLOR_MAP.get(ChatColor.RED).getColor()).build());
        player.getEquipment().setBoots(ItemBuilder.of(Material.LEATHER_BOOTS).color(ColorUtil.COLOR_MAP.get(ChatColor.RED).getColor()).build());
        player.removePotionEffect(PotionEffectType.SPEED);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 3));
        player.updateInventory();

        for (int i = 0; i < 9; i++) {
            player.getInventory().setItem(i, new ItemStack(Material.TNT));
        }

        player.getInventory().setItem(4, new ItemStack(Material.BLAZE_ROD));
        player.updateInventory();

        VoucherCommand.spawnFireworks(player.getLocation(), 1, 1, Color.RED, FireworkEffect.Type.STAR);
    }

    private void startTNTTag() {
        this.started = false;
        this.round = 1;

        for (Player player : this.getPlayers()) {
            InventoryUtils.resetInventoryNow(player);
            player.teleport(this.getVotedArena().getPointA());
        }

        this.setStartedAt(System.currentTimeMillis());

        new BukkitRunnable() {
            private int i = 6;

            @Override
            public void run() {
                if (state == GameState.ENDED || started) {
                    cancel();
                    return;
                }

                i--;

                sendSound(Sound.NOTE_PLING, 1F, i == 0 ? 2F : 1F);
                started = i == 0;
                startingSeconds = 60;
                nextExplosion = 60;

                if (i == 0) {
                    sendMessages(ChatColor.GREEN + "TNT Tag event has begun!");
                    selectTagged();
                } else {
                    sendMessages("&6&lTNT Tag &eevent begins in &f" + i + " second" + (i == 1 ? "" : "s") + "&e...");
                }
            }
        }.runTaskTimer(Foxtrot.getInstance(), 20L, 20L);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!started) {
                    return;
                }

                if (state == GameState.ENDED) {
                    cancel();
                    return;
                }

                if (getPlayers().size() < 2) {
                    endGame();
                    cancel();
                    return;
                }

                if (tagged.isEmpty()) {
                    selectTagged();
                }

                nextExplosion--;

                if (nextExplosion > 0 && nextExplosion <= 30 && (nextExplosion % 10 == 0 || nextExplosion <= 3)) {
                    sendMessages("&cAll tagged players will explode in &f" + nextExplosion + " seconds&c.");
                }

                if (nextExplosion > 0) {
                    return;
                }

                for (Player taggedPlayer : findTaggedPlayers()) {
                    taggedPlayer.getLocation().getWorld().createExplosion(taggedPlayer.getLocation().getBlockX(), taggedPlayer.getLocation().getBlockY(), taggedPlayer.getLocation().getBlockZ(), 2.0F, false, false);
                    eliminatePlayer(taggedPlayer, null);
                }

                selectTagged();

                round += 1;
                startingSeconds = startingSeconds-5;
                nextExplosion = startingSeconds;

                if (startingSeconds < 20) {
                    startingSeconds = 20;
                    nextExplosion = 20;
                }

                if (getPlayers().size() > 8) {
                    return;
                }

                for (Player player : getPlayers()) {
                    player.teleport(getVotedArena().getPointA());
                }
            }
        }.runTaskTimer(Foxtrot.getInstance(), 20, 20);
    }

    @Override
    public void eliminatePlayer(Player player, Player killer) {
        tagged.remove(player.getUniqueId());

        super.eliminatePlayer(player, killer);
        addSpectator(player);

        if (players.size() == 1) {
            endGame();
        }
    }

    public double getDeathHeight() {
        return Math.min(getVotedArena().getPointA().getBlockY(), getVotedArena().getPointB().getBlockY()) - 2.9;
    }

    @Override
    public Player findWinningPlayer() {
        if (players.size() == 1) {
            return Bukkit.getPlayer(players.iterator().next());
        }

        return null;
    }

    @Override
    public void getScoreboardLines(Player player, LinkedList<String> lines) {
        lines.add("&6&l" + getGameType().getDisplayName() + ":");

        if (state == GameState.WAITING) {
            lines.add(" &6Players: &f" + players.size() + "&7/&f" + getMaxPlayers());

            if (getVotedArena() != null) {
                lines.add(" &6Map: &f" + getVotedArena().getName());
            } else {
                lines.add("");
                lines.add(" &6Map Vote");

                getArenaOptions().entrySet().stream().sorted((o1, o2) -> o2.getValue().get()).forEach(entry -> lines.add("&7» " + (getPlayerVotes().getOrDefault(player.getUniqueId(), null) == entry.getKey() ? "&l" : "") + entry.getKey().getName() + " &7(" + entry.getValue().get() + ")"));
            }

            if (getStartedAt() == null) {
                int playersNeeded = getGameType().getMinPlayers() - getPlayers().size();
                lines.add("");
                lines.add("&cWaiting for " + playersNeeded + " player" + (playersNeeded == 1 ? "" : "s"));
            } else {
                float remainingSeconds = (getStartedAt() - System.currentTimeMillis()) / 1000F;
                lines.add("&aStarting in " + ((double) Math.round(10.0D * (double) remainingSeconds) / 10.0D) + "s");
            }
            return;
        }

        if (state == GameState.RUNNING) {
            lines.add(" &6Remaining: &f" + players.size() + "&7/&f" + getStartedWith());
            lines.add(" &6Round: &f" + this.round);
            lines.add("");
            lines.add(" &6Explosion: &f" + this.nextExplosion + "s");
            lines.add(" &6Tagged Players: &f" + tagged.size());
        } else {
            if (winningPlayer == null) {
                lines.add(" &6Winner: &fNone");
            } else {
                lines.add(" &6Winner: &f" + winningPlayer.getName());
            }
        }
    }

    @Override
    public List<FancyMessage> createHostNotification() {
        return Arrays.asList(
                new FancyMessage("███████").color(ChatColor.GRAY),
                new FancyMessage("")
                        .then("█").color(ChatColor.GRAY)
                        .then("█████").color(ChatColor.DARK_RED)
                        .then("█").color(ChatColor.GRAY)
                        .then(" " + getGameType().getDisplayName() + " Event").color(ChatColor.DARK_RED).style(ChatColor.BOLD),
                new FancyMessage("")
                        .then("███").color(ChatColor.GRAY)
                        .then("█").color(ChatColor.DARK_RED)
                        .then("███").color(ChatColor.GRAY),
                new FancyMessage("")
                        .then("███").color(ChatColor.GRAY)
                        .then("█").color(ChatColor.DARK_RED)
                        .then("███").color(ChatColor.GRAY)
                        .then(ChatColor.translate(" &7Players: &f" + this.getPlayers().size() + "/" + this.getMaxPlayers())),
                new FancyMessage("")
                        .then("███").color(ChatColor.GRAY)
                        .then("█").color(ChatColor.DARK_RED)
                        .then("███").color(ChatColor.GRAY)
                        .then(ChatColor.translate(" &7Hosted By: &f" + Neutron.getInstance().getProfileHandler().findDisplayName(this.getHost()))),
                new FancyMessage("")
                        .then("███").color(ChatColor.GRAY)
                        .then("█").color(ChatColor.DARK_RED)
                        .then("███").color(ChatColor.GRAY)
                        .then(" Click to join the event").color(ChatColor.GREEN)
                        .command("/game join")
                        .formattedTooltip(new FancyMessage("Click here to join the event").color(ChatColor.YELLOW)),
                new FancyMessage("███████").color(ChatColor.GRAY)
        );
    }
}
