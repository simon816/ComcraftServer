package net.comcraft.src;

import com.google.minijoe.sys.JsArray;
import com.google.minijoe.sys.JsException;
import com.google.minijoe.sys.JsObject;
import com.google.minijoe.sys.JsObjectFactory;
import com.google.minijoe.sys.JsSystem;

public class ModAPI extends JsObject implements JsObjectFactory {

    // Function ID's
    private static final int ID_IMPORT = 1000;

    // Object ID's
    private static final int FACTORY_ID_VEC3D = 0;
    private static final int FACTORY_ID_AABB = 1;
    private static final int FACTORY_ID_INVITEMSTACK = 2;
    private static final int FACTORY_ID_BLOCK = 3;
    private static final int FACTORY_ID_TRANSFORM = 4;
    private static final int FACTORY_ID_GUIBUTTON = 5;

    private static ModAPI instance = null;
    public static final EventHandlerAPI event = new EventHandlerAPI();

    private Server server;

    public static ModAPI getInstance(Server server) {
        if (instance == null) {
            instance = new ModAPI(server);
        }
        return instance;
    }

    public static ModAPI getInstance() {
        if (instance == null) {
            throw new NullPointerException("First instance of ModAPI must provide net.comcraft.client.Comcraft instance");
        }
        return instance;
    }

    private ModAPI(Server server) {
        super(OBJECT_PROTOTYPE);
        this.server = server;
        scopeChain = JsSystem.createGlobal();
        addSingletonObjects();
        addFunctions();
        addInstantiableObjects();
        addEventHandlerEvents();
    }

    /** Variables that are singletons (already initialised) */
    private void addSingletonObjects() {
        addVar("EventHandler", event);
        addVar("console", new OutputConsole(server.logger));
        addVar("Java", new JavaObjectManager());
    }

    /** Function names */
    private void addFunctions() {
        addNative("importFile", ID_IMPORT, 2);
    }

    /** Constructible objects */
    private void addInstantiableObjects() {
    }

    /** String names for events bindable in EventHandlerAPI */
    private void addEventHandlerEvents() {
        event.addEvent("Player.Join");
    }

    /** Handle all API global function calls */
    @Override
    public void evalNative(int id, JsArray stack, int sp, int parCount) {
        if (id < 1000 && id >= 100) {
            throw new IllegalArgumentException("Invalid function call. You may have missed the 'new' keyword");
        }
        switch (id) {
        case ID_IMPORT:
            if (parCount < 2) {
                throw new JsException("Not enough parameters to importFile");
            }
            String pkg = stack.getString(sp + 2);
            String file = stack.getString(sp + 3);
            try {
                stack.setBoolean(sp, server.modLoader.executeModInNs(pkg, file));
            } catch (Exception e) {
                e.printStackTrace();
                throw new JsException("Import " + pkg + "." + file + " failed: " + e.getMessage());
            }
            break;
        default:
            super.evalNative(id, stack, sp, parCount);
        }
    }

    /** Handle all API new instance calls (i.e 'new <ObjectName>()') */
    @Override
    public JsObject newInstance(int type) {
        switch (type) {
        case FACTORY_ID_VEC3D:
        case FACTORY_ID_AABB:
        case FACTORY_ID_INVITEMSTACK:
        case FACTORY_ID_BLOCK:
        case FACTORY_ID_TRANSFORM:
        case FACTORY_ID_GUIBUTTON:
        default:
            throw new IllegalArgumentException();
        }
    }

    public String toString() {
        return "[object ModAPI]";
    }
}
