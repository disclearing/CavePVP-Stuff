package net.minecraft.server;

import net.valorhcf.ChunkSectionSnapshot;

import java.util.Arrays;

public class ChunkSection {

    private int yPos;
    private int nonEmptyBlockCount;
    private int tickingBlockCount;
    private byte[] blockIds;
    // private NibbleArray extBlockIds; // MineHQ - 1.7 has no extended block IDs
    private NibbleArray blockData;
    private NibbleArray emittedLight;
    private NibbleArray skyLight;
    // CraftBukkit start - Compact storage
    private int compactId;
    // private byte compactExtId; // MineHQ
    private byte compactData;
    private byte compactEmitted;
    private byte compactSky;
    public boolean isDirty = false;

    // Pre-generated (read-only!) NibbleArrays for every possible value, used for chunk saving
    private static NibbleArray[] compactPregen = new NibbleArray[16];
    static {
        for (int i = 0; i < 16; i++) {
            compactPregen[i] = expandCompactNibble((byte) i);
        }
    }

    private static NibbleArray expandCompactNibble(byte value) {
        byte[] data = new byte[2048];
        Arrays.fill(data, (byte) (value | (value << 4)));
        return new NibbleArray(data, 4);
    }

    private boolean canBeCompact(byte[] array) {
        byte value = array[0];
        for (int i = 1; i < array.length; i++) {
            if (value != array[i]) {
                return false;
            }
        }

        return true;
    }
    // CraftBukkit end

    public ChunkSection(int i, boolean flag) {
        this.yPos = i;
        /* CraftBukkit - Start as null, using compact storage
        this.blockIds = new byte[4096];
        this.blockData = new NibbleArray(this.blockIds.length, 4);
        this.emittedLight = new NibbleArray(this.blockIds.length, 4);
        if (flag) {
            this.skyLight = new NibbleArray(this.blockIds.length, 4);
        }
        */
        if (!flag) {
            this.compactSky = -1;
        }
        // CraftBukkit end
    }

    // CraftBukkit start
    public ChunkSection(int y, boolean flag, byte[] blkIds, byte[] extBlkIds) {
        this.yPos = y;
        this.setIdArray(blkIds);
        // MineHQ start - 1.7 has no extended block IDs
        /*
        if (extBlkIds != null) {
            this.setExtendedIdArray(new NibbleArray(extBlkIds, 4));
        }
        */
        // MineHQ end
        if (!flag) {
            this.compactSky = -1;
        }
        this.recalcBlockCounts();
    }
    // CraftBukkit end

    public Block getTypeId(int i, int j, int k) {
        // CraftBukkit start - Compact storage
        if (this.blockIds == null) {
            // MineHQ start - 1.7 has no extended block IDs
            /*
            int id = this.compactId;
            if (this.extBlockIds == null) {
                id |= this.compactExtId << 8;
            } else {
                id |= this.extBlockIds.a(i, j, k) << 8;
            }

            return Block.getById(id);
            */
            return Block.getById(this.compactId);
            // MineHQ end
        }
        // CraftBukkit end

        int l = this.blockIds[j << 8 | k << 4 | i] & 255;

        // MineHQ start
        /*
        if (this.extBlockIds != null) {
            l |= this.extBlockIds.a(i, j, k) << 8;
        }
        */
        // MineHQ end
        return Block.getById(l);
    }

    public void setTypeId(int i, int j, int k, Block block) {
        // CraftBukkit start - Compact storage
        Block block1 = this.getTypeId(i, j, k);
        if (block == block1) {
            return;
        }
        // CraftBukkit end

        if (block1 != Blocks.AIR) {
            --this.nonEmptyBlockCount;
            if (block1.isTicking()) {
                --this.tickingBlockCount;
            }
        }

        if (block != Blocks.AIR) {
            ++this.nonEmptyBlockCount;
            if (block.isTicking()) {
                ++this.tickingBlockCount;
            }
        }

        int i1 = Block.getId(block);

        // CraftBukkit start - Compact storage
        if (this.blockIds == null) {
            this.blockIds = new byte[4096];
            Arrays.fill(this.blockIds, (byte) (this.compactId & 255));
        }
        // CraftBukkit end

        this.blockIds[j << 8 | k << 4 | i] = (byte) (i1 & 255);
        // MineHQ start
        isDirty = true;
        /*
        if (i1 > 255) {
            if (this.extBlockIds == null) {
                this.extBlockIds = expandCompactNibble(this.compactExtId); // CraftBukkit - Compact storage
            }

            this.extBlockIds.a(i, j, k, (i1 & 3840) >> 8);
        } else if (this.extBlockIds != null) {
            this.extBlockIds.a(i, j, k, 0);
        }
        */
        // MineHQ end
    }

