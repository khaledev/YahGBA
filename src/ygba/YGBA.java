package ygba;

import ygba.cpu.ARM7TDMI;
import ygba.memory.Memory;
import ygba.dma.DirectMemoryAccess;
import ygba.gfx.GFX;
import ygba.time.Time;

public final class YGBA {
    
    private ARM7TDMI cpu;
    private Memory memory;
    private DirectMemoryAccess dma;
    private GFX gfx;
    private Time time;
    
    private YGBACore ygbaCore;
    private Thread ygbaThread;
    
    
    public YGBA() {
        cpu = new ARM7TDMI();
        memory = new Memory();
        dma = new DirectMemoryAccess();
        gfx = new GFX();
        time = new Time();
        
        ygbaCore = new YGBACore(cpu, memory, time);
        ygbaThread = null;
        
        setupConnections();
    }
    
    private void setupConnections() {
        cpu.connectToMemory(memory);
        memory.connectToDMA(dma);
        memory.connectToGraphics(gfx);
        memory.connectToTime(time);
        dma.connectToMemory(memory);
        gfx.connectToMemory(memory);
        time.connectToMemory(memory);
    }
    
    
    public ARM7TDMI getCPU() { return cpu; }
    
    public Memory getMemory() { return memory; }
    
    public DirectMemoryAccess getDMA() { return dma; }
    
    public GFX getGraphics() { return gfx; }
    
    public Time getTime() { return time; }
    
    
    public void reset() {
        cpu.reset();
        memory.reset();
        dma.reset();
        gfx.reset();
        time.reset();
        
        ygbaThread = null;
        
        System.gc();
    }
    
    public void run() {
        ygbaThread = new Thread(ygbaCore);
        ygbaThread.setPriority(Thread.NORM_PRIORITY);
        ygbaThread.start();
    }
    
    public void stop() {
        if (ygbaThread != null) {
            ygbaCore.stop();
            try { ygbaThread.join(); } catch (InterruptedException e) {}
            ygbaThread = null;
        }
    }
    
    public boolean isReady() {
        return (memory.isBIOSLoaded() && memory.isROMLoaded());
    }
    
}
