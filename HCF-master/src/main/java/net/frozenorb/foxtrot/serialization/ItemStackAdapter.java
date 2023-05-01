package net.frozenorb.foxtrot.serialization;

import org.bukkit.Color;
import org.bukkit.Material;
import com.google.gson.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffect;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ItemStackAdapter implements JsonDeserializer<ItemStack>, JsonSerializer<ItemStack> {
    public JsonElement serialize(final ItemStack item, Type type, JsonSerializationContext context) {
        return serialize(item);
    }

    public ItemStack deserialize(final JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        return deserialize(element);
    }

    public static JsonElement serialize(ItemStack item) {
        if (item == null) {
            item = new ItemStack(Material.AIR);
        }
        JsonObject element = new JsonObject();
        element.addProperty("id", item.getTypeId());
        element.addProperty(getDataKey(item), item.getDurability());
        element.addProperty("count", item.getAmount());
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta.hasDisplayName()) {
                element.addProperty("name", meta.getDisplayName());
            }
            if (meta.hasLore()) {
                element.add("lore", convertStringList(meta.getLore()));
            }
            if (meta instanceof LeatherArmorMeta) {
                element.addProperty("color", ((LeatherArmorMeta) meta).getColor().asRGB());
            } else if (meta instanceof SkullMeta) {
                element.addProperty("skull", ((SkullMeta) meta).getOwner());
            } else if (meta instanceof BookMeta) {
                element.addProperty("title", ((BookMeta) meta).getTitle());
                element.addProperty("author", ((BookMeta) meta).getAuthor());
                element.add("pages", convertStringList(((BookMeta) meta).getPages()));
            } else if (meta instanceof PotionMeta) {
                if (!((PotionMeta) meta).getCustomEffects().isEmpty()) {
                    element.add("potion-effects", convertPotionEffectList(((PotionMeta) meta).getCustomEffects()));
                }
            } else if (meta instanceof MapMeta) {
                element.addProperty("scaling", ((MapMeta) meta).isScaling());
            } else if (meta instanceof EnchantmentStorageMeta) {
                JsonObject storedEnchantments = new JsonObject();
                for (final Map.Entry<Enchantment, Integer> entry : ((EnchantmentStorageMeta) meta).getStoredEnchants().entrySet()) {
                    storedEnchantments.addProperty(entry.getKey().getName(), entry.getValue());
                }
                element.add("stored-enchants", storedEnchantments);
            }
        }
        if (item.getEnchantments().size() != 0) {
            JsonObject enchantments = new JsonObject();
            for (final Map.Entry<Enchantment, Integer> entry2 : item.getEnchantments().entrySet()) {
                enchantments.addProperty(entry2.getKey().getName(), entry2.getValue());
            }
            element.add("enchants", enchantments);
        }
        return element;
    }

    public static ItemStack deserialize(final JsonElement object) {
        if (object == null || !(object instanceof JsonObject)) {
            return new ItemStack(Material.AIR);
        }
        JsonObject element = (JsonObject) object;
        int id = element.get("id").getAsInt();
        short data = element.has("damage") ? element.get("damage").getAsShort() : (element.has("data") ? element.get("data").getAsShort() : 0);
        int count = element.get("count").getAsInt();
        ItemStack item = new ItemStack(id, count, data);
        ItemMeta meta = item.getItemMeta();
        if (element.has("name")) {
            meta.setDisplayName(element.get("name").getAsString());
        }
        if (element.has("lore")) {
            meta.setLore(convertStringList(element.get("lore")));
        }
        if (element.has("color")) {
            ((LeatherArmorMeta) meta).setColor(Color.fromRGB(element.get("color").getAsInt()));
        } else if (element.has("skull")) {
            ((SkullMeta) meta).setOwner(element.get("skull").getAsString());
        } else if (element.has("title")) {
            ((BookMeta) meta).setTitle(element.get("title").getAsString());
            ((BookMeta) meta).setAuthor(element.get("author").getAsString());
            ((BookMeta) meta).setPages(convertStringList(element.get("pages")));
        } else if (element.has("potion-effects")) {
            PotionMeta potionMeta = (PotionMeta) meta;
            for (final PotionEffect effect : convertPotionEffectList(element.get("potion-effects"))) {
                potionMeta.addCustomEffect(effect, false);
            }
        } else if (element.has("scaling")) {
            ((MapMeta) meta).setScaling(element.get("scaling").getAsBoolean());
        } else if (element.has("stored-enchants")) {
            JsonObject enchantments = (JsonObject) element.get("stored-enchants");
            for (final Enchantment enchantment : Enchantment.values()) {
                if (enchantments.has(enchantment.getName())) {
                    ((EnchantmentStorageMeta) meta).addStoredEnchant(enchantment, enchantments.get(enchantment.getName()).getAsInt(), true);
                }
            }
        }
        item.setItemMeta(meta);
        if (element.has("enchants")) {
            JsonObject enchantments = (JsonObject) element.get("enchants");
            for (final Enchantment enchantment : Enchantment.values()) {
                if (enchantments.has(enchantment.getName())) {
                    item.addUnsafeEnchantment(enchantment, enchantments.get(enchantment.getName()).getAsInt());
                }
            }
        }
        return item;
    }

    private static String getDataKey(final ItemStack item) {
        if (item.getType() == Material.AIR) {
            return "data";
        }
        if (Enchantment.DURABILITY.canEnchantItem(item)) {
            return "damage";
        }
        return "data";
    }

    public static JsonArray convertStringList(final Collection<String> strings) {
        JsonArray ret = new JsonArray();
        for (final String string : strings) {
            ret.add(new JsonPrimitive(string));
        }
        return ret;
    }

    public static List<String> convertStringList(final JsonElement jsonElement) {
        JsonArray array = jsonElement.getAsJsonArray();
        List<String> ret = new ArrayList<String>();
        for (final JsonElement element : array) {
            ret.add(element.getAsString());
        }
        return ret;
    }

    public static JsonArray convertPotionEffectList(final Collection<PotionEffect> potionEffects) {
        JsonArray ret = new JsonArray();
        for (final PotionEffect e : potionEffects) {
            ret.add(PotionEffectAdapter.toJson(e));
        }
        return ret;
    }

    public static List<PotionEffect> convertPotionEffectList(final JsonElement jsonElement) {
        if (jsonElement == null) {
            return null;
        }
        if (!jsonElement.isJsonArray()) {
            return null;
        }
        JsonArray array = jsonElement.getAsJsonArray();
        List<PotionEffect> ret = new ArrayList<PotionEffect>();
        for (final JsonElement element : array) {
            PotionEffect e = PotionEffectAdapter.fromJson(element);
            if (e == null) {
                continue;
            }
            ret.add(e);
        }
        return ret;
    }

    public static class Key {
        public static String ID = "id";
        public static String COUNT = "count";
        public static String NAME = "name";
        public static String LORE = "lore";
        public static String ENCHANTMENTS = "enchants";
        public static String BOOK_TITLE = "title";
        public static String BOOK_AUTHOR = "author";
        public static String BOOK_PAGES = "pages";
        public static String LEATHER_ARMOR_COLOR = "color";
        public static String MAP_SCALING = "scaling";
        public static String STORED_ENCHANTS = "stored-enchants";
        public static String SKULL_OWNER = "skull";
        public static String POTION_EFFECTS = "potion-effects";
    }
}
