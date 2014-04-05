package net.comcraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class WorldInfo {

    public static final int latestVersion = 5;
    private float worldVersion;
    private float spawnX;
    private float spawnY;
    private float spawnZ;
    private int worldSize;

    public WorldInfo() {
        worldVersion = latestVersion;
    }

    public int getWorldSize() {
        return worldSize;
    }

    public void setWorldInfo(Player player, int worldSize) {
        spawnX = player.xPos;
        spawnY = player.yPos;
        spawnZ = player.zPos;
        this.worldSize = worldSize;
    }

    public void writeWorldInfo(DataOutputStream dos, Player player) throws IOException {
        dos.writeFloat(worldVersion);
        dos.writeInt(worldSize);
        dos.writeFloat(spawnX);
        dos.writeFloat(spawnY);
        dos.writeFloat(spawnZ);
        dos.writeInt(0);
        player.writeToDataOutputStream(dos, worldVersion);
    }

    public void loadWorldInfo(DataInputStream dis, Player player) throws IOException {
        worldVersion = dis.readFloat();
        worldSize = dis.readInt();
        spawnX = dis.readFloat();
        spawnY = dis.readFloat();
        spawnZ = dis.readFloat();
        if (worldVersion >= 5) {
            dis.readInt();
        }

        player.loadFromDataInputStream(dis, worldVersion);

        if (worldVersion < latestVersion) {
            worldVersion = latestVersion;
        }
    }

    public float getVersion() {
        return worldVersion;
    }
}
