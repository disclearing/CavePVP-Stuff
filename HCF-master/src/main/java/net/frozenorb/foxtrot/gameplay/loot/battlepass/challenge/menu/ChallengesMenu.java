package net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.BattlePassProgress;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.Challenge;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.impl.*;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.impl.visit.VisitActiveKOTHChallenge;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.impl.visit.VisitEndChallenge;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.impl.visit.VisitGlowstoneMountain;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.impl.visit.VisitNetherChallenge;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.menu.BattlePassMenu;
import net.frozenorb.foxtrot.util.CC;
import org.apache.commons.lang.math.IntRange;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ChallengesMenu extends Menu {

    private static int[] SLOTS = new int[] { 19, 20, 21, 22, 23, 24, 25 };

    private int page = 1;

    private final boolean daily;
    private final BattlePassProgress progress;

    @Override
    public String getTitle(Player player) {
        return (daily ? "Daily" : "Premium") + " Challenges";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        Collection<Challenge> challenges;
        if (daily) {
            challenges = Foxtrot.getInstance().getBattlePassHandler().getDailyChallenges().getChallenges().stream().sorted(CHALLENGE_COMPARATOR).collect(Collectors.toList());
        } else {
            challenges = Foxtrot.getInstance().getBattlePassHandler().getChallenges().stream().sorted(CHALLENGE_COMPARATOR).collect(Collectors.toList());
        }

        IntRange range;
        if (page == 1) {
            range = new IntRange(1, 7);
        } else {
            range = new IntRange(((page - 1) * 7) + 1, page * 7);
        }

        int skipped = 0;
        int slotIndex = 0;
        for (Challenge challenge : challenges) {
            if (skipped < range.getMinimumInteger()) {
                skipped++;
                continue;
            }

            buttons.put(SLOTS[slotIndex], new ChallengeButton(challenge));
            buttons.put(SLOTS[slotIndex] + 9, new ChallengeStatusButton(challenge));

            if (slotIndex >= 6) {
                break;
            } else {
                slotIndex++;
            }
        }

        buttons.put(4, new InfoButton());
        buttons.put(49, new BackButton());

        buttons.put(27, new PreviousPageButton());
        buttons.put(35, new NextPageButton());

        for (int i = 0; i < 54; i++) {
            if (!buttons.containsKey(i)) {
                buttons.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 15, " "));
            }
        }

        return buttons;
    }

    private int getMaxPages() {
        Collection<Challenge> challenges;
        if (daily) {
            challenges = Foxtrot.getInstance().getBattlePassHandler().getDailyChallenges().getChallenges();
        } else {
            challenges = Foxtrot.getInstance().getBattlePassHandler().getChallenges();
        }

        if (challenges.size() == 0) {
            return 1;
        } else {
            return (int) Math.ceil(challenges.size() / (double) 7);
        }
    }

    private class InfoButton extends Button {
        @Override
        public String getName(Player player) {
            return CC.GOLD + CC.BOLD + (daily ? "Daily" : "Premium") + " Challenges";
        }

        @Override
        public List<String> getDescription(Player player) {
            return Collections.emptyList();
        }

        @Override
        public Material getMaterial(Player player) {
            if (daily) {
                return Material.GOLD_INGOT;
            } else {
                return Material.DIAMOND;
            }
        }
    }

    @AllArgsConstructor
    private class ChallengeButton extends Button {
        private Challenge challenge;

        @Override
        public String getName(Player player) {
            if (progress.hasCompletedChallenge(challenge)) {
                return CC.GREEN + CC.BOLD + challenge.getName();
            } else if (challenge.hasStarted(player)) {
                return CC.YELLOW + CC.BOLD + challenge.getName();
            } else {
                return CC.RED + CC.BOLD + challenge.getName();
            }
        }

        @Override
        public List<String> getDescription(Player player) {
            return Collections.singletonList(CC.GRAY + challenge.getText());
        }

        @Override
        public Material getMaterial(Player player) {
            return Material.BOOK;
        }
    }

    @AllArgsConstructor
    private class ChallengeStatusButton extends Button {
        private Challenge challenge;

        @Override
        public String getName(Player player) {
            if (progress.hasCompletedChallenge(challenge)) {
                return CC.GREEN + CC.BOLD + "Completed";
            } else if (challenge.hasStarted(player)) {
                return CC.YELLOW + CC.BOLD + "Started";
            } else {
                return CC.RED + CC.BOLD + "Not Started";
            }
        }

        @Override
        public List<String> getDescription(Player player) {
            if (progress.hasCompletedChallenge(challenge)) {
                return Collections.singletonList(CC.GRAY + "You've completed this challenge!");
            } else {
                String progressText = challenge.getProgressText(player);
                if (progressText != null) {
                    return Collections.singletonList(CC.GRAY + progressText);
                } else {
                    return Collections.emptyList();
                }
            }
        }

        @Override
        public Material getMaterial(Player player) {
            return Material.STAINED_GLASS_PANE;
        }

        @Override
        public byte getDamageValue(Player player) {
            if (progress.hasCompletedChallenge(challenge)) {
                return 5;
            } else if (challenge.hasStarted(player)) {
                return 4;
            } else {
                return 14;
            }
        }
    }

    private class BackButton extends Button {
        @Override
        public String getName(Player player) {
            return CC.RED + CC.BOLD + "Go Back";
        }

        @Override
        public List<String> getDescription(Player player) {
            return Collections.emptyList();
        }

        @Override
        public Material getMaterial(Player player) {
            return Material.BED;
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {

            if (progress != null) {
                new BattlePassMenu(progress).openMenu(player);
            } else {
                BattlePassProgress progress = Foxtrot.getInstance().getBattlePassHandler().getProgress(player.getUniqueId());
                new BattlePassMenu(progress).openMenu(player);
            }
        }
    }

    private class PreviousPageButton extends Button {
        @Override
        public String getName(Player player) {
            if (page > 1) {
                return CC.RED + CC.BOLD + "Previous Page";
            } else {
                return CC.GRAY + CC.BOLD + "No Previous Page";
            }
        }

        @Override
        public List<String> getDescription(Player player) {
            return Collections.emptyList();
        }

        @Override
        public Material getMaterial(Player player) {
            return Material.LEVER;
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            if (clickType.isLeftClick() && page > 1) {
                page -= 1;
                openMenu(player);
            }
        }
    }

    private class NextPageButton extends Button {
        @Override
        public String getName(Player player) {
            if (page < getMaxPages()) {
                return CC.RED + CC.BOLD + "Next Page";
            } else {
                return CC.GRAY + CC.BOLD + "No Next Page";
            }
        }

        @Override
        public List<String> getDescription(Player player) {
            return Collections.emptyList();
        }

        @Override
        public Material getMaterial(Player player) {
            return Material.LEVER;
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            if (clickType.isLeftClick() && page < getMaxPages()) {
                page += 1;
                openMenu(player);
            }
        }
    }

    private static Comparator<Challenge> CHALLENGE_COMPARATOR = new Comparator<Challenge>() {
        @Override
        public int compare(Challenge o1, Challenge o2) {
            if (o1.getClass() == o2.getClass()) {
                if (o1 instanceof PlayTimeChallenge) {
                    return (int) ((PlayTimeChallenge) o1).getPlayTime() - (int) ((PlayTimeChallenge) o2).getPlayTime();
                }

                if (o1 instanceof OreChallenge) {
                    return ((OreChallenge) o1).getAmount() - ((OreChallenge) o2).getAmount();
                }

                if (o1 instanceof MineBlockChallenge) {
                    return ((MineBlockChallenge) o1).getAmount() - ((MineBlockChallenge) o2).getAmount();
                }

                if (o1 instanceof KillEntityChallenge) {
                    return ((KillEntityChallenge) o1).getKills() - ((KillEntityChallenge) o2).getKills();
                }

                if (o1 instanceof UsePartnerItemChallenge) {
                    return ((UsePartnerItemChallenge) o1).getUses() - ((UsePartnerItemChallenge) o2).getUses();
                }
            }

            return PRIORITIES.get(o1.getClass()) - PRIORITIES.get(o2.getClass());
        }
    };

    private static Map<Class<?>, Integer> PRIORITIES = new HashMap<>();

    static {
        PRIORITIES.put(ValuablesSoldChallenge.class, 0);
        PRIORITIES.put(MineBlockChallenge.class, 1);
        PRIORITIES.put(KillEntityChallenge.class, 2);
        PRIORITIES.put(PlayTimeChallenge.class, 4);
        PRIORITIES.put(OreChallenge.class, 5);
        PRIORITIES.put(AttemptCaptureKOTHChallenge.class, 6);
        PRIORITIES.put(VisitActiveKOTHChallenge.class, 7);
        PRIORITIES.put(VisitNetherChallenge.class, 8);
        PRIORITIES.put(VisitEndChallenge.class, 9);
        PRIORITIES.put(VisitGlowstoneMountain.class, 10);
        PRIORITIES.put(ArcherTagsChallenge.class, 11);
        PRIORITIES.put(KillstreakChallenge.class, 12);
        PRIORITIES.put(MakeFactionRaidableChallenge.class, 13);
        PRIORITIES.put(UsePartnerItemChallenge.class, 14);
    }

}
