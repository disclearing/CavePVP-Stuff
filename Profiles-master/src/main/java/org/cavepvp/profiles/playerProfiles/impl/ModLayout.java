package org.cavepvp.profiles.playerProfiles.impl;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.DyeColor;

import java.util.HashMap;
import java.util.Map;

public class ModLayout {
    @Getter @Setter private DyeColor carpetColor = DyeColor.ORANGE;
    @Getter private Map<String, Integer> itemSlots = new HashMap<>();
    @Getter private Map<String, Boolean> enabledItems = new HashMap<>();
    @Getter private Map<String, Integer> disabledItemSlots = new HashMap<>();

    public boolean isItemEnabled(String modModeItem) {
        if (!this.enabledItems.containsKey(modModeItem.toLowerCase())) {
            return false;
        }

        return this.enabledItems.get(modModeItem.toLowerCase());
    }

    public void setItemEnabled(String modModeItem, boolean value) {
        this.enabledItems.put(modModeItem.toLowerCase(), value);
    }

    public int getSlotByItem(String modModeItem, int defaultValue) {
        return this.itemSlots.getOrDefault(modModeItem.toLowerCase(), defaultValue);
    }

    public void setItemSlot(String modModeItem, int value) {
        this.itemSlots.put(modModeItem.toLowerCase(), value);
    }

    public int getDisabledItemSlot(String modModeItem) {
        return this.disabledItemSlots.get(modModeItem.toLowerCase());
    }

    public void setDisabledItemSlot(String modModeItem, int value) {
        this.disabledItemSlots.put(modModeItem.toLowerCase(), value);
    }

    public void removeDisabledItemSlot(String modModeItem) {
        this.disabledItemSlots.remove(modModeItem.toLowerCase());
    }
}