    public int getData(int i, int j, int k) {
        // CraftBukkit start - Compact storage
        if (this.blockData == null) {
            return this.compactData;
        }
        // CraftBukkit end
        return this.blockData.a(i, j, k);
    }

    public void setData(int i, int j, int k, int l) {
        // CraftBukkit start - Compact storage
        if (this.blockData == null) {
            if (this.compactData == l) {
                return;
            }
            this.blockData = expandCompactNibble(this.compactData);
        }
        // CraftBukkit end
        isDirty = true; // MineHQ
        this.blockData.a(i, j, k, l);
    }

    public boolean isEmpty() {
        return this.nonEmptyBlockCount == 0;
    }

    public boolean shouldTick() {
        return this.tickingBlockCount > 0;
    }

    public int getYPosition() {
        return this.yPos;
    }

    public void setSkyLight(int i, int j, int k, int l) {
        // CraftBukkit start - Compact storage
        if (this.skyLight == null) {
            if (this.compactSky == l) {
                return;
            }
            this.skyLight = expandCompactNibble(this.compactSky);
        }
        // CraftBukkit end
        isDirty = true; // MineHQ
        this.skyLight.a(i, j, k, l);
    }

    public int getSkyLight(int i, int j, int k) {
        // CraftBukkit start - Compact storage
        if (this.skyLight == null) {
            return this.compactSky;
        }
        // CraftBukkit end
        return this.skyLight.a(i, j, k);
    }

    public void setEmittedLight(int i, int j, int k, int l) {
        // CraftBukkit start - Compact storage
        if (this.emittedLight == null) {
            if (this.compactEmitted == l) {
                return;
            }
            this.emittedLight = expandCompactNibble(this.compactEmitted);
        }
        // CraftBukkit end
        isDirty = true; // MineHQ
        this.emittedLight.a(i, j, k, l);
    }

    public int getEmittedLight(int i, int j, int k) {
        // CraftBukkit start - Compact storage
        if (this.emittedLight == null) {
            return this.compactEmitted;
        }
        // CraftBukkit end
        return this.emittedLight.a(i, j, k);
    }

    public void recalcBlockCounts() {
        // CraftBukkit start - Optimize for speed
        int cntNonEmpty = 0;
        int cntTicking = 0;

        // MineHQ start - 1.7 has no extended block IDs
        if (this.blockIds == null) {
            int id = this.compactId;
            if (id > 0) {
                Block block = Block.getById(id);
                if (block == null) {
                    this.compactId = 0;
                } else {
                    cntNonEmpty = 4096;
                    if (block.isTicking()) {
                        cntTicking = 4096;
                    }
                }
            }
        } else {
            byte[] blkIds = this.blockIds;
            for (int off = 0; off < blkIds.length; off++) {
                int l = blkIds[off] & 0xFF;
                if (l > 0) {
                    if (Block.getById(l) == null) {
                        blkIds[off] = 0;
                    } else {
                        ++cntNonEmpty;
                        if (Block.getById(l).isTicking()) {
                            ++cntTicking;
                        }
                    }
                }
            }
        }
        // MineHQ end
        this.nonEmptyBlockCount = cntNonEmpty;
        this.tickingBlockCount = cntTicking;
    }

