package cc.fyre.neutron.profile.attributes.punishment;

import lombok.AllArgsConstructor;
import lombok.Getter;
@AllArgsConstructor @Getter
public enum  Punishments {
    BAN("Ban","banned","unbanned", IPunishment.Type.REMOVE_ABLE),
    MUTE("Mute","muted","unmuted", IPunishment.Type.REMOVE_ABLE),
    BLACKLIST("Blacklist","blacklisted","unblacklisted", IPunishment.Type.REMOVE_ABLE),
    WARN("Warn", "warned", null, IPunishment.Type.NORMAL),
    KICK("Kick", "kicked", null, IPunishment.Type.NORMAL);
    private String readable;
    private String executedContext;
    private String pardonedContext;
    private IPunishment.Type type;
}
