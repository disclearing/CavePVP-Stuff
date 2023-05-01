package cc.fyre.modsuite.mod

import cc.fyre.modsuite.ModSuite
import cc.fyre.modsuite.mod.event.PlayerModModeEvent
import cc.fyre.modsuite.mod.event.PlayerVisibilityEvent
import cc.fyre.modsuite.mod.item.ModModeItem
import cc.fyre.piston.Piston
import cc.fyre.proton.Proton

import com.lunarclient.bukkitapi.LunarClientAPI
import net.minecraft.server.v1_7_R4.ChatComponentText
import net.minecraft.server.v1_7_R4.PacketPlayOutChat
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.FixedMetadataValue
import org.cavepvp.profiles.Profiles
import org.cavepvp.profiles.playerProfiles.impl.ModLayout
import java.util.*

class ModMode(player: Player) {

    val player: UUID = player.uniqueId

    var enabled = false
    var visibility = ModVisibility.VISIBLE

    private var armor = arrayOf<ItemStack>()
    private var inventory = arrayOf<ItemStack>()

    private var gamemode = GameMode.SURVIVAL

    fun init(player: Player,enable: Boolean = true) {

        if (ModSuite.instance.config.modModeDisabled || !enable) {
            return
        }

        this.setModMode(true,player,true)
    }

    fun setModMode(value: Boolean,player: Player,force: Boolean = false) {

        val event = PlayerModModeEvent(player,if (value) PlayerModModeEvent.ModModeStage.ENTER else PlayerModModeEvent.ModModeStage.LEAVE,player,force).call()

        if (event.isCancelled) {

            if (event.cancelledMessage == null) {
                return
            }

            player.sendMessage(event.cancelledMessage)
            return
        }

        this.enabled = value
        this.visibility = if (value) {

            if (player.isOp) {
                ModVisibility.LOWER_STAFF
            } else {
                ModVisibility.INVISIBLE
            }

        } else {
            ModVisibility.VISIBLE
        }

        player.spigot().collidesWithEntities = (this.visibility == ModVisibility.VISIBLE)

        if (value) {
            this.armor = player.inventory.armorContents.clone()
            this.inventory =  player.inventory.contents.clone()
            this.gamemode = player.gameMode

            player.gameMode = GameMode.CREATIVE
            player.setMetadata(ModHandler.VANISH_METADATA,FixedMetadataValue(ModSuite.instance,true))

            this.refreshItems(player)
            this.sendActionBar(player)

            LunarClientAPI.getInstance().giveAllStaffModules(player)

            Proton.getInstance().visibilityHandler.update(player)
            return
        }

        player.removeMetadata(ModHandler.VANISH_METADATA,ModSuite.instance)
        player.gameMode = this.gamemode
        player.inventory.contents = this.inventory
        player.inventory.armorContents = this.armor

        this.armor = arrayOf()
        this.inventory = arrayOf()

        LunarClientAPI.getInstance().disableAllStaffModules(player)

        (player as CraftPlayer).handle.playerConnection.sendPacket(PacketPlayOutChat(ChatComponentText(""),3))

        Proton.getInstance().visibilityHandler.update(player)
    }

    fun setVisibility(visibility: ModVisibility,player: Player) {
        player.spigot().collidesWithEntities = (visibility == ModVisibility.VISIBLE)

        Bukkit.getServer().pluginManager.callEvent(PlayerVisibilityEvent(player,this.visibility,visibility))

        this.visibility = visibility

        if (this.enabled) {
            this.sendActionBar(player)
        }

        Proton.getInstance().visibilityHandler.update(player)
    }

    fun refreshItem(player: Player,item: ModModeItem) {
        player.inventory.setItem(item.getSlot(player),item.getItemStack(player))
    }

    fun refreshItems(player: Player) {
        player.inventory.clear()
        player.inventory.armorContents = null

        val layout = this.getLayout(player)

        for (item in ModHandler.getAllModModeItems()) {

            if (!item.hasPermission(player) || !layout.isItemEnabled(item.getKey())) {
                continue
            }

            player.inventory.setItem(item.getSlot(player),item.getItemStack(player))
        }

        player.inventory.setItem(ModModeItem.EDIT_ITEM_SLOT,ModModeItem.EDIT_ITEM)
    }

    fun getNextVisibility():ModVisibility {
        return when (this.visibility) {
            ModVisibility.INVISIBLE -> ModVisibility.VISIBLE
            ModVisibility.LOWER_STAFF -> ModVisibility.INVISIBLE
            ModVisibility.VISIBLE -> {

                if (Bukkit.getServer().getPlayer(this.player)?.isOp == true) {
                    ModVisibility.LOWER_STAFF
                } else {
                    ModVisibility.INVISIBLE
                }

            }
        }
    }

    fun getLayout(player: Player): ModLayout {

        val profile = Profiles.getInstance().playerProfileHandler.fetchProfile(player.uniqueId, player.name)
        var setting = profile.modLayout

        if (setting == null) {
            setting = ModLayout()
            setting.enabledItems.putAll(ModHandler.getAllModModeItems()
                .associate{it.name.lowercase() to it.isDefault()}
            )

            profile.modLayout = setting
            profile.save()
        }

        return setting
    }

    fun sendActionBar(player: Player) {

        val connection = (player as CraftPlayer).handle.playerConnection

        connection.sendPacket(PacketPlayOutChat(ChatComponentText(buildString{
            append("${ChatColor.DARK_AQUA}${ChatColor.BOLD}Visibility")
            append("${ChatColor.GRAY}: ")
            append(when (this@ModMode.visibility) {
                ModVisibility.VISIBLE -> "${ChatColor.RED}Visible"
                ModVisibility.INVISIBLE -> "${ChatColor.GREEN}Invisible"
                ModVisibility.LOWER_STAFF -> "${ChatColor.AQUA}Staff"
            })
            append(" ")
            append("${ChatColor.GRAY}┃")
            append(" ")
            append("${ChatColor.DARK_AQUA}${ChatColor.BOLD}Chat${ChatColor.GRAY}: ${when {
                Piston.getInstance().chatHandler.isMuted -> "${ChatColor.RED}Muted"
                Piston.getInstance().chatHandler.slowTime > 0 -> "${ChatColor.RED}Slowed ${ChatColor.GRAY}(${Piston.getInstance().chatHandler.slowTime}s)"
                else -> "${ChatColor.GREEN}Enabled"
            }}")


            append(" ")
            append("${ChatColor.GRAY}┃")
            append(" ")
            append("${ChatColor.DARK_AQUA}${ChatColor.BOLD}Staff Chat${ChatColor.GRAY}: ")

            if (player.hasMetadata("STAFF_CHAT")) {
                append("${ChatColor.GREEN}Staff")
            } else if (player.hasMetadata("ADMIN_CHAT")) {
                append("${ChatColor.RED}Admin")
            } else if (player.hasMetadata("MANAGER_CHAT")) {
                append("${ChatColor.LIGHT_PURPLE}Manager")
            } else {
                append("${ChatColor.RED}None")
            }

        }),2))

    }

}