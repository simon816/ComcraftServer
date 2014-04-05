package net.comcraft.server;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import net.comcraft.src.Server;

public class Main {

    private static Logger logger;

    public static void main(String[] args) {
        logger = Logger.getLogger(Main.class.getName());
        try {
            FileHandler handler = new FileHandler("server.log", true);
            handler.setFormatter(new LogFormatter());
            logger.addHandler(handler);
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.setLevel(Level.INFO);
        if (args.length == 0) {
            startGui();
        } else if (args[0].equals("-nogui")) {
            startConsole(args);
        } else {
            System.out.println("Usage:");
            System.out.println("ComcraftServer.jar -nogui");
            System.out.println("OR: No arguments starts GUI");
            return;
        }
    }

    private static void startConsole(String[] args) {
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.ALL);
        logger.addHandler(handler);
        Server server = new Server(logger);
        server.start();
    }

    private static void startGui() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                UIManager.put("swing.boldMetal", Boolean.FALSE);
                ServerGui.makeWindow(new Server(logger), logger);
                // new ServerGUI(logger);
            }
        });

    }

}
