package net.valorhcf.pathsearch;

import net.minecraft.server.Entity;
import net.minecraft.server.IBlockAccess;
import net.minecraft.server.PathPoint;
import net.minecraft.server.Pathfinder;

public class AsyncPathfinder extends Pathfinder {

    private IBlockAccess iblockaccess;

    public AsyncPathfinder(IBlockAccess iblockaccess, boolean flag, boolean flag1, boolean flag2, boolean flag3) {
        super(iblockaccess, flag, flag1, flag2, flag3);
        this.iblockaccess = iblockaccess;
    }

    @Override
    public int a(Entity entity, int i, int j, int k, PathPoint pathpoint) {
        return this.a(this.iblockaccess, entity, i, j, k, pathpoint);
    }
}
