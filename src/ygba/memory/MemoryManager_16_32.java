package ygba.memory;

abstract class MemoryManager_16_32
        extends MemoryManager {
    
    
    public MemoryManager_16_32(String name, int size) {
        super(name, size);
    }
    
    
    public final byte loadByte(int offset) {
        offset = getInternalOffset(offset) & 0xFFFFFFFE;
        return space[offset];
    }
    
    public final short loadHalfWord(int offset) {
        offset = getInternalOffset(offset) & 0xFFFFFFFE;
        return (short) ((space[offset] & 0x00FF) |
                        (space[offset + 1] << 8));
    }
    
    public final int loadWord(int offset) {
        offset = getInternalOffset(offset) & 0xFFFFFFFC;
        return (((space[offset] & 0x000000FF)) |
                ((space[offset + 1] & 0x000000FF) << 8) |
                ((space[offset + 2] & 0x000000FF) << 16) |
                ((space[offset + 3]) << 24));
    }
    
    
    public final void storeByte(int offset, byte value) {
        offset = getInternalOffset(offset) & 0xFFFFFFFE;
        space[offset] = space[offset + 1] = value;
    }
    
    public final void storeHalfWord(int offset, short value) {
        offset = getInternalOffset(offset) & 0xFFFFFFFE;
        space[offset] = (byte) value;
        space[offset + 1] = (byte) (value >>> 8);
    }
    
    public final void storeWord(int offset, int value) {
        offset = getInternalOffset(offset) & 0xFFFFFFFC;
        space[offset] = (byte) value;
        space[offset + 1] = (byte) (value >>> 8);
        space[offset + 2] = (byte) (value >>> 16);
        space[offset + 3] = (byte) (value >>> 24);
    }
    
}
