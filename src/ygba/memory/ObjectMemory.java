package ygba.memory;

public final class ObjectMemory
        extends MemoryManager_16_32 {
    
    
    public ObjectMemory() {
        super("Object RAM", 0x400);
    }
    
    
    public int getPriority(int objNumber) {
        int objAttributesAddress = (objNumber << 3);
        return ((space[objAttributesAddress + 5] >>> 2) & 0x00000003);
    }
    
    public int getXSize(int objNumber) {
        int objAttributesAddress = (objNumber << 3);
        switch (((space[objAttributesAddress + 1] & 0x000000C0) >>> 6) |
                ((space[objAttributesAddress + 3] & 0x000000C0) >>> 4)) {
            case 0:
            case 2:
            case 6:
                return 8;
            case 1:
            case 4:
            case 10:
                return 16;
            case 5:
            case 8:
            case 9:
            case 14:
                return 32;
            case 12:
            case 13:
                return 64;
            default:
                return 0;
        }
    }
    
    public int getYSize(int objNumber) {
        int objAttributesAddress = (objNumber << 3);
        switch (((space[objAttributesAddress + 1] & 0x000000C0) >>> 6) |
                ((space[objAttributesAddress + 3] & 0x000000C0) >>> 4)) {
            case 0:
            case 1:
            case 5:
                return 8;
            case 2:
            case 4:
            case 9:
                return 16;
            case 6:
            case 8:
            case 10:
            case 13:
                return 32;
            case 12:
            case 14:
                return 64;
            default:
                return 0;
        }
    }
    
    public int getXCoordinate(int objNumber) {
        int objAttributesAddress = (objNumber << 3);
        return ((space[objAttributesAddress + 2] & 0x000000FF) | ((space[objAttributesAddress + 3] << 31) >> 23));
    }
    
    public int getYCoordinate(int objNumber) {
        int objAttributesAddress = (objNumber << 3);
        return (space[objAttributesAddress] & 0x000000FF);
    }
    
    public boolean isMosaicEnabled(int objNumber) {
        int objAttributesAddress = (objNumber << 3);
        return ((space[objAttributesAddress + 1] & 0x10) != 0);
    }
    
    public boolean is256ColorPalette(int objNumber) {
        int objAttributesAddress = (objNumber << 3);
        return ((space[objAttributesAddress + 1] & 0x20) != 0);
    }
    
    public int getPaletteNumber(int objNumber) {
        int objAttributesAddress = (objNumber << 3);
        return ((space[objAttributesAddress + 5] & 0xF0) >>> 4);
    }
    
    public int getTileNumber(int objNumber) {
        int objAttributesAddress = (objNumber << 3);
        return ((space[objAttributesAddress + 4] & 0x000000FF) | ((space[objAttributesAddress + 5] & 0x00000003) << 8));
    }
    
    public boolean isRotScalEnabled(int objNumber) {
        int objAttributesAddress = (objNumber << 3);
        return ((space[objAttributesAddress + 1] & 0x01) != 0);
    }
    
    public boolean isDoubleSizeEnabled(int objNumber) {
        int objAttributesAddress = (objNumber << 3);
        return ((space[objAttributesAddress + 1] & 0x02) != 0);
    }
    
    public int getRotScalGroupNumber(int objNumber) {
        int objAttributesAddress = (objNumber << 3);
        return ((space[objAttributesAddress + 3] >>> 1) & 0x0000001F);
    }
    
    public short getPA(int groupNumber) {
        int address = (groupNumber << 5) + 6;
        return (short) ((space[address + 1] << 8) | (space[address] & 0x00FF));
    }
    
    public short getPB(int groupNumber) {
        int address = (groupNumber << 5) + 14;
        return (short) ((space[address + 1] << 8) | (space[address] & 0x00FF));
    }
    
    public short getPC(int groupNumber) {
        int address = (groupNumber << 5) + 22;
        return (short) ((space[address + 1] << 8) | (space[address] & 0x00FF));
    }
    
    public short getPD(int groupNumber) {
        int address = (groupNumber << 5) + 30;
        return (short) ((space[address + 1] << 8) | (space[address] & 0x00FF));
    }
    
    public boolean isDisplayable(int objNumber) {
        int objAttributesAddress = (objNumber << 3);
        return ((space[objAttributesAddress + 1] & 0x02) == 0);
    }
    
    public boolean isHFlipEnabled(int objNumber) {
        int objAttributesAddress = (objNumber << 3);
        return ((space[objAttributesAddress + 3] & 0x10) != 0);
    }
    
    public boolean isVFlipEnabled(int objNumber) {
        int objAttributesAddress = (objNumber << 3);
        return ((space[objAttributesAddress + 3] & 0x20) != 0);
    }
    
}
