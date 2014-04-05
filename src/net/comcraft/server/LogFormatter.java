package net.comcraft.server;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

class LogFormatter extends Formatter {

    LogFormatter() {
    }

    public String format(LogRecord record) {
        StringBuilder message = new StringBuilder();
        message.append(record.getMillis() + " [").append(record.getLevel().getName()).append("] ");
        message.append(this.formatMessage(record));
        message.append(System.lineSeparator());
        Throwable e = record.getThrown();

        if (e != null) {
            StringWriter msg = new StringWriter();
            e.printStackTrace(new PrintWriter(msg));
            message.append(msg.toString());
        }

        return message.toString();
    }
}
