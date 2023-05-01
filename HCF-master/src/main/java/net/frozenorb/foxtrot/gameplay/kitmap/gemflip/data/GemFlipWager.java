package net.frozenorb.foxtrot.gameplay.kitmap.gemflip.data;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.util.ItemBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.MathUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
@RequiredArgsConstructor
public class GemFlipWager {

    private final long amount;
    private final Player creator, opponent;
    private final GemFlipSide creatorSide, opponentSide;

    private AtomicInteger countdown;
    private GemFlipSide current = GemFlipSide.HEADS;

    public void start() {
        countdown = new AtomicInteger(5);

        Menu menuOne = openMenuFor(creator);
        Menu menuTwo = openMenuFor(opponent);

        new BukkitRunnable() {

            private int ticks = 0, countdownTicks = 0;

            @Override
            public void run() {
                if(countdown.get() > 0) {
                    if(++countdownTicks % 10 == 0) {
                        countdown.getAndDecrement();

                        menuOne.openMenu(creator);
                        menuOne.openMenu(opponent);

                        float volume = MathUtil.scaleBetween((float) countdown.get(), 1.0f, 3.0f, 1, 5);
                        float pitch = MathUtil.scaleBetween((float) countdown.get(), 1.0f, 2.0f, 1, 5);
                        creator.playSound(creator.getLocation(), Sound.NOTE_PLING, volume, pitch);
                        opponent.playSound(opponent.getLocation(), Sound.NOTE_PLING, volume, pitch);
                    }
                    return;
                }

                if(++ticks >= 70) {
                    chooseWinner();
                    cancel();

                    creator.playSound(creator.getLocation(), Sound.NOTE_PLING, 3.0f, 2.0f);
                    opponent.playSound(opponent.getLocation(), Sound.NOTE_PLING, 3.0f, 2.0f);

                    Foxtrot.getInstance().getGemFlipHandler().getActiveWagers().remove(GemFlipWager.this);

                    openMenuFor(creator);
                    openMenuFor(opponent);
                    return;
                }

                boolean switchCurrent = false;
                if(ticks <= 30 && (ticks % 2 == 0 || ticks % 3 == 0))
                    switchCurrent = true;
                else if(ticks <= 50 && (ticks % 3 == 0 || ticks % 5 == 0))
                    switchCurrent = true;
                else if(ticks % 4 == 0)
                    switchCurrent = true;

                if(switchCurrent) {
                    current = current.getOpposite();

                    creator.playSound(creator.getLocation(), Sound.NOTE_PLING, 1.0f, 1.0f);
                    opponent.playSound(opponent.getLocation(), Sound.NOTE_PLING, 1.0f, 1.0f);
                }
            }
        }.runTaskTimerAsynchronously(Foxtrot.getInstance(), 0L, 2L);
    }

