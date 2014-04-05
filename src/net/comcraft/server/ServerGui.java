package net.comcraft.server;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import net.comcraft.src.Server;

public class ServerGui extends JComponent implements ActionListener {
    private static final long serialVersionUID = 1L;
    private Server server;
    private JButton startButton;
    private JButton stopButton;
    private JTextArea textArea;

    public static void makeWindow(final Server server, Logger logger) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }

        ServerGui gui = new ServerGui(server, logger);
        final JFrame frame = new JFrame("Comcraft Mod Loader " + Server.version + " server");
        frame.add(gui);
        frame.pack();
        frame.setLocationRelativeTo((Component) null);
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (server.isRunning()) {
                    server.stop();
                }
                frame.dispose();
            }
        });
    }

    public ServerGui(Server server, Logger logger) {
        this.server = server;
        setPreferredSize(new Dimension(854, 480));
        setLayout(new BorderLayout());
        try {
            add(createLogViewer(logger), BorderLayout.CENTER);
            add(createPlayerViewer(), BorderLayout.WEST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JPanel createPlayerViewer() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(makePlayerPane(), BorderLayout.CENTER);
        return panel;
    }

    private JScrollPane makePlayerPane() {
        JList<String> list = new JList<String>();
        server.addPlayerListObserver(list);
        JScrollPane scrollpane = new JScrollPane(list, 22, 30);
        scrollpane.setBorder(new TitledBorder(new EtchedBorder(), "Players"));
        return scrollpane;
    }

    private JPanel createLogViewer(Logger logger) {
        JPanel panel = new JPanel(new BorderLayout());
        textArea = new JTextArea();
        logger.addHandler(new TextAreaLogHandler(textArea));
        JScrollPane scrollPane = new JScrollPane(textArea, 22, 30);
        textArea.setEditable(false);
        startButton = new JButton("Start");
        startButton.addActionListener(this);
        stopButton = new JButton("Stop");
        stopButton.addActionListener(this);
        stopButton.setEnabled(false);
        JPanel buttonPanel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        buttonPanel.add(startButton, BorderLayout.WEST);
        buttonPanel.add(stopButton, BorderLayout.EAST);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        panel.setBorder(new TitledBorder(new EtchedBorder(), "Log"));
        return panel;
    }

    static Server getServer(ServerGui gui) {
        return gui.server;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source instanceof JButton) {
            if (((JButton) source).equals(startButton)) {
                textArea.setText("");
                if (server.start()) {
                    startButton.setEnabled(false);
                    stopButton.setEnabled(true);
                }
            } else if (((JButton) source).equals(stopButton)) {
                server.stop();
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
            }
        }
    }
}
