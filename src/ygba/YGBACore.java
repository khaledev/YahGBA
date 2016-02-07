package ygba;

import ygba.cpu.ARM7TDMI;
import ygba.memory.Memory;
import ygba.memory.IORegMemory;
import ygba.time.Time;

final class YGBACore
        implements Runnable {
    
    private ARM7TDMI cpu;
    private IORegMemory iorMem;
    private Time time;
    
    private boolean stopped;
    
    
    public YGBACore(ARM7TDMI cpu, Memory memory, Time time) {
        this.cpu = cpu;
        this.iorMem = memory.getIORegMemory();
        this.time = time;
        
        stopped = true;
    }
    
    
    private final static int
            // Horizontal Dimensions
            HDrawDots  = 240,
            HBlankDots = 68,
            HDots      = HDrawDots + HBlankDots,
            // Vertical Dimensions
            VDrawLines  = 160,
            VBlankLines = 68,
            VLines      = VDrawLines + VBlankLines,
            // Timings
            CyclesPerDot    = 4,
            CyclesPerHDraw  = HDrawDots * CyclesPerDot,
            CyclesPerHBlank = HBlankDots * CyclesPerDot,
            CyclesPerLine   = CyclesPerHDraw + CyclesPerHBlank;
    
    public void run() {
        stopped = false;
        
        while (!stopped) {
            for (int scanline = 0; scanline < VLines; scanline++) {
                iorMem.setCurrentScanline(scanline);
                cpu.run(CyclesPerHDraw);
                iorMem.enterHBlank();
                cpu.run(CyclesPerHBlank);
                iorMem.exitHBlank();
                time.addTime(CyclesPerLine);
                if (scanline == VDrawLines - 1) iorMem.enterVBlank();
                else if (scanline == VLines - 1) iorMem.exitVBlank();
            }
        }
    }
    
    public void stop() {
        stopped = true;
    }
    
}
