package cc.fyre.neutron.profile.namemc;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.profile.attributes.grant.Grant;
import cc.fyre.neutron.rank.Rank;
import cc.fyre.proton.uuid.UUIDCache;
import lombok.SneakyThrows;

import java.net.URL;
import java.util.Scanner;
import java.util.UUID;

public class NameMCUtil {

    @SneakyThrows
    public static boolean isVerified(UUID uuid) {
        return new Scanner(new URL("https://api.namemc.com/server/cavepvp.org/likes?profile=" + uuid.toString()).openStream()).nextBoolean();
    }

    public static void verify(Profile profile) {
//        Neutron.getInstance().getServer().dispatchCommand(Neutron.getInstance().getServer().getConsoleSender(), "ogrant " + profile.getName() + " Cave 5d Free 5-day Promotion");

        final Rank rank = Neutron.getInstance().getRankHandler().fromName("Iron");

        if (rank == null) {
            return;
        }

        if (profile.getGrants().stream().noneMatch(it -> it.getRank().getName().equalsIgnoreCase("Iron"))) {
            final Grant grant = new Grant(rank, UUIDCache.CONSOLE_UUID, (long) Integer.MAX_VALUE, "Voted twice");

            profile.getGrants().add(grant);
            profile.recalculateGrants();
            profile.save();
        }
    }
}