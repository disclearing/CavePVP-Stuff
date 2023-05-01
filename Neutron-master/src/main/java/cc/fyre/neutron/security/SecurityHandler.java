package cc.fyre.neutron.security;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.security.comparator.SecurityAlertDateComparator;
import cc.fyre.neutron.security.listener.SecurityListener;
import cc.fyre.neutron.security.packet.SecurityAlertUpdatePacket;
import cc.fyre.proton.Proton;
import cc.fyre.universe.UniverseAPI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.block.data.type.Piston;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class SecurityHandler {
    private Neutron instance;
    @Getter private List<SecurityAlert> alerts = new ArrayList<>();

    @Getter private MongoCollection<Document> collection;

    public SecurityHandler(Neutron instance) {
        this.instance = instance;

        this.instance.getServer().getPluginManager().registerEvents(new SecurityListener(this.instance, this), this.instance);

        this.collection = instance.getMongoHandler().getMongoDatabase().getCollection("securityAlerts");
        this.collection.find().iterator().forEachRemaining(it -> alerts.add(Neutron.GSON.fromJson(it.toJson(), SecurityAlert.class)));
    }

    public List<SecurityAlert> getSortedAlerts() {
        return this.alerts.stream().sorted(new SecurityAlertDateComparator().reversed()).collect(Collectors.toList());
    }

    public List<SecurityAlert> findUrgentAlerts() {
        return this.alerts.stream().filter(SecurityAlert::isUrgent).collect(Collectors.toList());
    }

    public List<SecurityAlert> findAlertsVictim(UUID victim, boolean urgent) {
        if (urgent) {
            return this.findUrgentAlerts().stream().filter(it -> it.getVictim().equals(victim)).collect(Collectors.toList());
        }

        return this.getSortedAlerts().stream().filter(it -> it.getVictim().equals(victim)).collect(Collectors.toList());
    }

    public List<SecurityAlert> findAlertsTarget(UUID target, boolean urgent) {
        if (urgent) {
            return this.findUrgentAlerts().stream().filter(it -> it.getTarget().equals(target)).collect(Collectors.toList());
        }

        return this.getSortedAlerts().stream().filter(it -> it.getTarget().equals(target)).collect(Collectors.toList());
    }

    public List<SecurityAlert> findAlertsTarget(UUID target, AlertType alertType, boolean urgent) {
        if (urgent) {
            return this.findUrgentAlerts().stream().filter(it -> it.getAlertType().equals(alertType) && it.getTarget().equals(target)).collect(Collectors.toList());
        }

        return this.getSortedAlerts().stream().filter(it -> it.getAlertType().equals(alertType) && it.getTarget().equals(target)).collect(Collectors.toList());
    }

    public List<SecurityAlert> findAlertsVictim(UUID victim, AlertType alertType, boolean urgent) {
        if (urgent) {
            return this.findUrgentAlerts().stream().filter(it -> it.getAlertType().equals(alertType) && it.getVictim().equals(victim)).collect(Collectors.toList());
        }

        return this.getSortedAlerts().stream().filter(it -> it.getAlertType().equals(alertType) && it.getVictim().equals(victim)).collect(Collectors.toList());
    }

    public List<SecurityAlert> findAlerts(UUID target, UUID victim, AlertType alertType, boolean urgent) {
        if (alertType != null) {
            if (target != null) {
                return this.findAlertsTarget(target, alertType, urgent);
            }

            if (victim != null) {
                return this.findAlertsVictim(victim, alertType, urgent);
            }
        }

        if (target != null) {
            return this.findAlertsTarget(target, urgent);
        }

        if (victim != null) {
            return this.findAlertsVictim(victim, urgent);
        }

        if (urgent) {
            return this.findUrgentAlerts();
        }

        return this.getSortedAlerts();
    }

    public SecurityAlert addSecurityAlert(UUID target, UUID victim, AlertType alertType, boolean urgent, String description) {
        return this.addSecurityAlert(target, victim, alertType, urgent, Collections.singletonList(description));
    }

    public void addSecurityAlert(UUID target, UUID victim, AlertType alertType, boolean urgent, String... description) {
        this.addSecurityAlert(target, victim, alertType, urgent, Arrays.asList(description));
    }

    public SecurityAlert addSecurityAlert(UUID target, UUID victim, AlertType alertType, boolean urgent, List<String> description) {
        final SecurityAlert securityAlert = new SecurityAlert(UUID.randomUUID(), target, victim, UniverseAPI.getServerName(), System.currentTimeMillis(), alertType, description, urgent);

        this.updateAlert(securityAlert);
        return securityAlert;
    }

    public SecurityAlert loadAlert(String id) {
        if (this.instance.getServer().isPrimaryThread()) {
            return CompletableFuture.supplyAsync(() -> this.loadAlertMethod(id)).join();
        }

        return this.loadAlertMethod(id);
    }

    public SecurityAlert loadAlertMethod(String id) {

        final Document document = this.collection.find(Filters.eq("_id", id)).first();

        if (document == null) {
            return null;
        }

        return Neutron.GSON.fromJson(document.toJson(), SecurityAlert.class);
    }

    public UpdateResult updateAlert(SecurityAlert securityAlert) {
        final Document document = Document.parse(Neutron.GSON.toJson(securityAlert));

        final UpdateResult updateResult = this.collection.updateOne(new Document("_id", securityAlert.getId().toString()), new Document("$set", document), new UpdateOptions().upsert(true));

        Neutron.getInstance().sendPacketAsync(new SecurityAlertUpdatePacket(securityAlert.getId().toString()));

        return updateResult;
    }
}
