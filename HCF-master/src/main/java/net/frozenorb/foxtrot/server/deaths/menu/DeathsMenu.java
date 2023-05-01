package net.frozenorb.foxtrot.server.deaths.menu;

import cc.fyre.proton.Proton;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.pagination.PaginatedMenu;
import cc.fyre.proton.util.ItemBuilder;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.client.model.DBCollectionFindOptions;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;

import java.text.DateFormat;
import java.util.*;

@AllArgsConstructor
public class DeathsMenu extends PaginatedMenu {
    private OfflinePlayer target;
    private DateFormat FORMAT;

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();
        final UUID uuid = target.getUniqueId();

        int i = 0;

        final DBCollection mongoCollection = Foxtrot.getInstance().getMongoPool().getDB(Foxtrot.MONGO_DB_NAME).getCollection("Deaths");

        for (DBObject object : mongoCollection.find(new BasicDBObject("uuid", uuid.toString().replace("-", "")), new DBCollectionFindOptions().sort(new BasicDBObject("when", -1)))) {
            BasicDBObject basicDBObject = (BasicDBObject) object;

            StringBuilder message = new StringBuilder();

            message.append(ChatColor.RED).append(Proton.getInstance().getUuidCache().name(uuid));

            if (object.get("killerUUID") != null) {
                message.append(ChatColor.YELLOW + " was slain by " + ChatColor.RED).append(Proton.getInstance().getUuidCache().name(UUIDfromString(object.get("killerUUID").toString())));
            } else {
                if (object.get("reason") != null) {
                    message.append(ChatColor.YELLOW + " died to ").append(object.get("reason").toString().toLowerCase()).append(" damage.");
                } else {
                    message.append(ChatColor.YELLOW + " died to an unknown reason.");
                }
            }

            toReturn.put(i, new Button() {
                @Override
                public String getName(Player player) {
                    return null;
                }

                @Override
                public List<String> getDescription(Player player) {
                    return null;
                }

                @Override
                public Material getMaterial(Player player) {
                    return null;
                }

                @Override
                public ItemStack getButtonItem(Player player) {
                    return ItemBuilder.of(Material.PAPER).name(message.toString()).setLore(Arrays.asList(ChatColor.WHITE + FORMAT.format(basicDBObject.getDate("when")), "", ChatColor.GRAY + "Click to restore inventory.")).build();
                }

                @Override
                public void clicked(Player player, int i, ClickType clickType) {
                    player.closeInventory();

                    new DeathBreakDownMenu(basicDBObject, target, FORMAT).openMenu(player);
                }
            });
            i++;
        }

        return toReturn;
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Deaths";
    }

    private static UUID UUIDfromString(String string) {
        return UUID.fromString(
                string.replaceFirst(
                        "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"
                )
        );
    }
}