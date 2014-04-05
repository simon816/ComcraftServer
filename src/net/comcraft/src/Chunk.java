package net.comcraft.src;


public class Chunk {

    public final int xPos;
    public final int zPos;
    private ChunkStorage[] blockStorageArray;
    private World world;
    private boolean isEdited;

    public Chunk(World world, int chunkX, int chunkZ) {
        this.world = world;
        xPos = chunkX;
        zPos = chunkZ;
        blockStorageArray = new ChunkStorage[8];

    }

    public boolean isEdited() {
        return isEdited;
    }

    public ChunkStorage[] getBlockStorageArray() {
        return blockStorageArray;
    }

    public void setBlockStorageArray(ChunkStorage[] blockStorage) {
        blockStorageArray = blockStorage;
    }

    public boolean setBlockIDWithMetadata(int x, int y, int z, int id, int metadata) {
        isEdited = true;

        if (x == 0) {
            world.getChunkFromChunkCoords(xPos - 1, zPos);
        } else if (x == 3) {
            world.getChunkFromChunkCoords(xPos + 1, zPos);
        }

        if (z == 0) {
            world.getChunkFromChunkCoords(xPos, zPos - 1);
        } else if (z == 3) {
            world.getChunkFromChunkCoords(xPos, zPos + 1);
        }

        ChunkStorage blockStorage = blockStorageArray[y >> 2];

        if (blockStorage == null) {
            if (id == 0) {
                return false;
            }

            blockStorage = blockStorageArray[y >> 2] = new ChunkStorage();
        }

        blockStorage.setBlockID(x, y & 3, z, id);

        blockStorage.setBlockMetadata(x, y & 3, z, metadata);

        return true;
    }
}
