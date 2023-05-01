package net.frozenorb.foxtrot.gameplay.armorclass;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.frozenorb.foxtrot.util.RandomUtil;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@RequiredArgsConstructor
public enum ArmorPiece {

    HELMET(Material.DIAMOND_HELMET, Material.LEATHER_HELMET),
    CHESTPLATE(Material.DIAMOND_CHESTPLATE, Material.LEATHER_CHESTPLATE),
    LEGGINGS(Material.DIAMOND_LEGGINGS, Material.LEATHER_LEGGINGS),
    BOOTS(Material.DIAMOND_BOOTS, Material.LEATHER_BOOTS),
    SWORD(Material.DIAMOND_SWORD, Material.DIAMOND_SWORD),
    AXE(Material.DIAMOND_AXE, Material.DIAMOND_AXE),
    BOW(Material.BOW, Material.BOW);

    private final Material defaultMaterial;
    private final Material archerMaterial;

    public boolean isArmor() {
        switch(this) {
            case HELMET:
            case CHESTPLATE:
            case LEGGINGS:
            case BOOTS:
                return true;
            default:
                return false;
        }
    }

    public boolean isWeapon() {
        switch(this) {
            case SWORD:
            case AXE:
            case BOW:
                return true;
            default:
                return false;
        }
    }

    public static ArmorPiece fromMaterial(Material material) {
        switch(material) {
            case PUMPKIN:
            case SKULL_ITEM:
            case GOLD_HELMET:
            case LEATHER_HELMET:
            case CHAINMAIL_HELMET:
            case IRON_HELMET:
            case DIAMOND_HELMET:
                return HELMET;
            case GOLD_CHESTPLATE:
            case LEATHER_CHESTPLATE:
            case CHAINMAIL_CHESTPLATE:
            case IRON_CHESTPLATE:
            case DIAMOND_CHESTPLATE:
                return CHESTPLATE;
            case GOLD_BOOTS:
            case LEATHER_BOOTS:
            case CHAINMAIL_BOOTS:
            case IRON_BOOTS:
            case DIAMOND_BOOTS:
                return BOOTS;
            case GOLD_LEGGINGS:
            case LEATHER_LEGGINGS:
            case CHAINMAIL_LEGGINGS:
            case IRON_LEGGINGS:
            case DIAMOND_LEGGINGS:
                return LEGGINGS;
            case WOOD_SWORD:
            case STONE_SWORD:
            case GOLD_SWORD:
            case IRON_SWORD:
            case DIAMOND_SWORD:
                return SWORD;
            case WOOD_AXE:
            case STONE_AXE:
            case GOLD_AXE:
            case IRON_AXE:
            case DIAMOND_AXE:
                return AXE;
            case BOW:
                return BOW;
        }

        return null;
    }

    public static ArmorPiece random() {
        List<ArmorPiece> armorPieces = new ArrayList<>(Arrays.asList(values()));
        armorPieces.remove(BOW);

        return armorPieces.get(RandomUtil.RANDOM.nextInt(armorPieces.size()));
    }
}
