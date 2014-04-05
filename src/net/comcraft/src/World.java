package net.comcraft.src;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Logger;

import net.comcraft.server.Settings;

public class World {
    private ChunkManager chunkManager;
    private ChunkLoader chunkLoader;
    private WorldInfo info;
    public final int worldSize;

    public World(String root, Settings settings, Logger logger) throws IOException {
        WorldGenerator gen = new WorldGenerator(root, logger);
        if (gen.needsGenerating) {
            info = gen.generateAndSaveWorld(settings.worldSize, settings.worldType == "FLAT", settings.flatLevel, settings.generateTrees, settings.allowcommands);
        } else {
            info = new WorldInfo();
            DataInputStream dis = new DataInputStream(new FileInputStream(new File(root, "level.info")));
            info.loadWorldInfo(dis, Player.getSpawnPlayer());
            dis.close();
        }
        worldSize = info.getWorldSize();
        chunkLoader = new ChunkLoader(this, root);
        chunkManager = new ChunkManager(chunkLoader, this);
    }

    public final WorldInfo getWorldInfo() {
        return info;
    }

    public void saveWorld() {
        chunkManager.saveAllChunks();
        onWorldEnd();
    }

    public void onWorldEnd() {
        chunkManager.onChunkProviderEnd();
    }

    public Chunk getChunkFromBlockCoords(int x, int z) {
        return getChunkFromChunkCoords(x >> 2, z >> 2);
    }

    public Chunk getChunkFromChunkCoords(int x, int z) {
        return chunkManager.getChunk(x, z);
    }

}
