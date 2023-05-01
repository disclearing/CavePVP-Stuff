package net.frozenorb.foxtrot.server.voucher;

import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class VoucherHandler {

    @Getter
    private List<Voucher> cache = new ArrayList<>();

    @Getter private EffectiveHandler effectiveHandler;

    private File file = new File(Foxtrot.getInstance().getDataFolder(), "data/vouchers.yml");
    private FileConfiguration data = YamlConfiguration.loadConfiguration(this.file);

    private Foxtrot instance;

    public VoucherHandler(Foxtrot instance) {
        this.instance = instance;
        this.effectiveHandler = new EffectiveHandler();

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        if (data.get("vouchers") == null) {
            return;
        }

        this.instance.getServer().getScheduler().runTaskLater(this.instance, () -> {
            for (String path : data.getConfigurationSection("vouchers").getKeys(false)) {
                new Voucher(
                        UUID.fromString(data.getString("vouchers." + path + ".uuid")),
                        data.getString("vouchers." + path + ".voucherType"),
                        data.getString("vouchers." + path + ".addedBy"),
                        data.getLong("vouchers." + path + ".addedTime"),
                        data.getString("vouchers." + path + ".usedBy"),
                        data.getString("vouchers." + path + ".code"),
                        data.getLong("vouchers." + path + ".redeemedTime"),
                        data.getBoolean("vouchers." + path + ".used"),
                        data.getBoolean("vouchers." + path + ".rank"),
                        data.getBoolean("vouchers." + path + ".bundle"),
                        data.getString("vouchers." + path + ".rankName"),
                        data.getString("vouchers." + path + ".rankDuration"),
                        data.getString("vouchers." + path + ".bundleID"),
                        data.getInt("vouchers." + path + ".amount")
                );
            }
        }, 20L);
    }

    public void saveData() {
        Map<String, Object> configValues = this.data.getValues(false);
        for (Map.Entry<String, Object> entry : configValues.entrySet())
            this.data.set(entry.getKey(), null);

        int i = 0;

        for (Voucher voucher : this.cache) {
            i++;

            data.set("vouchers." + i + ".uuid", voucher.getTarget().toString());
            data.set("vouchers." + i + ".voucherType", voucher.getVoucher());
            data.set("vouchers." + i + ".addedBy", voucher.getAddedBy());
            data.set("vouchers." + i + ".addedTime", voucher.getAddedTime());
            data.set("vouchers." + i + ".usedBy", voucher.getUsedBy());
            data.set("vouchers." + i + ".code", voucher.getCode());
            data.set("vouchers." + i + ".usedTime", voucher.getUsedTime());
            data.set("vouchers." + i + ".used", voucher.isUsed());
            data.set("vouchers." + i + ".rank", voucher.isRank());
            data.set("vouchers." + i + ".rankName", voucher.getRankName());
            data.set("vouchers." + i + ".rankDuration", voucher.getRankDuration());
            data.set("vouchers." + i + ".amount", voucher.getAmount());
            data.set("vouchers." + i + ".bundle", voucher.isBundle());
            data.set("vouchers." + i + ".bundleID", voucher.getBundleID());
        }

        try {
            data.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
