package net.frozenorb.foxtrot.gameplay.loot.battlepass.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.BattlePassHandler;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.BattlePassProgress;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.menu.ChallengesMenu;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.reward.Reward;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.tier.Tier;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.Formats;
import org.apache.commons.lang.math.IntRange;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@RequiredArgsConstructor
public class BattlePassMenu extends Menu {

    private static BattlePassHandler handler = Foxtrot.getInstance().getBattlePassHandler();

    private final BattlePassProgress progress;
    private int page = 1;

    public BattlePassMenu(BattlePassProgress progress, int page) {
        this.progress = progress;
        this.page = page;
    }

    @Override
    public String getTitle(Player player) {
        return "BattlePass";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(2, new GoToChallengesButton(false));
        buttons.put(4, new InfoButton());
        buttons.put(6, new GoToChallengesButton(true));

        IntRange range;
        if (page == 1) {
            range = new IntRange(1, 7);
        } else {
            range = new IntRange(((page - 1) * 7) + 1, page * 7);
        }

        int slotOffset = 0;
        for (int tierNumber : range.toArray()) {
            Tier tier = handler.getTier(tierNumber);
            if (tier != null) {
                buttons.put(28 + slotOffset - 9, tier.getPremiumReward() != null ? new RewardButton(tier, tier.getPremiumReward()) : new EmptyRewardButton(tier));
                buttons.put(28 + slotOffset++, new TierButton(tier));
                buttons.put(28 + slotOffset + 8, tier.getFreeReward() != null ? new RewardButton(tier, tier.getFreeReward()) : new EmptyRewardButton(tier));
            }
        }

        buttons.put(27, new PreviousPageButton());
        buttons.put(35, new NextPageButton());

        for (int i = 0; i < 54; i++) {
            if (!buttons.containsKey(i)) {
                buttons.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 15, " "));
            }
        }

        return buttons;
    }

    @Override
    public boolean isAutoUpdate() {
        return true;
    }

    @Override
    public boolean isUpdateAfterClick() {
        return true;
    }

    private class InfoButton extends Button {
        @Override
        public String getName(Player player) {
            return CC.GOLD + CC.BOLD + "BattlePass";
        }

        @Override
        public List<String> getDescription(Player player) {
            return Arrays.asList(
                    CC.GRAY + "Current Tier: " + CC.GOLD + progress.getCurrentTier().getNumber(),
                    CC.GRAY + "Current XP: " + CC.GOLD + Formats.formatNumber(progress.getExperience())
            );
        }

        @Override
        public Material getMaterial(Player player) {
            return Material.NETHER_STAR;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            ItemStack itemStack = super.getButtonItem(player);
            itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
            return itemStack;
        }
    }

    @AllArgsConstructor
    private class GoToChallengesButton extends Button {
        private boolean daily;

        @Override
        public String getName(Player player) {
            if (daily) {
                return CC.GOLD + CC.BOLD + "Daily Challenges";
            } else {
                return CC.GOLD + CC.BOLD + "Premium Challenges";
            }
        }

        @Override
        public List<String> getDescription(Player player) {
            if (!daily && !progress.isPremium()) {
                return Formats.renderLines(CC.RED, "You don't have access to the Premium BattlePass challenges! Purchase on our store at store.cavepvp.org.");
            } else {
                return Collections.emptyList();
            }
        }

        @Override
        public Material getMaterial(Player player) {
            if (daily) {
                return Material.GOLD_INGOT;
            } else {
                return Material.DIAMOND;
            }
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            if (!daily && !progress.isPremium()) {
                player.sendMessage(CC.RED + "You don't have access to the Premium BattlePass challenges! Purchase on our store at store.cavepvp.org.");
                return;
            }

            new ChallengesMenu(daily, progress).openMenu(player);
        }
    }

    @AllArgsConstructor
    private class TierButton extends Button {
        private Tier tier;

        @Override
        public String getName(Player player) {
            return CC.GOLD + CC.BOLD + "Tier " + tier.getNumber();
        }

        @Override
        public List<String> getDescription(Player player) {
            return Collections.singletonList(Formats.formatExperience(Math.min(progress.getExperience(), tier.getRequiredExperience())) + CC.GRAY + "/" + Formats.formatExperience(tier.getRequiredExperience()));
        }

        @Override
        public Material getMaterial(Player player) {
            return Material.STAINED_GLASS_PANE;
        }

        @Override
        public byte getDamageValue(Player player) {
            if (progress.getExperience() >= tier.getRequiredExperience()) {
                return 5;
            } else if ((tier.getNumber() == 1 && progress.getExperience() < tier.getRequiredExperience()) || progress.getCurrentTier().getNumber() == tier.getNumber()) {
                return 4;
            } else {
                return 14;
            }
        }
    }

    @AllArgsConstructor
    private class RewardButton extends Button {
        private Tier tier;
        private Reward reward;

        @Override
        public String getName(Player player) {
            if (progress.getExperience() >= tier.getRequiredExperience()) {
                if (reward.hasClaimed(progress)) {
                    return CC.GREEN + CC.BOLD + "Claimed Reward";
                } else {
                    if (!reward.isFreeReward() && !progress.isPremium()) {
                        return CC.RED + CC.BOLD + "Reward Locked";
                    } else {
                        return CC.YELLOW + CC.BOLD + "Claim Reward";
                    }
                }
            } else {
                return CC.RED + CC.BOLD + "Reward Locked";
            }
        }

        @Override
        public List<String> getDescription(Player player) {
            List<String> description = new ArrayList<>();

            for (String line : reward.getText()) {
                description.add(CC.GRAY + "● " + line);
            }

            if (!reward.isFreeReward() && !progress.isPremium()) {
                description.add("");
                description.addAll(Formats.renderLines(CC.RED, "You don't have access to the Premium BattlePass rewards! Purchase on our store at store.cavepvp.org."));
            }

            return description;
        }

        @Override
        public Material getMaterial(Player player) {
            if (progress.getExperience() >= tier.getRequiredExperience()) {
                if (reward.hasClaimed(progress)) {
                    return Material.MINECART;
                } else {
                    if (!reward.isFreeReward() && !progress.isPremium()) {
                        return Material.HOPPER_MINECART;
                    } else {
                        return Material.STORAGE_MINECART;
                    }
                }
            } else {
                return Material.HOPPER_MINECART;
            }
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            if ((tier.getNumber() == 1 && progress.getExperience() < tier.getRequiredExperience()) || tier.getRequiredExperience() < tier.getRequiredExperience()) {
                return;
            }

            if (progress.getExperience() >= tier.getRequiredExperience()) {
                if (reward.isFreeReward() && !progress.getClaimedRewardsFree().contains(tier)) {
                    progress.getClaimedRewardsFree().add(tier);
                    progress.requiresSave();

                    reward.execute(player);
                } else if (progress.isPremium() && !progress.getClaimedRewardsPremium().contains(tier)) {
                    progress.getClaimedRewardsPremium().add(tier);
                    progress.requiresSave();

                    reward.execute(player);
                }
            }
        }
    }

    @AllArgsConstructor
    private class EmptyRewardButton extends Button {
        private Tier tier;

        @Override
        public String getName(Player player) {
            return CC.GOLD + CC.BOLD + "Tier " + tier.getNumber();
        }

        @Override
        public List<String> getDescription(Player player) {
            return Collections.emptyList();
        }

        @Override
        public Material getMaterial(Player player) {
            return Material.MINECART;
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
                final BattlePassMenu menu = new BattlePassMenu(progress, page);

                player.closeInventory();

                Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> menu.openMenu(player), 1);
            }
        }
    }

    private class NextPageButton extends Button {
        @Override
        public String getName(Player player) {
            if (page < 4) {
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
            if (clickType.isLeftClick() && page < 4) {
                page += 1;
                final BattlePassMenu menu = new BattlePassMenu(progress, page);

                player.closeInventory();

                Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> menu.openMenu(player), 1);
            }
        }
    }

}
