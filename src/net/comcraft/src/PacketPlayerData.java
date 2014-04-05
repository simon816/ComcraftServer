package net.comcraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.comcraft.server.PlayerThread;

public class PacketPlayerData extends Packet {

    private DataInputStream dis;
    protected static final int ACTION_JOIN = 1;
    protected static final int ACTION_QUIT = 2;
    protected static final int ACTION_MOVE = 3;
    private static final int ACTION_INVENTORY = 4;
    private int action;
    private int pId;
    private Object data;
    private int itmIndex;

    public PacketPlayerData() {
    }

    public PacketPlayerData(Player player, int action) {
        data = player.getPosition();
        pId = player.getId();
        this.action = action;
    }

    @Override
    public void writeData(DataOutputStream dos) throws IOException {
        dos.write(action);
        dos.writeInt(pId);
        switch (action) {
        case ACTION_JOIN:
            dos.writeFloat(((Vec3D) data).x);
            dos.writeFloat(((Vec3D) data).y);
            dos.writeFloat(((Vec3D) data).z);
            break;
        case ACTION_MOVE:
            dos.writeFloat(((Vec3D) data).x);
            dos.writeFloat(((Vec3D) data).y);
            dos.writeFloat(((Vec3D) data).z);
            break;
        case ACTION_QUIT:
            break;
        case ACTION_INVENTORY:
            dos.write(itmIndex);
            dos.writeShort(((InvItemStack) data).itemID);
            dos.write(((InvItemStack) data).stackSize);
            break;
        default:
            break;
        }
    }

    @Override
    public void readData(DataInputStream dis) throws IOException {
        action = dis.read() & 0xFF;
        switch (action) {
        case ACTION_JOIN:
            this.dis = dis;
            break;
        case ACTION_MOVE:
            pId = dis.readInt();
            data = new Vec3D(dis.readFloat(), dis.readFloat(), dis.readFloat());
            break;
        case ACTION_INVENTORY:
            itmIndex = dis.read();
            data = new InvItemStack(dis.readShort(), dis.read());
        default:
            break;
        }
    }

    @Override
    public void process(Player player, PlayerThread playerThread) {
        switch (action) {
        case ACTION_JOIN:
            try {
                player.loadFromDataInputStream(dis, playerThread.getHandler().getWorld().getWorldInfo().getVersion());
            } catch (IOException e) {
                e.printStackTrace();
            }
            break;
        case ACTION_MOVE:
            playerThread.getHandler().handlePlayerMove(player, (Vec3D) data);
            break;
        case ACTION_INVENTORY:
            player.inventory.setItemStackAt(itmIndex, (InvItemStack) data);
            break;
        default:
            break;
        }
    }
}
