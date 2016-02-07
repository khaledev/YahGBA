package ygba.memory;

public final class VideoMemory
        extends MemoryManager_16_32 {
    
    
    public VideoMemory() {
        super("Video RAM", 0x18000);
    }
    
    
    public int getInternalOffset(int offset) {
        return ((offset & 0x00FFFFFF) % size);
    }
    
}
