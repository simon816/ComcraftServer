package net.comcraft.server;

import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import javax.swing.JTextArea;

class TextAreaLogHandler extends Handler {
    private int[] sizeBuffer = new int[1024];
    private int bufPos;
    Formatter formatter = new LogFormatter();
    private JTextArea textArea;

    public TextAreaLogHandler(JTextArea textarea) {
        setFormatter(formatter);
        textArea = textarea;
    }

    public void close() {
    }

    public void flush() {
    }

    public void publish(LogRecord record) {
        int prevLength = textArea.getDocument().getLength();
        textArea.append(formatter.format(record));
        textArea.setCaretPosition(textArea.getDocument().getLength());
        int logSize = textArea.getDocument().getLength() - prevLength;

        if (sizeBuffer[bufPos] != 0) {
            textArea.replaceRange("", 0, sizeBuffer[bufPos]);
        }

        sizeBuffer[bufPos] = logSize;
        bufPos = (bufPos + 1) % 1024;
    }
}
