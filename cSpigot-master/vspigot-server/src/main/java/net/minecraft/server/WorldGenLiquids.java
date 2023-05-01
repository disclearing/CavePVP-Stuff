package net.minecraft.server;

import java.util.Random;

public class WorldGenLiquids extends WorldGenerator {
    private Block a;

    public WorldGenLiquids(Block var1) {
        this.a = var1;
    }

    public boolean generate(World var1, Random var2, int var3, int var4, int var5) {

        if (var1.getType(var3, var4 + 1, var5) != Blocks.STONE) {
            return false;
        } else if (var1.getType(var3, var4 - 1, var5) != Blocks.STONE) {
            return false;
        } else if (var1.getType(var3, var4, var5).getMaterial() != Material.AIR && var1.getType(var3, var4, var5) != Blocks.STONE) {
            return false;
        } else {
            int var6 = 0;
            if (var1.getType(var3 - 1, var4, var5) == Blocks.STONE) {
                ++var6;
            }

            if (var1.getType(var3 + 1, var4, var5) == Blocks.STONE) {
                ++var6;
            }

            if (var1.getType(var3, var4, var5 - 1) == Blocks.STONE) {
                ++var6;
            }

            if (var1.getType(var3, var4, var5 + 1) == Blocks.STONE) {
                ++var6;
            }

            int var7 = 0;
            if (var1.isEmpty(var3 - 1, var4, var5)) {
                ++var7;
            }

            if (var1.isEmpty(var3 + 1, var4, var5)) {
                ++var7;
            }

            if (var1.isEmpty(var3, var4, var5 - 1)) {
                ++var7;
            }

            if (var1.isEmpty(var3, var4, var5 + 1)) {
                ++var7;
            }

            if (var6 == 3 && var7 == 1) {
                var1.setTypeAndData(var3, var4, var5, this.a, 0, 2);
                var1.d = true;
                this.a.a(var1, var3, var4, var5, var2);
                var1.d = false;
            }

            return true;
        }
    }
}
