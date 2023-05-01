package net.frozenorb.foxtrot.gameplay.loot.partnercrate;

import cc.fyre.proton.util.ItemBuilder;
import cc.fyre.proton.util.UUIDUtils;
import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.UUID;

public enum PartnerType {
    VIK("Vik", ChatColor.AQUA, 12, "FocusMode"),
    INFORMING("informing", ChatColor.DARK_AQUA, 13, "Invisibility"),
    QNA("QnA", ChatColor.DARK_RED, 14, "CraftingChaos"),
    SAM_HCF("SamHCF", ChatColor.GOLD, 20, "GodMode"),
    LECTORS("Lectors", ChatColor.DARK_PURPLE, 21, "AntiTrap"),
    FLUSHA("FlushaAimLock", ChatColor.LIGHT_PURPLE, 22, "Invisibility"),
    OMARSMOSH("OmarSmosh", ChatColor.LIGHT_PURPLE, 23, "Debuff"),
    STEVVN("stevvn", ChatColor.AQUA, 24, "Debuff"),
    HOUP("Houp", ChatColor.GOLD, 30, "Debuff"),
    NICOARG17("NicoArg17", ChatColor.YELLOW, 31, "Spider"),
    FROZEADO("Frozeado", ChatColor.BLUE, 32, "AntiAbilityBall");

    @Getter @Setter
    public UUID uuid;
    @Getter @Setter
    public String crateName;
    @Getter @Setter
    final ChatColor chatColor;
    @Getter @Setter
    final int slot;
    @Getter @Setter
    final String abilityItem;

    PartnerType(String crateName, ChatColor chatColor, int slot, String abilityItem) {
        this.uuid = UUIDUtils.uuid(crateName);
        this.crateName = crateName;
        this.chatColor = chatColor;
        this.slot = slot;
        this.abilityItem = abilityItem;
    }

    private Ability ability;
    private ItemStack itemStack;

    public ItemStack getItemStack(boolean bypass) {
        if (this.itemStack != null && !bypass) {
            return this.itemStack;
        }

        String crateName = this.getChatColor() + ChatColor.BOLD.toString() + this.getCrateName() + " Crate";

        final ItemStack itemStack = ItemBuilder.of(Material.SKULL_ITEM)
                .name(crateName)
                .data((byte)3)
                .setLore(Arrays.asList(
                        ChatColor.translate("&7Obtain this key from at store.cavepvp.org"),
                        "",
                        ChatColor.GRAY + " - " + chatColor + ChatColor.BOLD + ChatColor.stripColor(this.getAbility().getDisplayName()),
                        "",
                        ChatColor.translate("&aLeft Click to view open this key"),
                        ChatColor.translate("&aRight Click to view crate contents")
                ))
                .build();

        final SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();

        if (this.crateName.equalsIgnoreCase("2:30 PM EST")) {
            skullMeta.setOwner("MHF_Question");
        } else {
            skullMeta.setOwner(this.crateName);
        }

        itemStack.setItemMeta(skullMeta);

        this.itemStack = itemStack;
        return itemStack;
    }

    public Ability getAbility() {

        if (this.ability != null) {
            return this.ability;
        }

        this.ability = Foxtrot.getInstance().getMapHandler().getAbilityHandler().fromName(this.abilityItem);

        return this.ability;
    }
}
