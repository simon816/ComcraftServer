package net.comcraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.comcraft.server.PlayerThread;

public class PacketBlockChange extends Packet {

    public int x;
    public int y;
    public int z;
    public int metadata;
    public int id;
    public int chunkX;
    public int chunkZ;

    public PacketBlockChange() {
    }

    public PacketBlockChange(int chunkX, int chunkZ, int x, int y, int z, int id, int metadata) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.x = x;
        this.y = y;
        this.z = z;
        this.id = id;
        this.metadata = metadata;
    }

    public void writeData(DataOutputStream dos) throws IOException {
        byte[] data = new byte[] { (byte) chunkX, (byte) chunkZ, (byte) x, (byte) y, (byte) z, (byte) id, (byte) metadata };
        dos.write(data);
    }

    public void readData(DataInputStream dis) throws IOException {
        chunkX = dis.read();
        chunkZ = dis.read();
        x = dis.read();
        y = dis.read();
        z = dis.read();
        id = dis.read() & 0xFF;
        metadata = dis.read() & 0xFF;
    }

    public void process(Player player, PlayerThread playerThread) {
        playerThread.getHandler().handleBlockChange(player, this);
    }
}
