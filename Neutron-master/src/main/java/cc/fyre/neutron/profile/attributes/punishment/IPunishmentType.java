package cc.fyre.neutron.profile.attributes.punishment;

public interface IPunishmentType {

    String getReadable();
    String getExecutedContext();
    @Deprecated String getPardonedContext();

}
