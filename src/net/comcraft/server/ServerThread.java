package net.comcraft.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.logging.Logger;

import net.comcraft.src.NetHandler;

public class ServerThread implements Runnable {

    private ServerSocket server;
    private Logger logger;
    private int port;
    private boolean running;
    private NetHandler handler;
    private ArrayList<PlayerThread> players;

    public ServerThread(Logger logger, String ip, int port, NetHandler handler) {
        this.logger = logger;
        this.port = port;
        this.handler = handler;
        players = new ArrayList<PlayerThread>();
    }

    @Override
    public void run() {
        try {
            server = new ServerSocket(port);
            server.setReuseAddress(true);
            running = true;
        } catch (IOException e) {
            e.printStackTrace();
            logger.severe("Unable to start server. Error: " + e.getMessage());
            return;
        }
        try {
            while (running) {
                PlayerThread player = new PlayerThread(server.accept(), logger, handler);
                players.add(player);
                new Thread(player).start();
            }
        } catch (IOException e) {
            if (running) {
                // Unplanned event
                logger.severe("Force stop server");
                e.printStackTrace();
                stop();
            }
        }
    }

    public void stop() {
        running = false;
        int size = players.size();
        System.out.println("There are " + size + " players");
        for (int i = 0; i < size; i++) {
            PlayerThread player = players.get(0);
            player.disconnectPlayer("disconnect.kick Server stopping");
            player.getHandler().deletePlayer(player.getPlayer());
        }
        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("Server Stopped");
    }

    public void removePlayer(PlayerThread playerThread) {
        if (players.contains(playerThread)) {
            players.remove(playerThread);
        }
    }

    public boolean isRunning() {
        return running;
    }

}
