package net.comcraft.src;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.minijoe.sys.JsArray;
import com.google.minijoe.sys.JsObject;

public class OutputConsole extends JsObject {

    private static final int ID_LOG = 100;
    private static final int ID_DEBUG = 101;
    private static final int ID_INFO = 102;
    private static final int ID_WARN = 103;
    private static final int ID_ERROR = 104;
    private Logger log;

    public OutputConsole(Logger logger) {
        super(OBJECT_PROTOTYPE);
        log = logger;
        addNative("log", ID_LOG, 1);
        addNative("debug", ID_DEBUG, 1);
        addNative("info", ID_INFO, 1);
        addNative("warn", ID_WARN, 1);
        addNative("error", ID_ERROR, 1);
    }

    @Override
    public void evalNative(int id, JsArray stack, int sp, int parCount) {
        Level level = Level.OFF;
        switch (id) {
        case ID_LOG:
            level = Level.ALL;
            break;
        case ID_DEBUG:
            level = Level.CONFIG;
            break;
        case ID_INFO:
            level = Level.INFO;
            break;
        case ID_WARN:
            level = Level.WARNING;
            break;
        case ID_ERROR:
            level = Level.SEVERE;
            break;
        default:
            super.evalNative(id, stack, sp, parCount);
            return;
        }
        String msg = "";
        for (int i = 0; i < parCount; i++) {
            msg += (i > 0 ? " " : "") + stack.getObject(sp + 2 + i);
        }
        System.out.println(msg);
        log.log(level, msg);
    }
}
