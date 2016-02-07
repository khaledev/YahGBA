package ygba.dma;

final class DMA3
        extends DMA {
    
    public DMA3() {
        super(3);
    }
    
    public void setSourceHRegister(short value) {
        source = ((value & 0x0FFF) << 16) | (source & 0x0000FFFF);
    }
    
    public void setDestinationHRegister(short value) {
        destination = ((value & 0x0FFF) << 16) | (destination & 0x0000FFFF);
    }
    
    public void setCountRegister(short value) {
        count = value;
    }
    
}
