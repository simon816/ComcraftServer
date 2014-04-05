package net.comcraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.comcraft.server.PlayerThread;

public class PacketLogin extends Packet {

    private int uniqueId;
    private String username;
    public short clientCompatVer;
    public short clientAPIVer;

    @Override
    public void writeData(DataOutputStream dos) throws IOException {
    }

    @Override
    public void readData(DataInputStream dis) throws IOException {
        uniqueId = dis.readInt();
        username = dis.readUTF();
        if (username.isEmpty()) {
            username = new Integer(uniqueId).toString();
        }
        clientCompatVer = dis.readShort();
        clientAPIVer = dis.readShort();
    }

    @Override
    public void process(Player player, PlayerThread playerThread) {
        player = new Player(playerThread);
        player.setId(uniqueId, username);
        playerThread.getHandler().addPlayer(player);
        playerThread.handleLogin(this, player);
        playerThread.sendPacket(this);
    }

}
