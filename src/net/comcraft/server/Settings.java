package net.comcraft.server;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class Settings {

    private File file;
    private Properties properties;
    public String ip = "127.0.0.1";
    public int port = 9999;
    public int worldSize = 16;
    public String worldType = "NORMAL";
    public int flatLevel = 12;
    public boolean generateTrees = false;
    public boolean allowcommands = false;

    public Settings(String root) throws IOException {
        file = new File(root, "server.properties");
        if (!file.exists()) {
            file.createNewFile();
        }
        FileReader fileRead = new FileReader(file);
        properties = new Properties();
        properties.load(fileRead);
        fileRead.close();
        defaults();
    }
    public void loadSettings() {
        ip = properties.getProperty("ip");
        port = Integer.parseInt(properties.getProperty("port"));
        worldSize = Integer.parseInt(properties.getProperty("worldSize"));
        worldType = properties.getProperty("worldType");
        flatLevel = Integer.parseInt(properties.getProperty("flatLevel"));
        generateTrees = Boolean.parseBoolean(properties.getProperty("generateTrees"));
        allowcommands = Boolean.parseBoolean(properties.getProperty("allowcommands"));
    }

    private void defaults() throws IOException {
        boolean needSaveFlag = false;
        if (!properties.containsKey("ip")) {
            properties.setProperty("ip", ip);
            needSaveFlag = true;
        }
        if (!properties.containsKey("port")) {
            properties.setProperty("port", new Integer(port).toString());
            needSaveFlag = true;
        }
        if (!properties.containsKey("worldSize")) {
            properties.setProperty("worldSize", new Integer(worldSize).toString());
            needSaveFlag = true;
        }
        if (!properties.containsKey("worldType")) {
            properties.setProperty("worldType", worldType);
            needSaveFlag = true;
        }
        if (!properties.containsKey("flatLevel")) {
            properties.setProperty("flatLevel", new Integer(flatLevel).toString());
            needSaveFlag = true;
        }
        if (!properties.containsKey("generateTrees")) {
            properties.setProperty("generateTrees", new Boolean(generateTrees).toString());
            needSaveFlag = true;
        }
        if (!properties.containsKey("allowcommands")) {
            properties.setProperty("allowcommands", new Boolean(allowcommands).toString());
            needSaveFlag = true;
        }
        try {
            Integer.parseInt(properties.getProperty("port"));
        } catch (NumberFormatException e) {
            properties.setProperty("port", new Integer(port).toString());
            needSaveFlag = true;
        }
        try {
            Integer.parseInt(properties.getProperty("worldSize"));
        } catch (NumberFormatException e) {
            properties.setProperty("worldSize", new Integer(worldSize).toString());
            needSaveFlag = true;
        }
        try {
            Integer.parseInt(properties.getProperty("flatLevel"));
        } catch (NumberFormatException e) {
            properties.setProperty("flatLevel", new Integer(flatLevel).toString());
            needSaveFlag = true;
        }
        if (needSaveFlag) {
            FileWriter fileWrite = new FileWriter(file);
            properties.store(fileWrite, null);
            fileWrite.close();
        }
    }

}
