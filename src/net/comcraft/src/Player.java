package net.comcraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.AccessControlException;

import net.comcraft.server.PlayerThread;

public class Player {
    private static final Player spawnPlayer = new Player();
    public float xPos;
    public float yPos;
    public float zPos;
    public float rotationYaw = 225;
    public float rotationPitch = 340;
    public boolean commandsAllowed = false;
    public InventoryPlayer inventory;
    private PlayerThread playerThread;
    private int uniqueId;
    private String name;

    public Player(PlayerThread playerThread) {
        this.playerThread = playerThread;
        inventory = new InventoryPlayer();
    }

    private Player() {
        inventory = new InventoryPlayer();
        xPos = 32;
        yPos = 15;
        zPos = 32;
    }

    public void setPlayerOnWorldCenter(int worldSize) {
        xPos = worldSize * 4 / 2;
        yPos = 15;
        zPos = worldSize * 4 / 2;
    }

    public void sendPacket(Packet p) {
        playerThread.sendPacket(p);
    }

    // High-risk! Reveals network to unsuspecting classes. Handle with care.
    // A required NetHandler reduces this risk.
    protected PlayerThread getThread(NetHandler handler) {
        if (handler == playerThread.getHandler()) {
            return playerThread;
        }
        throw new AccessControlException("The method cannot be accessed at this time.");
    }

    protected void setId(int uniqueId, String username) {
        this.uniqueId = uniqueId;
        this.name = username;
    }

    protected int getId() {
        return uniqueId;
    }

    public String getName() {
        return name;
    }

    public void savePlayer(String root, float worldVersion) throws IOException {
        File file = new File(root, "players/" + uniqueId + ".dat");
        if (!file.exists()) {
            file.createNewFile();
        }
        DataOutputStream dos = new DataOutputStream(new FileOutputStream(file));
        writeToDataOutputStream(dos, worldVersion);
        dos.close();
    }

    public void loadPlayer(String root, float worldVersion) {
        File file = new File(root, "players/" + uniqueId + ".dat");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            DataInputStream dis = new DataInputStream(new FileInputStream(file));
            loadFromDataInputStream(dis, worldVersion);
            dis.close();
        } catch (IOException e) {
            mergePlayer(spawnPlayer);
        }
    }

    public void mergePlayer(Player source) {
        xPos = source.xPos;
        yPos = source.yPos;
        zPos = source.zPos;
        rotationYaw = source.rotationYaw;
        rotationPitch = source.rotationPitch;
        inventory = source.inventory;
        commandsAllowed = source.commandsAllowed;
    }

    public Vec3D getPosition() {
        return new Vec3D(xPos, yPos, zPos);
    }

    public void loadFromDataInputStream(DataInputStream dis, float worldVersion) throws IOException {
        xPos = dis.readFloat();
        yPos = dis.readFloat();
        zPos = dis.readFloat();
        rotationPitch = dis.readFloat();
        rotationYaw = dis.readFloat();

        int fastSlotSize = dis.readInt();
        for (int n = 0; n < fastSlotSize; ++n) {
            int id = dis.readInt();
            int stackSize = 1;
            if (worldVersion >= 5) {
                stackSize = dis.read();
            }
            inventory.setItemStackAt(n, new InvItemStack(id, stackSize));
        }
        commandsAllowed = dis.readBoolean();
    }

    public void writeToDataOutputStream(DataOutputStream dos, float worldVersion) throws IOException {
        dos.writeFloat(xPos);
        dos.writeFloat(yPos);
        dos.writeFloat(zPos);
        dos.writeFloat(rotationPitch);
        dos.writeFloat(rotationYaw);
        dos.writeInt(inventory.getFastSlotSize());

        for (int n = 0; n < inventory.getFastSlotSize(); ++n) {
            InvItemStack stack = inventory.getItemStackAt(n);
            dos.writeInt(stack != null ? stack.itemID : 0);
            dos.write(stack != null ? stack.stackSize : 0);
        }
        dos.writeBoolean(commandsAllowed);
    }

    public static Player getSpawnPlayer() {
        return spawnPlayer;
    }

    public int getDataSize() {
        return 24 + (inventory.getFastSlotSize() * 5) + 1;
    }
}
