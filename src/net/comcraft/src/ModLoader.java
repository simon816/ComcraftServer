package net.comcraft.src;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;
import java.util.zip.GZIPInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.google.minijoe.sys.JsFunction;

public class ModLoader {
    public static final int API_VERSION = 6;
    public static final int MIN_API_VERSION = 6;
    private static final int PACKAGE = 0x10;
    private static final int MOD_DESCRIPTOR = 0x20;
    private static final int RESOURCE = 0x30;
    public static final String version = "0.6";
    private Vector<Mod> Mods;
    private boolean hasInitialized = false;
    private Hashtable<String, String> resourcedata;
    private Hashtable<String, HashMap<String, byte[]>> packages;

    public ModLoader() {
        Mods = new Vector<Mod>();
        System.out.println("Comcraft ModLoader " + version + " Initialized");
    }

    public void initMods(String root, Server server) {
        resourcedata = new Hashtable<String, String>();
        System.out.println("scanning mods folder");
        File modFolder = new File(root, "mods");
        if (!modFolder.exists() || !modFolder.isDirectory()) {
            modFolder.mkdir();
        }
        hasInitialized = true;
        String[] elements = modFolder.list();
        packages = new Hashtable<String, HashMap<String, byte[]>>(elements.length / 2);
        ModAPI.getInstance(server); // Prepare the API
        for (int i = 0; i < elements.length; i++) {
            String elementName = elements[i];
            if (elementName.endsWith("/") || !elementName.endsWith(".mod")) {
                continue;
            }
            System.out.println(elementName);
            String modFileName = elementName.substring(elementName.lastIndexOf(0x2F) + 1, elementName.length());
            Mod mod = new Mod(this, modFileName);
            try {
                ReadModFile(new DataInputStream(new GZIPInputStream(new FileInputStream(new File(modFolder, elementName)))), mod);
            } catch (IOException e) {
                e.printStackTrace();
                mod.info = e.getMessage();
                mod.fatalError = true;
            }

            Mods.addElement(mod);
        }
        for (int i = 0; i < Mods.size(); i++) {
            ((Mod) Mods.elementAt(i)).initMod();
        }
    }

    private void ReadModFile(DataInputStream dis, Mod mod) throws IOException {
        byte[] b = new byte[4];
        dis.read(b);
        if (!new String(b).equals("CCML")) {
            throw new IOException("Malformed Mod File");
        }
        int version = dis.read();
        if (version > API_VERSION) {
            throw new IOException("Mod built for a later version of comcraft");
        }
        if (version < MIN_API_VERSION) {
            throw new IOException("Mod built for an earlier version of comcraft");
        }
        mod.info = "No Mod Info";
        int flags = dis.read();
        readLoop: while (dis.available() > 0) {
            int opt = dis.read();
            switch (opt) {
            case MOD_DESCRIPTOR:
                mod.name = dis.readUTF();
                mod.description = dis.readUTF();
                mod.mainClass = dis.readUTF();
                String ldesc = dis.readUTF();
                if (ldesc.length() > 0) {
                    mod.info = ldesc;
                }
                if (isDisabled(mod.mainClass)) {
                    // Don't waste time if the mod is disabled
                    break readLoop;
                }
                break;
            case RESOURCE:
                int l = dis.read();
                for (int i = 0; i < l; i++) {
                    String resname = dis.readUTF();
                    String content = dis.readUTF();
                    if (resourcedata.containsKey(resname)) {
                        // Do something here maybe
                    } else {
                        resourcedata.put(resname, content);
                    }
                }
                break;
            case PACKAGE:
                l = dis.read();
                for (int i = 0; i < l; i++) {
                    String packageName = dis.readUTF();
                    int flen = dis.read();
                    HashMap<String, byte[]> files = new HashMap<String, byte[]>(flen / 2);
                    for (int x = 0; x < flen; x++) {
                        String filename = dis.readUTF();
                        if (version >= 4 && (flags & 1) == 1) {
                            dis.readUTF(); // Skip past source code
                        }
                        int length = dis.readInt();
                        byte[] data = new byte[length];
                        dis.read(data);
                        files.put(filename, data);
                    }
                    packages.put(packageName, files);
                }
                break;
            case -1:
                break;
            default:
                System.out.println("<Unknown Data " + opt + ">");
                break;
            }
        }
        dis.close();
    }

    public boolean executeModInNs(String package_, String fname) throws Exception {
        System.out.println("Executing " + package_ + "." + fname);
        HashMap<String, byte[]> pkg;
        if (packages.containsKey(package_) && (pkg = (HashMap<String, byte[]>) packages.get(package_)).containsKey(fname)) {
            if (pkg.get(fname) != null) {
                // Once executed, the namespace contains the resulting stack.
                // Therefore only execute if we haven't already.
                // Use of JsSystem.JS_NULL because hashtables can't contain null.
                JsFunction.exec(new DataInputStream(new ByteArrayInputStream((byte[]) pkg.get(fname))), ModAPI.getInstance());
                pkg.put(fname, null);
            }
            return true;
        }
        return false;
    }

    public InputStream getResourceAsStream(String filename) {
        // Emulates standard Class.getResourceAsStream
        String content = (String) resourcedata.get(filename);
        if (content == null) {
            return Class.class.getResourceAsStream(filename);
        }
        return new ByteArrayInputStream(content.getBytes());
    }

    public Vector<Mod> ListMods() {
        return Mods;
    }

    public boolean isInitialized() {
        return hasInitialized;
    }

    public boolean isDisabled(String mainClass) {
        return false;
    }
}