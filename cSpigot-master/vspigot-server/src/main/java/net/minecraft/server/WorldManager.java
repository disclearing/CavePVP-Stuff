package net.minecraft.server;

public class WorldManager implements IWorldAccess {

    private MinecraftServer server;
    public WorldServer world; // CraftBukkit - private -> public

    public WorldManager(MinecraftServer minecraftserver, WorldServer worldserver) {
        this.server = minecraftserver;
        this.world = worldserver;
    }

    public void a(String s, double d0, double d1, double d2, double d3, double d4, double d5) {}

    public void a(Entity entity) {
        this.world.getTracker().track(entity);
    }

    public void b(Entity entity) {
        this.world.getTracker().untrackEntity(entity);
    }

    public void a(String s, double d0, double d1, double d2, float f, float f1) {
        // CraftBukkit - this.world.dimension
        this.world.playerMap.sendPacketNearby(null, d0, d1, d2, f > 1.0F ? (double) (16.0F * f) : 16.0D, new PacketPlayOutNamedSoundEffect(s, d0, d1, d2, f, f1));
    }

    public void a(EntityHuman entityhuman, String s, double d0, double d1, double d2, float f, float f1) {
        // CraftBukkit - this.world.dimension
        this.world.playerMap.sendPacketNearby((EntityPlayer) entityhuman, d0, d1, d2, f > 1.0F ? (double) (16.0F * f) : 16.0D, new PacketPlayOutNamedSoundEffect(s, d0, d1, d2, f, f1)); // MineHQ
    }

    public void a(int i, int j, int k, int l, int i1, int j1) {}

    public void a(int i, int j, int k) {
        this.world.getPlayerChunkMap().flagDirty(i, j, k);
    }

    public void b(int i, int j, int k) {}

    public void a(String s, int i, int j, int k) {}

    public void a(EntityHuman entityhuman, int i, int j, int k, int l, int i1) {
        // CraftBukkit - this.world.dimension
        this.world.playerMap.sendPacketNearby((EntityPlayer) entityhuman, (double) j, (double) k, (double) l, 64.0D, new PacketPlayOutWorldEvent(i, j, k, l, i1, false)); // MineHQ
    }

    public void a(int i, int j, int k, int l, int i1) {
        this.server.getPlayerList().sendAll(new PacketPlayOutWorldEvent(i, j, k, l, i1, true));
    }

    public void b(int i, int j, int k, int l, int i1) {
        // MineHQ start - PlayerMap
        Entity entity = this.world.getEntity(i);
        EntityPlayer player = entity instanceof EntityPlayer ? (EntityPlayer) entity : null;
        this.world.playerMap.sendPacketNearby(player, j, k, l, 32.0D, new PacketPlayOutBlockBreakAnimation(i, j, k, l, i1));
        // MineHQ end
    }

    public void b() {}
}
