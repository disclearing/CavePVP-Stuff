package cc.fyre.neutron.listener;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.proton.util.UUIDUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class VoteListener implements Listener {

//    @EventHandler
//    private void onVote(VotifierEvent event) {
//        final UUID uuid = UUIDUtils.uuid(event.getVote().getUsername());
//
//        if (uuid == null) {
//            return;
//        }
//
//        final Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(uuid, true);
//
//        profile.setVotes(profile.getVotes()+1);
//        profile.save();
//        System.out.println("Added 1 vote to " + profile.getName() + " totaling to " + profile.getVotes());
//    }

}
