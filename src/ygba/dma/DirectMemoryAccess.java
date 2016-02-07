package ygba.dma;

import ygba.memory.Memory;

public final class DirectMemoryAccess {
    
    private DMA[] dma;
    
    
    public DirectMemoryAccess() {
        dma = new DMA[4];
        dma[0] = new DMA0();
        dma[1] = new DMA1();
        dma[2] = new DMA2();
        dma[3] = new DMA3();
    }
    
    public void connectToMemory(Memory memory) {
        for (int i = 0; i < dma.length; i++) {
            dma[i].connectToMemory(memory);
        }
    }
    
    public DMA getDMA(int dmaNumber) {
        return dma[dmaNumber];
    }
    
    public void reset() {
        for (int i = 0; i < dma.length; i++) {
            dma[i].reset();
        }
    }
    
}