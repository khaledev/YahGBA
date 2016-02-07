package ygba.memory;

import ygba.util.Hex;

abstract class MemoryManager
        implements MemoryInterface {
    
    
    String name;
    
    int size;
    int mask;
    
    byte[] space;
    
    
    public MemoryManager(String name, int size) {
        this.name = name;
        createSpace(size);
    }
    
    private final static int MinMemSize = 0x4;
    
    protected byte[] createSpace(int s) {
        size = (s < MinMemSize) ? MinMemSize : s;
        mask = size - 1;
        space = new byte[size];
        return space;
    }
    
    public final String getName() {
        return name;
    }
    
    public final int getSize() {
        return size;
    }
    
    public final byte[] getSpace() {
        return space;
    }
    
    public final byte[] getBytes() {
        return getSpace();
    }
    
    public int getInternalOffset(int offset) {
        return (offset & mask);
    }
    
    
    public final byte getByte(int offset) {
        return space[getInternalOffset(offset)];
    }
    
    public final short getHalfWord(int offset) {
        offset = getInternalOffset(offset);
        return (short) ((space[offset] & 0x00FF) |
                        (space[offset + 1] << 8));
    }
    
    public final int getWord(int offset) {
        offset = getInternalOffset(offset);
        return (((space[offset] & 0x000000FF)) |
                ((space[offset + 1] & 0x000000FF) << 8) |
                ((space[offset + 2] & 0x000000FF) << 16) |
                ((space[offset + 3]) << 24));
    }
    
    
    public final void setByte(int offset, byte value) {
        offset = getInternalOffset(offset);
        space[offset] = value;
    }
    
    public final void setHalfWord(int offset, short value) {
        offset = getInternalOffset(offset);
        space[offset] = (byte) value;
        space[offset + 1] = (byte) (value >>> 8);
    }
    
    public final void setWord(int offset, int value) {
        offset = getInternalOffset(offset);
        space[offset] = (byte) value;
        space[offset + 1] = (byte) (value >>> 8);
        space[offset + 2] = (byte) (value >>> 16);
        space[offset + 3] = (byte) (value >>> 24);
    }
    
    
    public void softReset() {
        hardReset();
    }
    
    public final void hardReset() {
        for (int i = 0; i < space.length; i++) {
            space[i] = 0;
        }
    }
    
    
    protected final void handleAccessViolation(int offset) {
        //System.out.println("Memory access violation at " + Hex.toAddrString(offset, Hex.Word) + " ("+ name + ")");
    }
    
}
