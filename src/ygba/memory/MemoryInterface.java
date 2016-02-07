package ygba.memory;

public interface MemoryInterface {
    
    byte getByte(int offset);
    short getHalfWord(int offset);
    int getWord(int offset);
    
    void setByte(int offset, byte value);
    void setHalfWord(int offset, short value);
    void setWord(int offset, int value);
    
    byte loadByte(int offset);
    short loadHalfWord(int offset);
    int loadWord(int offset);
    
    void storeByte(int offset, byte value);
    void storeHalfWord(int offset, short value);
    void storeWord(int offset, int value);
    
}
