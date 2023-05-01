package net.frozenorb.foxtrot.gameplay.coupondrops;

import cc.fyre.proton.command.Command;
import lombok.Getter;
import lombok.Setter;
import net.buycraft.plugin.data.Coupon;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.coupondrops.listener.CouponDropsListener;
import net.frozenorb.foxtrot.gameplay.coupondrops.tasks.CouponDropsTask;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

public class CouponDropsHandler {
    private Foxtrot instance;
    @Getter @Setter private long dropsIn;
    @Getter @Setter private Location location;

    public CouponDropsHandler(Foxtrot instance) {
        this.instance = instance;

        instance.getServer().getPluginManager().registerEvents(new CouponDropsListener(instance, this), instance);
    }

    public Coupon createCoupon() {
        final int[] percentages = { 10, 15, 20 };
        final int percentage = percentages[ThreadLocalRandom.current().nextInt(percentages.length)];

        final Coupon.Discount discount = new Coupon.Discount("percentage", new BigDecimal(percentage), new BigDecimal("0.0"));

        final Calendar expiration = Calendar.getInstance();
        expiration.set(Calendar.YEAR, 2030);

        final Coupon coupon = Coupon.builder()
                .note("Generated coupon drop at " + new Date(System.currentTimeMillis()).toLocaleString())
                .basketType("single")
                .code("DROP-" + RandomStringUtils.random(4, true, true))
                .userLimit(1)
                .redeemUnlimited(false)
                .startDate(new Date(System.currentTimeMillis()))
                .minimum(new BigDecimal("0.0"))
                .expire(new Coupon.Expire("limit", 1, expiration.getTime()))
                .discountMethod(2)
                .effective(new Coupon.Effective("cart", Foxtrot.getInstance().getVoucherHandler().getEffectiveHandler().getEffectivePackages(), new ArrayList<>()))
                .startDate(new Date(System.currentTimeMillis()))
                .discount(discount).build();

        Foxtrot.getInstance().getServer().getScheduler().runTaskAsynchronously(Foxtrot.getInstance(), () -> {

            try {
                Foxtrot.getInstance().getBuycraftPlugin().getApiClient().createCoupon(coupon).execute();
            } catch (IOException e) {
                System.out.println("FAILED TO CREATE COUPON ! ! !");
                e.printStackTrace();
            }
        });

        return coupon;
    }

    @Command(names = {"coupondrops start", "discountdrops start", "discountdrop start", "coupondrop start"}, permission = "op")
    public static void execute(Player player) {
        player.sendMessage(ChatColor.GREEN + "Started Coupon Drop!");
        new CouponDropsTask(Foxtrot.getInstance(), Foxtrot.getInstance().getCouponDropsHandler().createCoupon()).runTask(Foxtrot.getInstance());
    }

    @Command(names = {"coupondrops fix", "discountdrops fix", "discountdrop fix", "coupondrop fix"}, permission = "op")
    public static void fix(Player player) {
        player.sendMessage(ChatColor.GREEN + "Fixed Coupon Drop!");

        Foxtrot.getInstance().getCouponDropsHandler().setDropsIn(0);
        Foxtrot.getInstance().getCouponDropsHandler().setLocation(null);
    }

}
