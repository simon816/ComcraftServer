package net.comcraft.src;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Hashtable;

public class ChunkLoader {
    private World world;
    private FileInputStream fis;
    private DataOutputStream dos;
    private File file;
    private int currenPosition;
    private boolean readWriteFlag;
    private boolean isAlive;

    public ChunkLoader(World world, String root) {
        this.world = world;
        isAlive = true;
        file = new File(root, "world.data");
    }

    public void onChunkLoaderEnd() {
        isAlive = false;

        while (readWriteFlag) {
            try {
                Thread.sleep(200L);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        try {
            if (fis != null) {
                fis.close();
            }

            if (dos != null)
                dos.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public Chunk loadChunk(int x, int z) {
        return readChunkFromFile(x, z);
    }

    public void saveChunks(Hashtable<Integer, Chunk> chunksList) {
        writeChunksToFile(chunksList);
    }

    private Chunk readChunkFromFile(int x, int z) {
        if (!isAlive) {
            return null;
        }

        readWriteFlag = true;

        int bytesToSkip = (x + z * world.worldSize) * 1024;
        System.out.println("bytesToSkip=" + bytesToSkip);

        if ((fis == null) || (currenPosition > bytesToSkip)) {
            try {
                if (fis != null) {
                    fis.close();
                }

                fis = new FileInputStream(file);
                currenPosition = 0;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        try {
            fis.skip(bytesToSkip - currenPosition);

            currenPosition = (bytesToSkip + 1024);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        Chunk chunk = new Chunk(world, x, z);

        ChunkStorage[] blockStorage = new ChunkStorage[8];

        for (int i = 0; i < blockStorage.length; i++) {
            blockStorage[i] = new ChunkStorage();

            byte[] id = new byte[64];
            byte[] metadata = new byte[64];
            try {
                fis.read(id);
                fis.read(metadata);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            blockStorage[i].setBlockIDArray(id);
            blockStorage[i].setBlockMetadataArray(metadata);

            blockStorage[i].initBlockStorage();
        }

        chunk.setBlockStorageArray(blockStorage);

        readWriteFlag = false;

        return chunk;
    }

    private void writeChunksToFile(Hashtable<Integer, Chunk> chunksList) {
        if (!isAlive) {
            return;
        }

        readWriteFlag = true;
        File tmp = new File(file.getAbsolutePath() + ".tmp");
        try {
            if (fis != null) {
                fis.close();
            }
            Files.copy(file.toPath(), tmp.toPath());
            fis = new FileInputStream(tmp);
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }

        startSavingBlockStorage();

        for (int z = 0; z < world.worldSize; z++) {
            for (int x = 0; x < world.worldSize; x++) {
                Chunk chunk = chunksList.get(ChunkManager.getChunkID(x, z, world.worldSize));

                if (chunk != null) {
                    // System.out.println("Writing chunk from memory");
                    try {
                        fis.skip(1024L);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    ChunkStorage[] blockStorage = chunk.getBlockStorageArray();

                    saveBlockStorage(blockStorage);
                } else {
                    // System.out.println("Writing chunk from file");
                    byte[] data = new byte[1024];
                    try {
                        fis.read(data);
                        dos.write(data);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }

        endSavingBlockStorage();
        try {
            fis.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        fis = null;

        try {
            Files.move(tmp.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }

        readWriteFlag = false;
    }

    public void startSavingBlockStorage() {
        try {
            dos = new DataOutputStream(new FileOutputStream(file));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void saveBlockStorage(ChunkStorage[] blockStorage) {
        if (dos == null) {
            return;
        }

        for (int i = 0; i < 8; i++)
            try {
                dos.write(blockStorage[i].getBlockIDArray());
                dos.write(blockStorage[i].getBlockMetadataArray());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
    }

    public void endSavingBlockStorage() {
        try {
            dos.close();
            dos = null;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
