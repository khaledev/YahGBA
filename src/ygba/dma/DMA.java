package ygba.dma;

import ygba.memory.Memory;
import ygba.memory.IORegMemory;

public abstract class DMA {
    
    int source, destination;
    short count, control;
    boolean isEnabled, isIRQEnabled, isRepeatEnabled;
    int startTiming;
    
    private int dmaNumber;
    private int dmaMaxCount;
    private short dmaInterruptBit;
    
    Memory memory;
    IORegMemory iorMem;
    
    private final static int
            ImmediateStartTiming = 0x0000,
            VBlankStartTiming = 0x1000,
            HBlankStartTiming = 0x2000,
            SpecialStartTiming = 0x3000;
    
    public DMA(int dmaNumber) {
        this.dmaNumber = dmaNumber;
        dmaMaxCount = ((dmaNumber == 3) ? 0x00010000 : 0x00004000);
        dmaInterruptBit = (short) (0x0100 << dmaNumber);
    }
    
    public final void connectToMemory(Memory memory) {
        this.memory = memory;
        iorMem = memory.getIORegMemory();
    }
    
    public final void reset() {
        source = destination = 0;
        count = control = 0;
        isEnabled = isIRQEnabled = isRepeatEnabled = false;
        startTiming = 0;
    }
    
    public final String getName() {
        return "DMA" + dmaNumber;
    }
    
    public void setSourceLRegister(short value) {
        source = (source & 0xFFFF0000) | (value & 0x0000FFFF);
    }
    
    public abstract void setSourceHRegister(short value);
    
    public void setDestinationLRegister(short value) {
        destination = (destination & 0xFFFF0000) | (value & 0x0000FFFF);
    }
    
    public abstract void setDestinationHRegister(short value);
    
    public abstract void setCountRegister(short value);
    
    public final void setControlRegister(short value) {
        control = value;
        isEnabled = ((control & 0x8000) != 0);
        isIRQEnabled = ((control & 0x4000) != 0);
        isRepeatEnabled = ((control & 0x0200) != 0);
        startTiming = (control & 0x3000);
        signalImmediately();
    }
    
    private final void signal(int st) {
        if (isEnabled & (startTiming == st)) {
            boolean is32BitTransfer = ((control & 0x0400) != 0);
            int dmaTransferSize = (is32BitTransfer ? 4 : 2);
            int dmaCount = ((count == 0) ? dmaMaxCount : (count & 0x0000FFFF));
            
            int dstAdd, srcAdd;
            int dstControl = control & 0x0060;
            int srcControl = control & 0x0180;
            
            switch (dstControl) {
                case 0x0000:
                case 0x0060: dstAdd = +dmaTransferSize; break;
                case 0x0020: dstAdd = -dmaTransferSize; break;
                case 0x0040: dstAdd = 0; break;
                default: return;
            }
            switch (srcControl) {
                case 0x0000: srcAdd = +dmaTransferSize; break;
                case 0x0080: srcAdd = -dmaTransferSize; break;
                case 0x0100: srcAdd = 0; break;
                default: return;
            }
            
            int old_destination = destination;
            if (is32BitTransfer) {
                for (int i = 0; i < dmaCount; i++) {
                    memory.storeWord(destination, memory.loadWord(source));
                    destination += dstAdd;
                    source += srcAdd;
                }
            } else {
                for (int i = 0; i < dmaCount; i++) {
                    memory.storeHalfWord(destination, memory.loadHalfWord(source));
                    destination += dstAdd;
                    source += srcAdd;
                }
            }
            if (dstControl == 0x0060) destination = old_destination;
            
            if (isIRQEnabled) iorMem.generateInterrupt(dmaInterruptBit);
            
            if (!isRepeatEnabled) {
                control &= ~0x8000;
                isEnabled = false;
            }
        }
    }
    
    private final void signalImmediately() {
        signal(ImmediateStartTiming);
    }
    
    public final void signalVBlank() {
        signal(VBlankStartTiming);
    }
    
    public final void signalHBlank() {
        signal(HBlankStartTiming);
    }
    
    public final void signalSpecial() {
        signal(SpecialStartTiming);
    }
    
    public final short getSourceLRegister() {
        return (short) (source & 0x0000FFFF);
    }
    
    public final short getSourceHRegister() {
        return (short) (source >>> 16);
    }
    
    public final short getDestinationLRegister() {
        return (short) (destination & 0x0000FFFF);
    }
    
    public final short getDestinationHRegister() {
        return (short) (destination >>> 16);
    }
    
    public final short getCountRegister() {
        return count;
    }
    
    public final short getControlRegister() {
        return control;
    }
    
}
