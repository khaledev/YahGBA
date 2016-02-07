package ygba.dma;

final class DMA0
        extends DMA {
    
    public DMA0() {
        super(0);
    }
    
    public void setSourceHRegister(short value) {
        source = ((value & 0x07FF) << 16) | (source & 0x0000FFFF);
    }
    
    public void setDestinationHRegister(short value) {
        destination = ((value & 0x07FF) << 16) | (destination & 0x0000FFFF);
    }
    
    public void setCountRegister(short value) {
        count = (short) (value & 0x3FFF);
    }
    
}
