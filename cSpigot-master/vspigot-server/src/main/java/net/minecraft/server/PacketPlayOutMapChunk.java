package net.minecraft.server;

import net.valorhcf.ReusableByteArray;

import java.io.IOException;
import java.util.zip.Deflater;

public class PacketPlayOutMapChunk extends Packet {

    private int a;
    private int b;
    private int c;
    private int d;
    private static final ReusableByteArray bufferCache = new ReusableByteArray(164196); // MineHQ
    private byte[] f;
    private boolean g;
    private int h;
    private static byte[] i = new byte[196864];

    private World world; // MineHQ - use world instead of chunk
    private int mask; // Spigot

    public PacketPlayOutMapChunk() {}

    // Spigot start - protocol patch
    public PacketPlayOutMapChunk(Chunk chunk, boolean flag, int i, int version) {
        this.world = chunk.world;
        this.mask = i;
        this.a = chunk.locX;
        this.b = chunk.locZ;
        this.g = flag;
        // MineHQ start - don't need to do chunkmap for unload chunk packets
        if (i == 0 && this.g) {
            return;
        }
        // MineHQ end
        ChunkMap chunkmap = chunk.getChunkMap(flag, i, version); // MineHQ

        this.d = chunkmap.c;
        this.c = chunkmap.b;

        this.f = chunkmap.a;
    }

    // MineHQ start - constructor for unload chunk packets
    public static PacketPlayOutMapChunk unload(int x, int z) {
        PacketPlayOutMapChunk packet = new PacketPlayOutMapChunk();
        packet.a = x;
        packet.b = z;
        packet.g = true;
        return packet;
    }
    // MineHQ end

    public static int c() {
        return 196864;
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        // MineHQ start - this is client code
        /*
        this.a = packetdataserializer.readInt();
        this.b = packetdataserializer.readInt();
        this.g = packetdataserializer.readBoolean();
        this.c = packetdataserializer.readShort();
        this.d = packetdataserializer.readShort();
        this.h = packetdataserializer.readInt();
        if (i.length < this.h) {
            i = new byte[this.h];
        }

        packetdataserializer.readBytes(i, 0, this.h);
        int i = 0;

        int j;

        for (j = 0; j < 16; ++j) {
            i += this.c >> j & 1;
        }

        j = 12288 * i;
        if (this.g) {
            j += 256;
        }

        this.f = new byte[j];
        Inflater inflater = new Inflater();

        inflater.setInput(PacketPlayOutMapChunk.i, 0, this.h);

        try {
            inflater.inflate(this.f);
        } catch (DataFormatException dataformatexception) {
            throw new IOException("Bad compressed data format");
        } finally {
            inflater.end();
        }
        */
        // MineHQ end
    }

    public void b(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeInt(this.a);
        packetdataserializer.writeInt(this.b);
        packetdataserializer.writeBoolean(this.g);
        packetdataserializer.writeShort((short) (this.c & '\uffff'));
        // MineHQ start - don't send any data for unload chunks, the client still accepts the packets fine without it
        if (this.g && this.c == 0) {
            if (packetdataserializer.version < 27) {
                packetdataserializer.writeShort((short) (this.d & '\uffff'));
                packetdataserializer.writeInt(0);
            } else {
                packetdataserializer.b(0);
            }
            return;
        }
        // MineHQ end
        // Spigot start - protocol patch
        if ( packetdataserializer.version < 27 )
        {
            this.world.spigotConfig.antiXrayInstance.obfuscate(this.a, this.b, mask, this.f, this.world, false); // Spigot
            Deflater deflater = new Deflater(4); // Spigot
            byte[] buffer;
            try {
                deflater.setInput(this.f, 0, this.f.length);
                deflater.finish();
                buffer = bufferCache.get(this.f.length + 100);
                this.h = deflater.deflate(buffer);
            } finally {
                deflater.end();
            }
            packetdataserializer.writeShort( (short) ( this.d & '\uffff' ) );
            packetdataserializer.writeInt( this.h );
            packetdataserializer.writeBytes( buffer, 0, this.h );
        } else
        {
            this.world.spigotConfig.antiXrayInstance.obfuscate(this.a, this.b, mask, this.f, this.world, true); // Spigot
            a( packetdataserializer, this.f );
        }
        // Spigot end - protocol patch
    }

    public void a(PacketPlayOutListener packetplayoutlistener) {
        packetplayoutlistener.a(this);
    }

    public String b() {
        return String.format("x=%d, z=%d, full=%b, sects=%d, add=%d, size=%d", new Object[] { Integer.valueOf(this.a), Integer.valueOf(this.b), Boolean.valueOf(this.g), Integer.valueOf(this.c), Integer.valueOf(this.d), Integer.valueOf(this.h)});
    }

