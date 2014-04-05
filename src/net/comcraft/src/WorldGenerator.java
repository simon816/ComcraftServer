package net.comcraft.src;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

public class WorldGenerator {

    private long seed;
    private WorldInfo saveHandler;
    private ChunkLoader chunkLoader;
    private int worldSize; // in chunks
    private ChunkGenerator chunkGenerator;
    private boolean allowcommands;
    private File file;
    private Logger logger;
    public final boolean needsGenerating;

    public WorldGenerator(String root, Logger logger) throws IOException {
        file = new File(root, "level.info");
        if (!new File(root, "world.data").createNewFile() && file.exists()) {
            needsGenerating = false;
            return;
        }
        file.createNewFile();
        needsGenerating = true;
        this.logger = logger;
    }

    public WorldInfo generateAndSaveWorld(int worldSize, boolean isFlat, int flatLevel, boolean generateTrees, boolean allowcommands) {
        saveHandler = new WorldInfo();
        chunkLoader = new ChunkLoader(null, file.getParent());
        this.worldSize = worldSize;
        seed = System.currentTimeMillis();

        if (isFlat) {
            chunkGenerator = new ChunkGeneratorFlat(seed, flatLevel);
        } else {
            chunkGenerator = new ChunkGeneratorNormal(seed, generateTrees);
        }
        this.allowcommands = allowcommands;
        writeWorldInfo();
        writeWorldData();
        return saveHandler;
    }

    private void writeWorldInfo() {
        Player player = Player.getSpawnPlayer();
        player.setPlayerOnWorldCenter(worldSize);
        player.commandsAllowed = allowcommands;
        saveHandler.setWorldInfo(player, worldSize);
        try {
            DataOutputStream dos = new DataOutputStream(new FileOutputStream(file));
            saveHandler.writeWorldInfo(dos, player);
            dos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeWorldData() {
        chunkLoader.startSavingBlockStorage();

        for (int z = 0; z < worldSize; ++z) {
            for (int x = 0; x < worldSize; ++x) {
                chunkLoader.saveBlockStorage(chunkGenerator.generateChunk(x, z));
            }

            logger.info("Generating world " + new Float(((float) z / (worldSize - 1)) * 100).shortValue() + "%");
        }

        chunkLoader.endSavingBlockStorage();

        chunkLoader.onChunkLoaderEnd();
    }
}
