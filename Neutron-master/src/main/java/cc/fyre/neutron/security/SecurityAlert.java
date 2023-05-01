package cc.fyre.neutron.security;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class SecurityAlert {
    @Getter @SerializedName("_id") @Setter private UUID id;
    @Getter @Setter private UUID target;
    @Getter @Setter private UUID victim;
    @Getter @Setter private String server;
    @Getter @Setter private long timeAt;
    @Getter @Setter private AlertType alertType;
    @Getter @Setter private List<String> description;
    @Getter @Setter private boolean urgent;
}
