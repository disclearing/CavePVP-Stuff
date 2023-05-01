package net.minecraft.server;

public class PacketPlayOutEntityLook extends PacketPlayOutEntity {

    private boolean onGround; // Spigot - protocol patch

    public PacketPlayOutEntityLook() {
        this.g = true;
    }

    public PacketPlayOutEntityLook(int i, byte b0, byte b1, boolean onGround) { // Spigot - protocol patch
        super(i);
        this.e = b0;
        this.f = b1;
        this.g = true;
        this.onGround = onGround; // Spigot - protocol patch
    }

    public void a(PacketDataSerializer packetdataserializer) {
        super.a(packetdataserializer);
        this.e = packetdataserializer.readByte();
        this.f = packetdataserializer.readByte();
    }

    public void b(PacketDataSerializer packetdataserializer) {
        super.b(packetdataserializer);
        packetdataserializer.writeByte(this.e);
        packetdataserializer.writeByte(this.f);
        // Spigot start - protocol patch
        if ( packetdataserializer.version >= 22 )
        {
            packetdataserializer.writeBoolean( onGround );
        }
        // Spigot end
    }

    public String b() {
        return super.b() + String.format(", yRot=%d, xRot=%d", new Object[] { Byte.valueOf(this.e), Byte.valueOf(this.f)});
    }

    public void handle(PacketListener packetlistener) {
        super.a((PacketPlayOutListener) packetlistener);
    }
}
