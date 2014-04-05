package net.comcraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.comcraft.server.PlayerThread;

public class PacketWorldInfo extends Packet {

    private World world;
    private Player player;
    private Player[] players;

    public PacketWorldInfo() {
    }

    public void writeData(DataOutputStream dos) throws IOException {
        int size = 24 + player.getDataSize();
        dos.writeInt(size);
        world.getWorldInfo().writeWorldInfo(dos, player);
        dos.write(players.length - 1);
        for (int i = 0; i < players.length; i++) {
            if (!players[i].equals(player)) {
                dos.writeInt(players[i].getId());
                dos.writeFloat(players[i].xPos);
                dos.writeFloat(players[i].yPos);
                dos.writeFloat(players[i].zPos);
            }
        }
    }

    public void readData(DataInputStream dis) throws IOException {
    }

    public void process(Player player, PlayerThread playerThread) {
        this.player = player;
        playerThread.getHandler().handleWorldInfo(this);
        playerThread.sendPacket(this);

    }

    public void setWorld(World world) {
        this.world = world;
    }

    public void setPlayerList(Player[] players) {
        this.players = players;
    }

}
