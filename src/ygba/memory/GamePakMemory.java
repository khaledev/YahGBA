package ygba.memory;

public final class GamePakMemory
        extends MemoryManager_8_16_32 {
    
    
    public GamePakMemory(int partNumber) {
        super("Game Pak ROM Part" + partNumber, 0x0);
    }
    
    
    protected byte[] createSpace(int s) {
        int i = 1;
        while (i < s) i <<= 1;
        return super.createSpace(i);
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
