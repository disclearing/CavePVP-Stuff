package org.bukkit.craftbukkit.util;

import net.minecraft.server.MobEffect;
import net.minecraft.server.MobEffectList;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class CraftPotionUtils {
    
    public static PotionEffectType toBukkit(MobEffectList nms) {
        return PotionEffectType.getById(nms.getId());
    }
    
    public static PotionEffect toBukkit(MobEffect effect) {
        return new PotionEffect(
                PotionEffectType.getById(effect.getEffectId()),
                effect.getDuration(),
                effect.getAmplifier(),
                effect.isAmbient());
    }
    
    public static MobEffectList toNMS(PotionEffectType effect) {
        return MobEffectList.byId[effect.getId()];
    }
    
    public static MobEffect toNMS(PotionEffect effect) {
        return new MobEffect(effect.getType().getId(),
                effect.getDuration(),
                effect.getAmplifier(),
                effect.isAmbient());
    }
    
    public static MobEffect cloneWithDuration(MobEffect effect, int duration) {
        return new MobEffect(effect.getEffectId(),
                duration,
                effect.getAmplifier(),
                effect.isAmbient());
    }
    
    public static void extendDuration(MobEffect effect, int duration) {
        effect.a(cloneWithDuration(effect, duration));
    }
    
}
