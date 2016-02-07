package ygba.memory;

abstract class MemoryManager_8
        extends MemoryManager {
    
    
    public MemoryManager_8(String name, int size) {
        super(name, size);
    }
    
    
    public final byte loadByte(int offset) {
        offset = getInternalOffset(offset);
        return space[offset];
    }
    
    public final short loadHalfWord(int offset) { return 0; }
    
    public final int loadWord(int offset) { return 0; }
    
    
    public final void storeByte(int offset, byte value) {
        offset = getInternalOffset(offset);
        space[offset] = value;
    }
    
    public final void storeHalfWord(int offset, short value) {}
    
    public final void storeWord(int offset, int value) {}
    
}
