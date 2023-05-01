package net.frozenorb.foxtrot.util;

import lombok.experimental.UtilityClass;
import net.minecraft.server.v1_7_R4.EntityPotion;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

@UtilityClass
public final class PotionUtil {

	public static String getName(PotionEffectType type) {

		if (type.getId() == 1) {
			return "Speed";
		} else if (type.getId() == 2) {
			return "Slowness";
		} else if (type.getId() == 3) {
			return "Haste";
		} else if (type.getId() == 4) {
			return "Mining Fatigue";
		} else if (type.getId() == 5) {
			return "Strength";
		} else if (type.getId() == 6) {
			return "Heal";
		} else if (type.getId() == 7) {
			return "Harm";
		} else if (type.getId() == 8) {
			return "Jump Boost";
		} else if (type.getId() == 9) {
			return "Nausea";
		} else if (type.getId() == 10) {
			return "Regeneration";
		} else if (type.getId() == 11) {
			return "Resistance";
		} else if (type.getId() == 12) {
			return "Fire Resistance";
		} else if (type.getId() == 13) {
			return "Water Breathing";
		} else if (type.getId() == 14) {
			return "Invisibility";
		} else if (type.getId() == 15) {
			return "Blindness";
		} else if (type.getId() == 16) {
			return "Night Vision";
		} else if (type.getId() == 17) {
			return "Hunger";
		} else if (type.getId() == 18) {
			return "Weakness";
		} else if (type.getId() == 19) {
			return "Poison";
		} else if (type.getId() == 20) {
			return "Wither";
		} else if (type.getId() == 21) {
			return "Health Boost";
		} else if (type.getId() == 22) {
			return "Absorption";
		} else if (type.getId() == 23) {
			return "Saturation";
		}

		return "Error";
	}

	public static ChatColor getColor(PotionEffectType type) {

		if (type.getId() == 1) {
			return ChatColor.AQUA;
		} else if (type.getId() == 2) {
			return ChatColor.DARK_GRAY;
		} else if (type.getId() == 3) {
			return ChatColor.GOLD;
		} else if (type.getId() == 4) {
			return ChatColor.WHITE;
		} else if (type.getId() == 5) {
			return ChatColor.RED;
		} else if (type.getId() == 6) {
			return ChatColor.LIGHT_PURPLE;
		} else if (type.getId() == 7) {
			return ChatColor.DARK_PURPLE;
		} else if (type.getId() == 8) {
			return ChatColor.GREEN;
		} else if (type.getId() == 9) {
			return ChatColor.LIGHT_PURPLE;
		} else if (type.getId() == 10) {
			return ChatColor.LIGHT_PURPLE;
		} else if (type.getId() == 11) {
			return ChatColor.GRAY;
		} else if (type.getId() == 12) {
			return ChatColor.GOLD;
		} else if (type.getId() == 13) {
			return ChatColor.BLUE;
		} else if (type.getId() == 14) {
			return ChatColor.GRAY;
		} else if (type.getId() == 15) {
			return ChatColor.DARK_GRAY;
		} else if (type.getId() == 16) {
			return ChatColor.DARK_BLUE;
		} else if (type.getId() == 17) {
			return ChatColor.YELLOW;
		} else if (type.getId() == 18) {
			return ChatColor.GRAY;
		} else if (type.getId() == 19) {
			return ChatColor.DARK_GREEN;
		} else if (type.getId() == 20) {
			return ChatColor.DARK_PURPLE;
		} else if (type.getId() == 21) {
			return ChatColor.RED;
		} else if (type.getId() == 22) {
			return ChatColor.YELLOW;
		} else if (type.getId() == 23) {
			return ChatColor.WHITE;
		}

		return ChatColor.DARK_RED;
	}

	public static String getFancyName(PotionEffectType type) {
		return getColor(type) + getName(type);
	}

	public void splashPotion(Player player, ItemStack itemStack) {
		CraftWorld world = (CraftWorld) player.getWorld();
		CraftPlayer craftPlayer = (CraftPlayer) player;
		net.minecraft.server.v1_7_R4.ItemStack stack = CraftItemStack.asNMSCopy(itemStack);
		EntityPotion entity = new EntityPotion(world.getHandle(), craftPlayer.getHandle(), stack);
		world.getHandle().addEntity(entity);
	}

}
