package net.comcraft.src;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import net.comcraft.server.PlayerThread;

public class PacketChunkData extends Packet {
    public int x;
    public int z;
    private ChunkStorage[] storageArray;

    public void writeData(DataOutputStream dos) throws IOException {
        dos.write(x);
        dos.write(z);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream gzos = new GZIPOutputStream(baos);
        for (int i = 0; i < storageArray.length; i++) {
            byte[] ids = storageArray[i].getBlockIDArray();
            gzos.write(ids);
            gzos.write(storageArray[i].getBlockMetadataArray());
        }
        gzos.finish();
        gzos.close();
        byte[] arr = baos.toByteArray();
        dos.writeInt(arr.length);
        dos.write(arr);
    }

    public void readData(DataInputStream dis) throws IOException {
        x = dis.read();
        z = dis.read();
    }

    @Override
    public void process(Player player, PlayerThread playerThread) {
        playerThread.getHandler().handleChunkData(this);
        // setChunk called between these methods
        playerThread.sendPacket(this);
    }

    public void setChunk(Chunk chunk) {
        storageArray = chunk.getBlockStorageArray();
    }
}
