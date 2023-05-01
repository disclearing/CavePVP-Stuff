package net.valorhcf.blocks;

import net.minecraft.server.Block;

public class BlockAccessCache {

    private BlockCacheEntry[] array;

    public BlockAccessCache() {
        this.array = new BlockCacheEntry[4096];
        for(int i = 0; i < this.array.length; i++) {
            this.array[i] = new PendingBlockEntry(this);
        }
    }

    public Block getById(int id) {
        if(id >= 0 && id < this.array.length) {
            return this.array[id].getById(id);
        } else if(id >= this.array.length) {
            BlockCacheEntry[] arr = new BlockCacheEntry[this.array.length * 2];
            System.arraycopy(this.array, 0, arr, 0, this.array.length);
            for(int i = this.array.length; i < arr.length; i++) {
                arr[i] = new PendingBlockEntry(this);
            }
            this.array = arr;
            return this.getById(id);
        }
        return (Block) Block.REGISTRY_BLOCKS.a(id);
    }

    private interface BlockCacheEntry {
        public Block getById(int id);
    }

    private class FinalBlockEntry implements BlockCacheEntry {

        private Block block;

        public FinalBlockEntry(Block block) {
            this.block = block;
        }

        @Override
        public Block getById(int id) {
            return block;
        }
    }

    private class PendingBlockEntry implements BlockCacheEntry {

        private BlockAccessCache cache;

        public PendingBlockEntry(BlockAccessCache cache) {
            this.cache = cache;
        }

        @Override
        public Block getById(int id) {
            Object obj = Block.REGISTRY_BLOCKS.getByIdWithoutDefaulting(id);
            if(obj != null) {
                Block block = (Block) obj;
                this.cache.updateCacheEntry(id, block);
                return block;
            }
            return (Block) Block.REGISTRY_BLOCKS.getDefaultBlock();
        }
    }

    private void updateCacheEntry(int id, Block block) {
        if(id >= 0 && id < this.array.length) {
            this.array[id] = new FinalBlockEntry(block);
        }
    }
}
