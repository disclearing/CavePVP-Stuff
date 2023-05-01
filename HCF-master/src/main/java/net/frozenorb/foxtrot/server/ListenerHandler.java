package net.frozenorb.foxtrot.server;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.chat.listeners.ChatListener;
import net.frozenorb.foxtrot.commands.RatioCommand;
import net.frozenorb.foxtrot.gameplay.kitmap.game.arena.select.SelectionListeners;
import net.frozenorb.foxtrot.gameplay.kitmap.game.impl.ffa.FFAListeners;
import net.frozenorb.foxtrot.gameplay.kitmap.game.impl.knockout.KnockoutListeners;
import net.frozenorb.foxtrot.gameplay.kitmap.game.impl.minestrike.MineStrikeListeners;
import net.frozenorb.foxtrot.gameplay.kitmap.game.impl.oitq.OITQListeners;
import net.frozenorb.foxtrot.gameplay.kitmap.game.impl.parkour.ParkourListener;
import net.frozenorb.foxtrot.gameplay.kitmap.game.impl.shuffle.ShuffleListeners;
import net.frozenorb.foxtrot.gameplay.kitmap.game.impl.spleef.SpleefListeners;
import net.frozenorb.foxtrot.gameplay.kitmap.game.impl.sumo.SumoListeners;
import net.frozenorb.foxtrot.gameplay.kitmap.game.impl.tntrun.TNTRunListeners;
import net.frozenorb.foxtrot.gameplay.kitmap.game.impl.tnttag.TNTTagListeners;
import net.frozenorb.foxtrot.gameplay.kitmap.game.listener.GameListeners;
import net.frozenorb.foxtrot.gameplay.kitmap.kits.listener.KitEditorListener;
import net.frozenorb.foxtrot.gameplay.loot.partnercrate.listener.PartnerCrateListener;
import net.frozenorb.foxtrot.listener.*;
import net.frozenorb.foxtrot.map.listener.BlockDecayListeners;
import net.frozenorb.foxtrot.server.pearl.EnderpearlCooldownHandler;
import net.frozenorb.foxtrot.team.commands.team.TeamClaimCommand;
import net.frozenorb.foxtrot.team.commands.team.subclaim.TeamSubclaimCommand;
import net.frozenorb.foxtrot.team.upgrade.listener.UpgradeListener;

public class ListenerHandler {
    
