package ygba.memory;

public final class PaletteMemory
        extends MemoryManager_16_32 {
    
    
    public PaletteMemory() {
        super("Palette RAM", 0x400);
    }
    
}
