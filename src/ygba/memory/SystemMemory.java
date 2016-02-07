package ygba.memory;

public final class SystemMemory
        extends MemoryManager_8_16_32 {
    
    
    public SystemMemory() {
        super("System ROM", 0x4000);
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
    
    
    public void softReset() {}
    
}
