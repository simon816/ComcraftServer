package net.comcraft.src;

public class PlayerInventory {
    private int[] slots;
    private int[] counts;

    public PlayerInventory(int slotsize) {
        slots = new int[slotsize];
        counts = new int[slotsize];
    }

    public void setIdAtSlot(int slotnum, int id) {
        slots[slotnum] = id;
    }

    public int getIdAtSlot(int slotnum) {
        return slots[slotnum];
    }

    public int slotSize() {
        return slots.length;
    }

    public int getCountAtSlot(int slotnum) {
        return counts[slotnum];
    }

    public void setCountAtSlot(int slotnum, int count) {
        counts[slotnum] = count;
    }
}
