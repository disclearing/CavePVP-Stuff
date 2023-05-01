package net.frozenorb.foxtrot.server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.event.player.PlayerMoveEvent;

@AllArgsConstructor
public enum RegionType {

    WARZONE(RegionMoveHandler.ALWAYS_TRUE),
    WILDNERNESS(RegionMoveHandler.ALWAYS_TRUE),
    ROAD(RegionMoveHandler.ALWAYS_TRUE),

    KOTH(RegionMoveHandler.PVP_TIMER),
    CITADEL(RegionMoveHandler.PVP_TIMER),
    CONQUEST(RegionMoveHandler.PVP_TIMER),
    CLAIMED_LAND(RegionMoveHandler.PVP_TIMER),

    SPAWN(new RegionMoveHandler() {

        @Override
        public boolean handleMove(PlayerMoveEvent event) {
            if (SpawnTagHandler.isTagged(event.getPlayer()) && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
                event.getPlayer().sendMessage(ChatColor.RED + "You cannot enter spawn while spawn-tagged.");
                event.setTo(event.getFrom());
                return (false);
            }

            if (!event.getPlayer().isDead()) {
                event.getPlayer().setHealth(event.getPlayer().getMaxHealth());
                event.getPlayer().setFoodLevel(20);
            }

            return (true);
        }

    });

    @Getter private RegionMoveHandler moveHandler;

}