    // Spigot start - protocol patch
    public static ChunkMap a(Chunk chunk, boolean flag, int i, int version) {
        int j = 0;
        ChunkSection[] achunksection = chunk.getSections();
        int k = 0;
        ChunkMap chunkmap = new ChunkMap();
        byte[] abyte = PacketPlayOutMapChunk.i;

        if (flag) {
            chunk.q = true;
        }

        int l;

        for (l = 0; l < achunksection.length; ++l) {
            if (achunksection[l] != null && (!flag || !achunksection[l].isEmpty()) && (i & 1 << l) != 0) {
                chunkmap.b |= 1 << l;
                // MineHQ start - 1.7 has no extended block IDs
                /*
                if (achunksection[l].getExtendedIdArray() != null) {
                    chunkmap.c |= 1 << l;
                    ++k;
                }
                */
            }
        }

        if ( version < 24 )
        {
            for ( l = 0; l < achunksection.length; ++l )
            {
                if ( achunksection[ l ] != null && ( !flag || !achunksection[ l ].isEmpty() ) && ( i & 1 << l ) != 0 )
                {
                    byte[] abyte1 = achunksection[ l ].getIdArray();

                    System.arraycopy( abyte1, 0, abyte, j, abyte1.length );
                    j += abyte1.length;
                }
            }
        } else {
            for ( l = 0; l < achunksection.length; ++l )
            {
                if ( achunksection[ l ] != null && ( !flag || !achunksection[ l ].isEmpty() ) && ( i & 1 << l ) != 0 )
                {
                    byte[] abyte1 = achunksection[ l ].getIdArray();
                    NibbleArray nibblearray = achunksection[ l ].getDataArray();
                    for ( int ind = 0; ind < abyte1.length; ind++ )
                    {
                        int id = abyte1[ ind ] & 0xFF;
                        int px = ind & 0xF;
                        int py = ( ind >> 8 ) & 0xF;
                        int pz = ( ind >> 4 ) & 0xF;
                        int data = nibblearray.a( px, py, pz );
                        if ( id == 90 && data == 0 )
                        {
                            Blocks.PORTAL.updateShape( chunk.world, ( chunk.locX << 4 ) + px, ( l << 4 ) + py, ( chunk.locZ << 4 ) + pz );
                        } else
                        {
                            data = org.spigotmc.SpigotDebreakifier.getCorrectedData( id, data );
                        }
                        char val = (char) ( id << 4 | data );
                        abyte[ j++ ] = (byte) ( val & 0xFF );
                        abyte[ j++ ] = (byte) ( ( val >> 8 ) & 0xFF );
                    }
                }
            }
        }

        NibbleArray nibblearray;

        if ( version < 24 )
        {
            for ( l = 0; l < achunksection.length; ++l )
            {
                if ( achunksection[ l ] != null && ( !flag || !achunksection[ l ].isEmpty() ) && ( i & 1 << l ) != 0 )
                {
                    nibblearray = achunksection[ l ].getDataArray();
                    System.arraycopy(nibblearray.a, 0, abyte, j, nibblearray.a.length);
                    j += nibblearray.a.length;
                }
            }
        }

        for (l = 0; l < achunksection.length; ++l) {
            if (achunksection[l] != null && (!flag || !achunksection[l].isEmpty()) && (i & 1 << l) != 0) {
                nibblearray = achunksection[l].getEmittedLightArray();
                System.arraycopy(nibblearray.a, 0, abyte, j, nibblearray.a.length);
                j += nibblearray.a.length;
            }
        }

        if (!chunk.world.worldProvider.g) {
            for (l = 0; l < achunksection.length; ++l) {
                if (achunksection[l] != null && (!flag || !achunksection[l].isEmpty()) && (i & 1 << l) != 0) {
                    nibblearray = achunksection[l].getSkyLightArray();
                    System.arraycopy(nibblearray.a, 0, abyte, j, nibblearray.a.length);
                    j += nibblearray.a.length;
                }
            }
        }

        // MineHQ start - 1.7 has no extended block IDs
        /*
        if (k > 0 && version < 24) {
            for (l = 0; l < achunksection.length; ++l) {
                if (achunksection[l] != null && (!flag || !achunksection[l].isEmpty()) && achunksection[l].getExtendedIdArray() != null && (i & 1 << l) != 0) {
                    nibblearray = achunksection[l].getExtendedIdArray();
                    System.arraycopy(nibblearray.a, 0, abyte, j, nibblearray.a.length);
                    j += nibblearray.a.length;
                }
            }
        }
        */
        // MineHQ end

        if (flag) {
            byte[] abyte2 = chunk.m();

            System.arraycopy(abyte2, 0, abyte, j, abyte2.length);
            j += abyte2.length;
        }

        chunkmap.a = new byte[j];
        System.arraycopy(abyte, 0, chunkmap.a, 0, j);
        return chunkmap;
    }
    // Spigot end - protocol patch

    @Override
    public void handle(PacketListener packetlistener) {
        this.a((PacketPlayOutListener) packetlistener);
    }
}
