package net.frozenorb.foxtrot.server.deaths;

import cc.fyre.proton.Proton;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.UUIDUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.client.model.DBCollectionFindOptions;
import com.mongodb.util.JSON;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.DisableCommand;
import net.frozenorb.foxtrot.commands.LastInvCommand;
import net.frozenorb.foxtrot.listener.ClickableKitListener;
import net.frozenorb.foxtrot.server.deaths.menu.DeathMainMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class DeathsCommand {

    private final static DateFormat FORMAT = new SimpleDateFormat("M dd yyyy h:mm a");

    @Command(names={ "deathrefund" }, permission="foxtrot.deathrefund")
    public static void refund(Player sender, @Parameter(name="id") String id) {
        Foxtrot.getInstance().getServer().getScheduler().runTaskAsynchronously(Foxtrot.getInstance(), () -> {
            DBCollection mongoCollection = Foxtrot.getInstance().getMongoPool().getDB(Foxtrot.MONGO_DB_NAME).getCollection("Deaths");
            DBObject object = mongoCollection.findOne(id);

            if (object != null) {
                BasicDBObject basicDBObject = (BasicDBObject) object;
                Player player = Bukkit.getPlayer(UUIDfromString(object.get("uuid").toString()));

                if (basicDBObject.containsKey("refundedBy")) {
                    sender.sendMessage(ChatColor.RED + "This death was already refunded by " + UUIDUtils.name(UUIDfromString(basicDBObject.getString("refundedBy"))) + ".");
                    return;
                }

                if (player == null) {
                    sender.sendMessage(ChatColor.RED + "Player isn't on to receive items.");
                    return;
                }

                if (player.getWorld().getName().equalsIgnoreCase("Deathban")) {
                    player.sendMessage(ChatColor.RED + "You may not give someone who is in Deathban arena their inventory back!");
                    return;
                }

                ItemStack[] contents = Proton.PLAIN_GSON.fromJson(JSON.serialize(((BasicDBObject) basicDBObject.get("playerInventory")).get("contents")),
                        ItemStack[].class);
                ItemStack[] armor = Proton.PLAIN_GSON.fromJson(JSON.serialize(((BasicDBObject) basicDBObject.get("playerInventory")).get("armor")),
                        ItemStack[].class);

                final List<ItemStack> newList = Arrays.stream(contents).collect(Collectors.toList());

                for (ItemStack content : contents) {
                    if (content == null) {
                        continue;
                    }

                    if (content.getType() == Material.TRIPWIRE_HOOK || content.getType() == Material.BEACON || content.getType() == Material.ENDER_CHEST || content.getType() == Material.DROPPER) {
                        newList.remove(content);
                    }

                    if (content.getItemMeta() == null) {
                        continue;
                    }

                    if (ClickableKitListener.isSimilar(content)) {
                        newList.remove(content);
                    }

                    if (content.getItemMeta().getDisplayName() == null) {
                        continue;
                    }

                    if (content.getType() == Material.NETHER_STAR && content.getItemMeta().getDisplayName().contains("Key")) {
                        newList.remove(content);
                    }

                    if (content.getItemMeta().getLore() == null) {
                        continue;
                    }

                    if (content.getItemMeta().getLore().contains(ChatColor.translate("&7&oOpen this key at &aSpawn &7&oto redeem the loot!"))) {
                        newList.remove(content);
                    }
                }

                LastInvCommand.cleanLoot(armor);

                player.getInventory().setContents(newList.toArray(new ItemStack[0]));
                player.getInventory().setArmorContents(armor);

                basicDBObject.put("refundedBy", sender.getUniqueId().toString().replace("-", ""));
                basicDBObject.put("refundedAt", new Date());

                mongoCollection.save(basicDBObject);

                player.sendMessage(ChatColor.GREEN + "Your inventory has been reset to an inventory from a previous life.");
                sender.sendMessage(ChatColor.GREEN + "Successfully refunded inventory to " + player.getName() + ".");

            } else {
                sender.sendMessage(ChatColor.RED + "Death not found.");
            }

        });
    }

    @Command(names = {"deaths", "kills"}, permission = "foxtrot.deaths")
    public static void newDeaths(Player player, @Parameter(name = "target")OfflinePlayer offlinePlayer) {
        if (DisableCommand.deaths) {
            player.sendMessage(ChatColor.RED + "Deaths GUI is currently disabled by SimplyTrash!");
            return;
        }

        new DeathMainMenu(offlinePlayer).openMenu(player);
    }

    private static UUID UUIDfromString(String string) {
        return UUID.fromString(
                string.replaceFirst(
                        "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"
                )
        );
    }

}