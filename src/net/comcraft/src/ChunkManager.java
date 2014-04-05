package net.comcraft.src;

import java.util.Hashtable;

public class ChunkManager {

    private Hashtable<Integer, Chunk> chunksMap;
    private Chunk[][] chunksTable;
    private World world;
    private ChunkLoader chunkLoader;

    public ChunkManager(ChunkLoader chunkLoader, World world) {
        this.world = world;
        this.chunkLoader = chunkLoader;
        chunksMap = new Hashtable<Integer, Chunk>(1024);
        chunksTable = new Chunk[world.worldSize][world.worldSize];
    }

    public Chunk getChunk(int x, int z) {
        if (x < 0 || x >= world.worldSize || z < 0 || z >= world.worldSize) {
            return null;
        }

        Chunk chunk = chunksTable[x][z];

        if (chunk == null) {
            chunk = chunksMap.get(getChunkID(x, z, world.worldSize));

            if (chunk == null) {
                chunk = loadChunk(x, z);
            }
            chunksTable[x][z] = chunk;
        }
        return chunk;
    }

    public Chunk loadChunk(int x, int z) {
        if (x < 0 || x >= world.worldSize || z < 0 || z >= world.worldSize) {
            return null;
        }

        Integer id = getChunkID(x, z, world.worldSize);

        x = getDecodedChunkX(id, world.worldSize);
        z = getDecodedChunkZ(id, world.worldSize);

        Chunk chunk = chunkLoader.loadChunk(x, z);
        chunksMap.put(getChunkID(x, z, world.worldSize), chunk);
        return chunk;

    }

    public static Integer getChunkID(int x, int z, int worldSize) {
        return new Integer(x + z * worldSize);
    }

    public static int getDecodedChunkX(Integer chunkId, int worldSize) {
        int id = chunkId.intValue();

        return id % worldSize;
    }

    public static int getDecodedChunkZ(Integer chunkId, int worldSize) {
        int id = chunkId.intValue();

        return (id - getDecodedChunkX(chunkId, worldSize)) / worldSize;
    }

    public void saveAllChunks() {
        chunkLoader.saveChunks(chunksMap);
    }

    public void onChunkProviderEnd() {
        chunkLoader.onChunkLoaderEnd();
    }

    public int getLoadedChunksNum() {
        return chunksMap.size();
    }

    public int getChunksQueueNum() {
        return 0;
    }
}
