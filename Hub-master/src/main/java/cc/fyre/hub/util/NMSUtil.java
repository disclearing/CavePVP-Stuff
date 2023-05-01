package cc.fyre.hub.util;


import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class NMSUtil {
    public static ItemStack applySkullTexture(ItemStack itemStack, String texture) {
        final net.minecraft.server.v1_8_R3.ItemStack nmsCopy = CraftItemStack.asNMSCopy(itemStack);

        final NBTTagCompound tag = nmsCopy.getTag() != null ? nmsCopy.getTag() : new NBTTagCompound();
        final NBTTagCompound skullOwner = new NBTTagCompound();
        skullOwner.setString("Id", UUID.randomUUID().toString());

        final NBTTagCompound compound = new NBTTagCompound();
        compound.setString("Value", texture);

        final NBTTagList textures = new NBTTagList();
        textures.add(compound);

        final NBTTagCompound properties = new NBTTagCompound();

        properties.set("textures", textures);
        skullOwner.set("Properties", properties);
        tag.set("SkullOwner", skullOwner);

        nmsCopy.setTag(tag);

        return CraftItemStack.asBukkitCopy(nmsCopy);
    }

}
