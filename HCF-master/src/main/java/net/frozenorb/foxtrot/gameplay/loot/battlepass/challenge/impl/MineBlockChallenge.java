package net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.impl;

import cc.fyre.proton.util.ItemUtils;
import lombok.Getter;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.Challenge;
import net.frozenorb.foxtrot.util.Formats;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;

@Getter
public class MineBlockChallenge extends Challenge {

    private Material material;
    private int amount;

    public MineBlockChallenge(String id, String text, int experience, boolean daily, Material material, int amount) {
        super(id, text, experience, daily);

        this.material = material;
        this.amount = amount;
    }

    public String getBlockName() {
        return ItemUtils.getName(new ItemStack(material));
    }

    public int getMined(Player player) {
        return getProgress(player).getBlocksMined(material, isDaily());
    }

    @Override
    public Type getAbstractType() {
        return MineBlockChallenge.class;
    }

    @Override
    public String getText() {
        return "Mine " + Formats.formatNumber(amount) + " " + getBlockName();
    }

    @Override
    public boolean hasStarted(Player player) {
        return getMined(player) > 0;
    }

    @Override
    public boolean meetsCompletionCriteria(Player player) {
        return getMined(player) >= amount;
    }

    @Override
    public String getProgressText(Player player) {
        int remaining = amount - getMined(player);
        return "You need to mine " + Formats.formatNumber(remaining) + " more " + getBlockName() + ".";
    }

}
