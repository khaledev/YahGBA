package ygba.memory;

public final class EWorkMemory
        extends MemoryManager_8_16_32 {
    
    
    public EWorkMemory() {
        super("External Work RAM", 0x40000);
    }
    
}