    public ListenerHandler(Foxtrot instance) {
        instance.getServer().getPluginManager().registerEvents(new MapListener(), instance);
        instance.getServer().getPluginManager().registerEvents(new ReputationListener(), instance);
        instance.getServer().getPluginManager().registerEvents(new AntiGlitchListener(), instance);
        instance.getServer().getPluginManager().registerEvents(new BasicPreventionListener(), instance);
        instance.getServer().getPluginManager().registerEvents(new BorderListener(), instance);
        instance.setCombatLoggerListener(new CombatLoggerListener());
        instance.getServer().getPluginManager().registerEvents(instance.getCombatLoggerListener(), instance);
        instance.getServer().getPluginManager().registerEvents(new CrowbarListener(), instance);
        instance.getServer().getPluginManager().registerEvents(new EnchantmentLimiterListener(), instance);
        instance.getServer().getPluginManager().registerEvents(new EnderpearlCooldownHandler(), instance);
        instance.getServer().getPluginManager().registerEvents(new ChatListener(), Foxtrot.getInstance());
        instance.getServer().getPluginManager().registerEvents(new RedstoneCrashListener(instance), instance);
        instance.getServer().getPluginManager().registerEvents(new EndListener(), instance);
        instance.getServer().getPluginManager().registerEvents(new FoundDiamondsListener(), instance);
        instance.getServer().getPluginManager().registerEvents(new FoxListener(), instance);
        instance.getServer().getPluginManager().registerEvents(new RatioCommand(), instance);
        instance.getServer().getPluginManager().registerEvents(new TitleListener(), instance);
        instance.getServer().getPluginManager().registerEvents(new GoldenAppleListener(), instance);
        instance.getServer().getPluginManager().registerEvents(new OwnerClickableKitListener(instance), instance);
        instance.getServer().getPluginManager().registerEvents(new KOTHRewardKeyListener(), instance);
        instance.getServer().getPluginManager().registerEvents(new PvPTimerListener(), instance);
        instance.getServer().getPluginManager().registerEvents(new PearlListener(), instance);
        instance.getServer().getPluginManager().registerEvents(new PotionLimiterListener(), instance);
        instance.getServer().getPluginManager().registerEvents(new PracticeListener(), instance);
        instance.getServer().getPluginManager().registerEvents(new NetherPortalListener(), instance);
        instance.getServer().getPluginManager().registerEvents(new RodCooldownListener(), instance);
        instance.getServer().getPluginManager().registerEvents(new PortalTrapListener(), instance);
        instance.getServer().getPluginManager().registerEvents(new SignSubclaimListener(), instance);
        instance.getServer().getPluginManager().registerEvents(new DoorListener(), instance);
        instance.getServer().getPluginManager().registerEvents(new ElevatorsListener(), instance);
        instance.getServer().getPluginManager().registerEvents(new BlockDecayListeners(), instance);
        instance.getServer().getPluginManager().registerEvents(new AntiGriefListener(), instance);
        instance.getServer().getPluginManager().registerEvents(new SpawnListener(), instance);
        instance.getServer().getPluginManager().registerEvents(new SpawnTagListener(), instance);
        instance.getServer().getPluginManager().registerEvents(new StaffUtilsListener(), instance);
        instance.getServer().getPluginManager().registerEvents(new TeamListener(), instance);
        instance.getServer().getPluginManager().registerEvents(new PhaseGlitchListener(), instance);
        instance.getServer().getPluginManager().registerEvents(new WebsiteListener(instance), instance);
        instance.getServer().getPluginManager().registerEvents(new StatTrackerListener(), instance);
        instance.getServer().getPluginManager().registerEvents(new DomainListener(), instance);
        instance.getServer().getPluginManager().registerEvents(new TeamSubclaimCommand(), instance);
        instance.getServer().getPluginManager().registerEvents(new TeamClaimCommand(), instance);
        instance.getServer().getPluginManager().registerEvents(new SellAllListener(), instance);
        instance.getServer().getPluginManager().registerEvents(new FlyListener(), instance);
        instance.getServer().getPluginManager().registerEvents(new AntiCleanListener(), instance);
        instance.getServer().getPluginManager().registerEvents(new SpawnerTrackerListener(), instance);

        if (!Foxtrot.getInstance().getServerHandler().isTeams()) {
            instance.getServer().getPluginManager().registerEvents(new BlockConvenienceListener(), instance);
            instance.getServer().getPluginManager().registerEvents(new FixListener(), instance);
            instance.getServer().getPluginManager().registerEvents(new SugeListener(), instance);
            instance.getServer().getPluginManager().registerEvents(new PartnerCrateListener(), instance);
            instance.getServer().getPluginManager().registerEvents(new ClickableKitListener(instance), instance);
//            instance.getServer().getPluginManager().registerEvents(new PearlListener(), instance);
            instance.getServer().getPluginManager().registerEvents(new DupeGlitchListener(instance), instance);
            instance.getServer().getPluginManager().registerEvents(new AntiPrimeListener(), instance);
            instance.getServer().getPluginManager().registerEvents(new UpgradeListener(), instance);

            if (!instance.getMapHandler().isKitMap()) {
                instance.getServer().getPluginManager().registerEvents(new AutoRebuildListener(), instance);

                instance.getServer().getPluginManager().registerEvents(new CrappleLimitListener(), instance);
            }
        }

        instance.getServer().getPluginManager().registerEvents(new LunarClientListener(instance), instance);

        if (instance.getServerHandler().isReduceArmorDamage()) {
            instance.getServer().getPluginManager().registerEvents(new ArmorDamageListener(), instance);
        }

        if (instance.getServerHandler().isBlockEntitiesThroughPortals()) {
            instance.getServer().getPluginManager().registerEvents(new EntityPortalListener(), instance);
        }

        if (instance.getServerHandler().isBlockRemovalEnabled()) {
            instance.getServer().getPluginManager().registerEvents(new BlockRegenListener(), instance);
        }

        if (instance.getMapHandler().isKitMap()) {
            instance.getServer().getWorld("world").setSpawnLocation(0, 73, 0);

            instance.getServer().getPluginManager().registerEvents(new KitMapListener(), instance);
            instance.getServer().getPluginManager().registerEvents(new KitEditorListener(), instance);
            instance.getServer().getPluginManager().registerEvents(new FastBowListener(), instance);
            instance.getServer().getPluginManager().registerEvents(new GameListeners(), instance);
            instance.getServer().getPluginManager().registerEvents(new SumoListeners(), instance);
            instance.getServer().getPluginManager().registerEvents(new FFAListeners(), instance);
            instance.getServer().getPluginManager().registerEvents(new SpleefListeners(), instance);
            instance.getServer().getPluginManager().registerEvents(new ShuffleListeners(), instance);
            instance.getServer().getPluginManager().registerEvents(new SelectionListeners(), instance);
            instance.getServer().getPluginManager().registerEvents(new OITQListeners(), instance);
            instance.getServer().getPluginManager().registerEvents(new TNTRunListeners(), instance);
            instance.getServer().getPluginManager().registerEvents(new TNTTagListeners(), instance);
            instance.getServer().getPluginManager().registerEvents(new KnockoutListeners(), instance);
            instance.getServer().getPluginManager().registerEvents(new ParkourListener(), instance);
            instance.getServer().getPluginManager().registerEvents(new MineStrikeListeners(), instance);
        }

        instance.getServer().getPluginManager().registerEvents(new RedstoneCrashListener(instance), instance);
    }
    
}
