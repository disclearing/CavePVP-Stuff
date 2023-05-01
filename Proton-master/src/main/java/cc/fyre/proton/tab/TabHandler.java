package cc.fyre.proton.tab;

import cc.fyre.proton.Proton;
import cc.fyre.proton.tab.construct.Tab;
import cc.fyre.proton.tab.construct.TabAdapter;
import cc.fyre.proton.tab.listener.TabListener;
import cc.fyre.proton.tab.provider.LayoutProvider;
import cc.fyre.proton.util.Pair;
import com.comphenix.protocol.ProtocolLibrary;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.com.google.gson.JsonArray;
import net.minecraft.util.com.google.gson.JsonParser;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.HttpAuthenticationService;
import net.minecraft.util.com.mojang.authlib.minecraft.MinecraftSessionService;
import net.minecraft.util.com.mojang.authlib.properties.PropertyMap;
import net.minecraft.util.com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import org.bukkit.entity.Player;

import java.net.Proxy;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class TabHandler {
    
    @Getter private final AtomicReference<Object> propertyMapSerializer = new AtomicReference<>();
    @Getter private final AtomicReference<Object> defaultPropertyMap = new AtomicReference<>();
    @Getter @Setter private LayoutProvider layoutProvider;
    @Getter private Map<String,Tab> tabs = new ConcurrentHashMap<>();
    @Getter private Map<UUID, Pair<String, String>> cachedTextures = new ConcurrentHashMap<>();

    public TabHandler() {
        
        if (Proton.getInstance().getConfig().getBoolean("disableTab", false)) {
            return;
        }

        this.getDefaultPropertyMap();
        
        new TabThread().start();

        Proton.getInstance().getServer().getPluginManager().registerEvents(new TabListener(), Proton.getInstance());

        if (Proton.getInstance().getServer().getPluginManager().getPlugin("ProtocolLib") != null) {
            ProtocolLibrary.getProtocolManager().addPacketListener(new TabAdapter());
        }

    }
    

    public void addPlayer(Player player) {
        this.tabs.put(player.getName(), new Tab(player));

    }
    
    public void updatePlayer(Player player) {
        if (this.tabs.containsKey(player.getName())) {
            this.tabs.get(player.getName()).update();
        }

    }

    public void removePlayer(Player player) {
        this.tabs.remove(player.getName());
    }

    private PropertyMap fetchSkin() {


        //final String propertyMap = Proton.getInstance().runBackboneRedisCommand(redis -> redis.get("propertyMap"));
        final String propertyMap = null;

        if (propertyMap != null && !propertyMap.isEmpty()) {

            Proton.getInstance().getServer().getLogger().info("Using cached PropertyMap for skin...");

            final JsonArray jsonObject = (new JsonParser()).parse(propertyMap).getAsJsonArray();

            return getPropertyMapSerializer().deserialize(jsonObject,null,null);
        } else {
            final GameProfile profile = new GameProfile(UUID.fromString("6b22037d-c043-4271-94f2-adb00368bf16"), "bananasquad");
            final HttpAuthenticationService authenticationService = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
            final MinecraftSessionService sessionService = authenticationService.createMinecraftSessionService();
            final GameProfile profile1 = sessionService.fillProfileProperties(profile, true);
            final PropertyMap localPropertyMap = profile1.getProperties();

            /*
            Proton.getInstance().runBackboneRedisCommand(redis -> {

                Proton.getInstance().getServer().getLogger().info("Caching PropertyMap for skin...");
                redis.setex("propertyMap", 3600,getPropertyMapSerializer().serialize(localPropertyMap,null,null).toString());
                return null;

            });*/

            return localPropertyMap;
        }

    }

    public PropertyMap.Serializer getPropertyMapSerializer() {
       
        Object value = propertyMapSerializer.get();
        if (value == null) {
            synchronized(propertyMapSerializer) {
                value = propertyMapSerializer.get();
                if (value == null) {
                    PropertyMap.Serializer actualValue = new PropertyMap.Serializer();
                    value = actualValue == null ? propertyMapSerializer : actualValue;
                    propertyMapSerializer.set(value);
                }
            }
        }

        return ((PropertyMap.Serializer)(value == propertyMapSerializer ? null : value));
    }

    public PropertyMap getDefaultPropertyMap() {
        Object value = defaultPropertyMap.get();
        if (value == null) {
            synchronized(defaultPropertyMap) {
                value = defaultPropertyMap.get();
                if (value == null) {
                    PropertyMap actualValue = fetchSkin();
                    value = actualValue == null ? defaultPropertyMap : actualValue;
                    defaultPropertyMap.set(value);
                }
            }
        }

        return ((PropertyMap)(value == defaultPropertyMap ? null : value));
    }

}

