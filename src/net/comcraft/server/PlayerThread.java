package net.comcraft.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Logger;

import net.comcraft.src.NetHandler;
import net.comcraft.src.Packet;
import net.comcraft.src.PacketDisconnect;
import net.comcraft.src.PacketLogin;
import net.comcraft.src.Player;
import net.comcraft.src.Server;

public class PlayerThread implements Runnable {

    private Socket playerSock;
    private Logger logger;
    private NetHandler handler;
    private boolean running = false;
    private DataInputStream dis;
    private DataOutputStream dos;
    private ArrayList<Packet> sendQueue;
    private Player player;

    public PlayerThread(Socket playerSock, Logger logger, NetHandler handler) {
        this.playerSock = playerSock;
        this.logger = logger;
        this.handler = handler;
        sendQueue = new ArrayList<Packet>();
    }

    @Override
    public void run() {
        try {
            dis = new DataInputStream(playerSock.getInputStream());
            dos = new DataOutputStream(playerSock.getOutputStream());
        } catch (IOException e1) {
            e1.printStackTrace();
            return;
        }
        running = true;
        new Thread(new SendThread()).start();
        while (running) {
            try {
                if (!readPackets()) {
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
                disconnectPlayer("disconnect.socketIOError Server could not read packet");
                break;
            }
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        try {
            playerSock.shutdownInput();
        } catch (IOException e) {
            e.printStackTrace();
        }
        playerDisconnect();
        logger.info(player.getName() + " left the game.");

    }

    private boolean readPackets() throws IOException {
        if (!playerSock.isConnected()) {
            disconnectPlayer("disconnect.socketIOError Client hung up");
            return false;
        }
        int id = dis.read() & 0xFF;
        Packet p = Packet.getNewPacket(id);
        logger.finest("Packet recieved " + p);
        p.readData(dis);
        p.process(player, this);
        return true;
    }

    public NetHandler getHandler() {
        return handler;
    }

    private void sendPackets() throws IOException {
        if (sendQueue.isEmpty()) {
            return;
        }
        Packet p;
        dos.write(sendQueue.size());
        for (int i = 0; i < sendQueue.size(); i++) {
            p = sendQueue.get(i);
            logger.finest("Sending packet " + p);
            try {
                dos.write(p.getPacketId());
                p.writeData(dos);
            } catch (IOException e) {
                e.printStackTrace();
                logger.severe("Packet " + p + " did not successfully send");
            }
        }
        dos.flush();
        sendQueue.clear();
    }

    public void playerDisconnect() {
        running = false;
        try {
            playerSock.close();
        } catch (IOException e1) {
        }
        handler.deletePlayer(player);
    }

    public void disconnectPlayer(String reason) {
        logger.info("Disconnect Player (" + player.getName() + ") REASON: " + reason);
        sendPacket(new PacketDisconnect(reason));
    }

    public void sendPacket(Packet packet) {
        sendQueue.add(packet);
    }

    private class SendThread implements Runnable {

        @Override
        public void run() {
            while (running) {
                try {
                    sendPackets();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    disconnectPlayer("disconnect.socketIOError Server could not send packet");
                    break;
                }
            }
            try {
                playerSock.shutdownOutput();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void handleLogin(PacketLogin packetLogin, Player player) {
        this.player = player;
        logger.info(player.getName() + "[" + playerSock.getRemoteSocketAddress() + "] logged in at (" + player.xPos + ", " + player.yPos + ", " + player.zPos
                + ")");
        if (packetLogin.clientCompatVer != Server.SERVER_VERSION) {
            disconnectPlayer("disconnect.kick Server version not compatible");
            return; // Bypass handleLogin - other players should NOT be alerted there is a new player
        }
        this.handler.handleLogin(player);
    }

    public void handleDisconnect(float rotationPitch, float rotationYaw) {
        player.rotationPitch = rotationPitch;
        player.rotationYaw = rotationYaw;
        running = false;
    }

    protected Player getPlayer() {
        return player;
    }
}
