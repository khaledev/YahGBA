package ygba.memory;

public final class IWorkMemory
        extends MemoryManager_8_16_32 {
    
    
    public IWorkMemory() {
        super("Internal Work RAM", 0x8000);
    }
    
}
