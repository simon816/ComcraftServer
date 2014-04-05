package net.comcraft.src;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.JList;

import net.comcraft.server.ServerThread;
import net.comcraft.server.Settings;

public class Server implements NetHandler {
    Logger logger;
    private Settings settings;
    private ServerThread server = null;
    private World world;
    private ArrayList<Player> players;
    private String root;
    private ArrayList<JList<String>> pListObserver = new ArrayList<JList<String>>();
    public ModLoader modLoader;
    public static final String version = "0.6";
    public static final short SERVER_VERSION = 2;

    public Server(Logger logger) {
        this.logger = logger;
        modLoader = new ModLoader();
    }

    public boolean start() {
        players = new ArrayList<Player>();
        logger.info("Attempting to start Comcraft Server (Version " + (SERVER_VERSION / 10) + "." + SERVER_VERSION + ")");
        root = System.getProperty("user.dir");
        try {
            settings = new Settings(root);
        } catch (IOException e) {
            e.printStackTrace();
            logger.severe("Unable to load properties file. Error: " + e.getMessage());
            return false;
        }
        settings.loadSettings();
        logger.fine("Loaded Settings");
        modLoader.initMods(root, this);
        File pDir = new File(root, "players");
        if (!pDir.exists()) {
            if (!pDir.mkdirs()) {
                logger.severe("Could not create players directory");
                return false;
            }
        }
        logger.info("Starting server on " + settings.ip + ":" + settings.port);
        server = new ServerThread(logger, settings.ip, settings.port, this);
        try {
            world = new World(root, settings, logger);
        } catch (IOException e) {
            e.printStackTrace();
            logger.severe("Failed to load world. Error: " + e.getMessage());
            return false;
        }
        new Thread(server).start();
        return true;
    }

    public void stop() {
        logger.info("Attempting to stop server");
        server.stop();
        world.saveWorld();
    }

    public boolean isRunning() {
        if (server == null) {
            return false;
        }
        return server.isRunning();
    }

    @Override
    public void handleBlockChange(Player fromPlayer, PacketBlockChange packetBlockChange) {
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            if (!player.equals(fromPlayer)) {
                player.sendPacket(packetBlockChange);
            }
        }
        Chunk chunk = world.getChunkFromChunkCoords(packetBlockChange.chunkX, packetBlockChange.chunkZ);
        chunk.setBlockIDWithMetadata(packetBlockChange.x, packetBlockChange.y, packetBlockChange.z, packetBlockChange.id, packetBlockChange.metadata);
    }

    @Override
    public void handleChunkData(PacketChunkData p) {
        p.setChunk(world.getChunkFromChunkCoords(p.x, p.z));
    }

    @Override
    public void handleWorldInfo(PacketWorldInfo p) {
        p.setWorld(world);
        p.setPlayerList(players.toArray(new Player[players.size()]));
    }

    @Override
    public void addPlayer(Player player) {
        player.loadPlayer(root, world.getWorldInfo().getVersion());
        players.add(player);
        String[] s = new String[players.size()];
        for (int p = 0; p < players.size(); p++) {
            s[p] = players.get(p).getName();
        }
        for (int i = 0; i < pListObserver.size(); i++) {
            pListObserver.get(i).setListData(s);
        }
    }

    @Override
    public void deletePlayer(Player player) {
        System.out.println("deletePlayer " + player.getName());
        try {
            player.savePlayer(root, world.getWorldInfo().getVersion());
        } catch (IOException e) {
            e.printStackTrace();
            logger.severe("Unable to save player data");
        }
        players.remove(player);
        server.removePlayer(player.getThread(this));
        sendDataToAllOtherPlayers(player, PacketPlayerData.ACTION_QUIT);
        String[] s = new String[players.size()];
        for (int p = 0; p < players.size(); p++) {
            s[p] = players.get(p).getName();
        }
        for (int i = 0; i < pListObserver.size(); i++) {
            pListObserver.get(i).setListData(s);
        }
    }

    @Override
    public void handleLogin(Player player) {
        ModAPI.event.runEvent("Player.Join");
        sendDataToAllOtherPlayers(player, PacketPlayerData.ACTION_JOIN);
    }

    private void sendDataToAllOtherPlayers(Player causedBy, int action) {
        if (isRunning()) {
            for (int i = 0; i < players.size(); i++) {
                if (players.get(i) != causedBy) {
                    players.get(i).sendPacket(new PacketPlayerData(causedBy, action));
                }
            }
        }
    }

    public void addPlayerListObserver(JList<String> list) {
        pListObserver.add(list);
    }

    @Override
    public void handlePlayerMove(Player player, Vec3D data) {
        player.xPos = data.x;
        player.yPos = data.y;
        player.zPos = data.z;
        sendDataToAllOtherPlayers(player, PacketPlayerData.ACTION_MOVE);
    }

    @Override
    public World getWorld() {
        return world;
    }
}