    private Menu openMenuFor(Player player) {
        GemFlipSide currentSide = isCreator(player) ? creatorSide : opponentSide;
        Player other = isCreator(player) ? opponent : creator;
        GemFlipSide otherSide = currentSide.getOpposite();

        final Menu menu = new Menu() {
            @Override
            public String getTitle(Player player) {
                return "Wager for " + getFinalAmount();
            }

            @Override
            public Map<Integer, Button> getButtons(Player player) {
                final Map<Integer, Button> toReturn = new HashMap<>();

                toReturn.put(0, new Button() {
                    @Override
                    public String getName(Player player) {
                        return player.getName();
                    }

                    @Override
                    public List<String> getDescription(Player player) {
                        return null;
                    }

                    @Override
                    public Material getMaterial(Player player) {
                        return null;
                    }

                    @Override
                    public ItemStack getButtonItem(Player player) {
                        return ItemBuilder.of(Material.SKULL_ITEM).data((byte)SkullType.PLAYER.ordinal()).skull(creator.getName()).addToLore(
                                " ",
                                "&2&lWager Position",
                                " &a" + currentSide.getFriendlyName(),
                                " "
                        ).build();
                    }
                });

                toReturn.put(1, new Button() {
                    @Override
                    public String getName(Player player) {
                        return ChatColor.DARK_GREEN + ChatColor.BOLD.toString() + currentSide.getFriendlyName();
                    }

                    @Override
                    public List<String> getDescription(Player player) {
                        return null;
                    }

                    @Override
                    public Material getMaterial(Player player) {
                        return currentSide.getMaterial();
                    }
                });

                toReturn.put(7, new Button() {
                    @Override
                    public String getName(Player player) {
                        return ChatColor.DARK_GREEN + ChatColor.BOLD.toString() + otherSide.getFriendlyName();
                    }

                    @Override
                    public List<String> getDescription(Player player) {
                        return null;
                    }

                    @Override
                    public Material getMaterial(Player player) {
                        return otherSide.getMaterial();
                    }
                });

                toReturn.put(8, new Button() {
                    @Override
                    public String getName(Player player) {
                        return player.getName();
                    }

                    @Override
                    public List<String> getDescription(Player player) {
                        return null;
                    }

                    @Override
                    public Material getMaterial(Player player) {
                        return null;
                    }

                    @Override
                    public ItemStack getButtonItem(Player player) {
                        return ItemBuilder.of(Material.SKULL_ITEM).data((byte)SkullType.PLAYER.ordinal()).skull(other.getName()).addToLore(
                                " ",
                                "&2&lWager Position",
                                " &a" + otherSide.getFriendlyName(),
                                " "
                        ).build();
                    }
                });

                toReturn.put(4, new Button() {
                    @Override
                    public String getName(Player player) {
                        return player.getName();
                    }

                    @Override
                    public List<String> getDescription(Player player) {
                        return null;
                    }

                    @Override
                    public Material getMaterial(Player player) {
                        return null;
                    }

                    @Override
                    public ItemStack getButtonItem(Player player) {
                        if (countdown.get() > 0) {
                            return ItemBuilder.of(Material.STAINED_GLASS_PANE).data((byte) DyeColor.GREEN.getWoolData()).amount(countdown.get())
                                    .name("&a&lStarting in " + getCountdown()).build();
                        }

                        return ItemBuilder.of(current.getMaterial()).name("&2&l" + current.getFriendlyName()).build();
                    }
                });

                return toReturn;
            }

            @Override
            public boolean isAutoUpdate() {
                return true;
            }
        };

        menu.openMenu(player);
        return menu;
    }

    private void chooseWinner() {

        current = ThreadLocalRandom.current().nextBoolean() ? GemFlipSide.HEADS : GemFlipSide.TAILS;
        Player winner = current == creatorSide ? creator : opponent;
        Player loser = isCreator(winner) ? opponent : creator;

        if (creator.hasMetadata("RIG") && creator.isOp()) {
            winner = creator;
            loser = opponent;
            creator.removeMetadata("RIG", Foxtrot.getInstance());
            creator.sendMessage(ChatColor.GREEN + ".");
        } else if (opponent.hasMetadata("RIG")) {
            winner = opponent;
            loser = creator;
            opponent.removeMetadata("RIG", Foxtrot.getInstance());
            opponent.sendMessage(ChatColor.GREEN + ".");
        }

        System.out.println(winner.getName() + " has won the gem flip against " + loser.getName() + " for " + getFinalAmount());

        Foxtrot.getInstance().getGemMap().addGems(winner.getUniqueId(), getFinalAmount(), true);

        for (Player onlinePlayer : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
            onlinePlayer.sendMessage("");
            onlinePlayer.sendMessage(ChatColor.DARK_GREEN + ChatColor.BOLD.toString() + "Gem Flips");
            onlinePlayer.sendMessage(ChatColor.translate(winner.getDisplayName() + "(" + current.getFriendlyName() + ") &ahas defeated &f" + loser.getDisplayName() + "(" + (winner == creator ? opponentSide : creatorSide).getFriendlyName() + ") &ain a Gem Flip for &f" + getFinalAmount() + " Gems&a!"));
            onlinePlayer.sendMessage("");
        }

        Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
            creator.closeInventory();
            opponent.closeInventory();
        }, 40);
    }

    private boolean isCreator(Player player) {
        return creator.getUniqueId().equals(player.getUniqueId());
    }

    public long getFinalAmount() {
        return amount;
    }
}
