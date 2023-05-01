package net.frozenorb.foxtrot.gameplay.loot.redeem;

import cc.fyre.proton.Proton;
import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.FileConfig;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class RedeemCreatorHandler {

    @Getter private final Map<String, String> creators = new HashMap<>();

    public RedeemCreatorHandler() {
        FileConfig fileConfig = new FileConfig(Foxtrot.getInstance(), "redeem_creator.yml");
        FileConfiguration config = fileConfig.getConfig();
        config.options().copyDefaults(true);
        fileConfig.save();

//        System.out.println("Partners:");

        for (String partner : config.getConfigurationSection("partners").getKeys(false)) {
//            System.out.println("- " + partner);
            creators.put(partner.toLowerCase(Locale.ROOT), partner);

            for (String alias : config.getStringList("partners." + partner + ".aliases")) {
//                System.out.println("  - " + alias);
                creators.put(alias.toLowerCase(Locale.ROOT), partner);
            }
        }
    }

    public String getCreator(String input) {
        return creators.get(input.toLowerCase(Locale.ROOT));
    }

    public boolean hasRedeemed(Player player) {
        return Proton.getInstance().runRedisCommand(jedis -> jedis.exists("redeemed_creator:" + player.getUniqueId().toString()));
    }

    public void redeem(Player player, String creator) {
        Proton.getInstance().runRedisCommand(jedis -> jedis.set("redeemed_creator:" + player.getUniqueId().toString(), creator));
    }
}
