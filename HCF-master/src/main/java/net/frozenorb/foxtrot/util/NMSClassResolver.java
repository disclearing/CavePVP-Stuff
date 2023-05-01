package net.frozenorb.foxtrot.util;

public class NMSClassResolver extends ClassResolver
{
    @Override
    public Class resolve(final String... names) throws ClassNotFoundException {
        for (int i = 0; i < names.length; ++i) {
            if (!names[i].startsWith("net.minecraft.server")) {
                names[i] = "net.minecraft.server." + Minecraft.getVersion() + names[i];
            }
        }
        return super.resolve(names);
    }
}