    public void old_recalcBlockCounts() {
        // CraftBukkit end
        this.nonEmptyBlockCount = 0;
        this.tickingBlockCount = 0;

        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                for (int k = 0; k < 16; ++k) {
                    Block block = this.getTypeId(i, j, k);

                    if (block != Blocks.AIR) {
                        ++this.nonEmptyBlockCount;
                        if (block.isTicking()) {
                            ++this.tickingBlockCount;
                        }
                    }
                }
            }
        }
    }

    public byte[] getIdArray() {
        // CraftBukkit start - Compact storage
        if (this.blockIds == null) {
            byte[] ids = new byte[4096];
            Arrays.fill(ids, (byte) (this.compactId & 255));
            return ids;
        }
        // CraftBukkit end
        return this.blockIds;
    }

    // MineHQ start - 1.7 has no extended block IDs
    /*
    public NibbleArray getExtendedIdArray() {
        // CraftBukkit start - Compact storage
        if (this.extBlockIds == null && this.compactExtId != 0) {
            return compactPregen[this.compactExtId];
        }
        // CraftBukkit end
        return this.extBlockIds;
    }
    */
    // MineHQ end

    public NibbleArray getDataArray() {
        // CraftBukkit start - Compact storage
        if (this.blockData == null) {
            return compactPregen[this.compactData];
        }
        // CraftBukkit end
        return this.blockData;
    }

    public NibbleArray getEmittedLightArray() {
        // CraftBukkit start - Compact storage
        if (this.emittedLight == null) {
            return compactPregen[this.compactEmitted];
        }
        // CraftBukkit end
        return this.emittedLight;
    }

    public NibbleArray getSkyLightArray() {
        // CraftBukkit start - Compact storage
        if (this.skyLight == null && this.compactSky != -1) {
            return compactPregen[this.compactSky];
        }
        // CraftBukkit end
        return this.skyLight;
    }

    public void setIdArray(byte[] abyte) {
        // CraftBukkit start - Compact storage
        if (abyte == null) {
            this.compactId = 0;
            this.blockIds = null;
            return;
        } else if (canBeCompact(abyte)) {
            this.compactId = abyte[0] & 255;
            return;
        }
        // CraftBukkit end
        this.blockIds = this.validateByteArray(abyte); // CraftBukkit - Validate data
    }

    // MineHQ start - 1.7 has no extended block IDs
    /*
    public void setExtendedIdArray(NibbleArray nibblearray) {
        // CraftBukkit start - Compact storage
        if (nibblearray == null) {
            this.compactExtId = 0;
            this.extBlockIds = null;
            return;
        } else if (canBeCompact(nibblearray.a)) {
            this.compactExtId = (byte) (nibblearray.a(0, 0, 0) & 0xF);
            return;
        }
        // CraftBukkit end
        this.extBlockIds = this.validateNibbleArray(nibblearray); // CraftBukkit - Validate data
    }
    */
    // MineHQ end

    public void setDataArray(NibbleArray nibblearray) {
        // CraftBukkit start - Compact storage
        if (nibblearray == null) {
            this.compactData = 0;
            this.blockData = null;
            return;
        } else if (canBeCompact(nibblearray.a)) {
            this.compactData = (byte) (nibblearray.a(0, 0, 0) & 0xF);
            return;
        }
        // CraftBukkit end
        this.blockData = this.validateNibbleArray(nibblearray); // CraftBukkit - Validate data
    }

    public void setEmittedLightArray(NibbleArray nibblearray) {
        // CraftBukkit start - Compact storage
        if (nibblearray == null) {
            this.compactEmitted = 0;
            this.emittedLight = null;
            return;
        } else if (canBeCompact(nibblearray.a)) {
            this.compactEmitted = (byte) (nibblearray.a(0, 0, 0) & 0xF);
            return;
        }
        // CraftBukkit end
        this.emittedLight = this.validateNibbleArray(nibblearray); // CraftBukkit - Validate data
    }

    public void setSkyLightArray(NibbleArray nibblearray) {
        // CraftBukkit start - Compact storage
        if (nibblearray == null) {
            this.compactSky = -1;
            this.skyLight = null;
            return;
        } else if (canBeCompact(nibblearray.a)) {
            this.compactSky = (byte) (nibblearray.a(0, 0, 0) & 0xF);
            return;
        }
        // CraftBukkit end
        this.skyLight = this.validateNibbleArray(nibblearray); // CraftBukkit - Validate data
    }

    // CraftBukkit start - Validate array lengths
    private NibbleArray validateNibbleArray(NibbleArray nibbleArray) {
        if (nibbleArray != null && nibbleArray.a.length < 2048) {
            byte[] newArray = new byte[2048];
            System.arraycopy(nibbleArray.a, 0, newArray, 0, nibbleArray.a.length);
            nibbleArray = new NibbleArray(newArray, 4);
        }

        return nibbleArray;
    }

    private byte[] validateByteArray(byte[] byteArray) {
        if (byteArray != null && byteArray.length < 4096) {
            byte[] newArray = new byte[4096];
            System.arraycopy(byteArray, 0, newArray, 0, byteArray.length);
            byteArray = newArray;
        }

        return byteArray;
    }
    // CraftBukkit end

    // MineHQ start - chunk snapshot api
    public ChunkSectionSnapshot createSnapshot() {
        return new ChunkSectionSnapshot(
                nonEmptyBlockCount,
                tickingBlockCount,
                clone(blockIds),
                clone(blockData),
                clone(emittedLight),
                clone(skyLight),
                compactId,
                compactData,
                compactEmitted,
                compactSky);
    }

    public void restoreSnapshot(ChunkSectionSnapshot snap) {
        nonEmptyBlockCount = snap.getNonEmptyBlockCount();
        tickingBlockCount = snap.getTickingBlockCount();
        blockIds = clone(snap.getBlockIds());
        blockData = clone(snap.getBlockData());
        emittedLight = clone(snap.getEmittedLight());
        skyLight = clone(snap.getSkyLight());
        compactId = snap.getCompactId();
        compactData = snap.getCompactData();
        compactEmitted = snap.getCompactEmitted();
        compactSky = snap.getCompactSky();
        isDirty = true;
    }

    private static byte[] clone(byte[] array) {
        if (array != null) {
            return array.clone();
        }
        return null;
    }

    private static NibbleArray clone(NibbleArray array) {
        if (array != null) {
            return array.clone();
        }
        return null;
    }
    // MineHQ end
}
