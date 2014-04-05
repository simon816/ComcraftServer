package net.comcraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.comcraft.server.PlayerThread;

public class PacketDisconnect extends Packet {

    private String reason;
    private float rotationPitch;
    private float rotationYaw;

    public PacketDisconnect() {
    }

    public PacketDisconnect(String reason) {
        this.reason = reason;
    }

    @Override
    public void writeData(DataOutputStream dos) throws IOException {
        dos.writeUTF(reason);
    }

    @Override
    public void readData(DataInputStream dis) throws IOException {
        rotationPitch = dis.readFloat();
        rotationYaw = dis.readFloat();
    }

    @Override
    public void process(Player player, PlayerThread playerThread) {
        playerThread.handleDisconnect(rotationPitch, rotationYaw);
    }

}
