package net.frozenorb.foxtrot.gameplay.cavesays.type;

import cc.fyre.piston.Piston;
import net.frozenorb.foxtrot.gameplay.cavesays.Task;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class Chat extends Task {
    private boolean wasMuted;
    private int slowChat;

    @Override
    public String getTaskDisplayName() {
        return "Type anything in Chat";
    }

    @Override
    public String getTaskID() {
        return "Chat";
    }

    @Override
    public int getPointsToWin() {
        return 1;
    }

    @Override
    public void activate() {
        super.activate();

        this.wasMuted = Piston.getInstance().getChatHandler().isMuted();
        this.slowChat = Piston.getInstance().getChatHandler().getSlowTime();

        Piston.getInstance().getChatHandler().setMuted(false);
        Piston.getInstance().getChatHandler().setSlowTime(0);
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onEnchant(AsyncPlayerChatEvent event) {
        Piston.getInstance().getChatHandler().setMuted(this.wasMuted);
        Piston.getInstance().getChatHandler().setSlowTime(this.slowChat);

        this.addProgress(event.getPlayer());
    }
}