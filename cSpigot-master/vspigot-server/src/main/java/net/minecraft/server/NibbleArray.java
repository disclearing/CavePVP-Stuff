package net.minecraft.server;

public class NibbleArray {

    public final byte[] a;

    public NibbleArray(int i, int j) {
        this.a = new byte[i >> 1];
        // this.b = j; // MineHQ
        // this.c = j + 4; // MineHQ
    }

    public NibbleArray(byte[] abyte, int i) {
        // MineHQ start
        if (abyte.length != 2048 || i != 4) {
            throw new IllegalStateException("NibbleArrays should be 2048 in length with 4 bits per nibble.");
        }
        // MineHQ end
        this.a = abyte;
    }

    public int a(int i, int j, int k) {
        // MineHQ start
        int position = j << 8 | k << 4 | i;
        return this.a[position >> 1] >> ((position & 1) << 2) & 15;
        // MineHQ end
    }

    public void a(int i, int j, int k, int l) {
        // MineHQ start
        int position = j << 8 | k << 4 | i; // MineHQ
        int shift = (position & 1) << 2;
        this.a[position >> 1] = (byte) (this.a[position >> 1] & ~(15 << shift) | (l & 15) << shift);
        // MineHQ end
    }

    // MineHQ start - chunk snapshot api
    public NibbleArray clone() {
        return new NibbleArray(a.clone(), 4);
    }
    // MineHQ end
}
