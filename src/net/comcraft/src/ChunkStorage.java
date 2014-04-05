package net.comcraft.src;

public class ChunkStorage {

    private byte[] blockIDArray;
    private byte[] blockMetadataArray;
    public boolean containsBlocks;

    public ChunkStorage() {
        blockIDArray = new byte[64];
        blockMetadataArray = new byte[64];
    }

    public int getBlockID(int x, int y, int z) {
        return (int) blockIDArray[x + (y << 2) + (z << 4)] & 0xFF;
    }

    public void setBlockID(int x, int y, int z, int id) {
        if (id != 0) {
            containsBlocks = true;
        }
        blockIDArray[x + (y << 2) + (z << 4)] = (byte) id;
    }

    public int getBlockMetadata(int x, int y, int z) {
        return (int) blockMetadataArray[x + (y << 2) + (z << 4)];
    }

    public void setBlockMetadata(int x, int y, int z, int metadata) {
        blockMetadataArray[x + (y << 2) + (z << 4)] = (byte) metadata;
    }

    public byte[] getBlockIDArray() {
        return blockIDArray;
    }

    public void setBlockIDArray(byte[] data) {
        blockIDArray = data;
    }

    public byte[] getBlockMetadataArray() {
        return blockMetadataArray;
    }

    public void setBlockMetadataArray(byte[] data) {
        blockMetadataArray = data;
    }

    public void initBlockStorage() {
        containsBlocks = false;
        for (int i = 0; i < 64; i++)
            if (blockIDArray[i] != 0) {
                containsBlocks = true;
            }
    }
}
