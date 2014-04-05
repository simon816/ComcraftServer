package net.comcraft.src;

public class Mod {

    public String info;
    public boolean running = false;
    private ModLoader ml;
    public String name;
    public String description;
    public String mainClass;
    public boolean enabled;
    public boolean fatalError;

    public Mod(ModLoader modLoader, String name) {
        ml = modLoader;
        this.name = name;
        enabled = true;
        fatalError = false;
    }

    public void initMod() {
        enabled = !ml.isDisabled(mainClass);
        runMain();
    }

    public void runMain() {
        if (running || !enabled || mainClass == null)
            return;
        int idx = mainClass.lastIndexOf('.');
        String mainPackage = mainClass.substring(0, idx);
        String fileName = mainClass.substring(idx + 1);
        try {
            ml.executeModInNs(mainPackage, fileName);
            running = true;
        } catch (Exception e) {
            fatalError = true;
            e.printStackTrace();
            info = e.getMessage() + "\n\nIn Mod " + mainClass;
        }
    }

    public void enable() {
        // ml.enable(mainClass);
        enabled = true;
    }

    public void disable() {
        // ml.disable(mainClass);
        enabled = false;
    }
}
