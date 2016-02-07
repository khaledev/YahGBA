package ygba.memory;

abstract class MemoryManager_8_16_32
        extends MemoryManager {
    
    
    public MemoryManager_8_16_32(String name, int size) {
        super(name, size);
    }
    
    
    public byte loadByte(int offset) {
        offset = getInternalOffset(offset);
        return space[offset];
    }
    
    public short loadHalfWord(int offset) {
        offset = getInternalOffset(offset) & 0xFFFFFFFE;
        return (short) ((space[offset] & 0x00FF) |
                        (space[offset + 1] << 8));
    }
    
    public int loadWord(int offset) {
        offset = getInternalOffset(offset) & 0xFFFFFFFC;
        return (((space[offset] & 0x000000FF)) |
                ((space[offset + 1] & 0x000000FF) << 8) |
                ((space[offset + 2] & 0x000000FF) << 16) |
                ((space[offset + 3]) << 24));
    }
    
    
    public void storeByte(int offset, byte value) {
        offset = getInternalOffset(offset);
        space[offset] = value;
    }
    
    public void storeHalfWord(int offset, short value) {
        offset = getInternalOffset(offset) & 0xFFFFFFFE;
        space[offset] = (byte) value;
        space[offset + 1] = (byte) (value >>> 8);
    }
    
    public void storeWord(int offset, int value) {
        offset = getInternalOffset(offset) & 0xFFFFFFFC;
        space[offset] = (byte) value;
        space[offset + 1] = (byte) (value >>> 8);
        space[offset + 2] = (byte) (value >>> 16);
        space[offset + 3] = (byte) (value >>> 24);
    }
    
}
