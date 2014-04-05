package net.comcraft.src;

import java.util.Random;

public abstract class ChunkGenerator {

    protected long seed;
    protected Random random;

    public ChunkGenerator(long seed) {
        random = new Random(seed);
        this.seed = seed;
    }

    public abstract ChunkStorage[] generateChunk(int x, int z);

}
