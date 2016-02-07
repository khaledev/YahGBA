package ygba.memory;

public final class UnusedMemory
        extends MemoryManager {
    
    
    public UnusedMemory() {
        super("Unused", 0x0);
    }
    
    
    public byte loadByte(int offset) {
        handleAccessViolation(offset);
        return 0;
    }
    
    public short loadHalfWord(int offset) {
        handleAccessViolation(offset);
        return 0;
    }
    
    public int loadWord(int offset) {
        handleAccessViolation(offset);
        return 0;
    }
    
    
    public void storeByte(int offset, byte value) {
        handleAccessViolation(offset);
    }
    
    public void storeHalfWord(int offset, short value) {
        handleAccessViolation(offset);
    }
    
    public void storeWord(int offset, int value) {
        handleAccessViolation(offset);
    }
    
}
