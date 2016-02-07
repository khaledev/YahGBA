package ygba.memory;

import ygba.dma.DirectMemoryAccess;
import ygba.dma.DMA;
import ygba.gfx.GFX;
import ygba.time.Time;
import ygba.time.Timer;

import java.awt.event.KeyEvent;

public final class IORegMemory
        extends MemoryManager_8_16_32 {
    
    // LCD registers
    public final static int
            REG_DISPCNT  = 0x0000, // LCD control
            REG_DISPSTAT = 0x0004, // General LCD status
            REG_VCOUNT   = 0x0006, // Vertical counter
            REG_BG0CNT   = 0x0008, // BG0 control
            REG_BG1CNT   = 0x000A, // BG1 control
            REG_BG2CNT   = 0x000C, // BG2 control
            REG_BG3CNT   = 0x000E, // BG3 control
            REG_BG0HOFS  = 0x0010, // BG0 X-offset
            REG_BG0VOFS  = 0x0012, // BG0 Y-offset
            REG_BG1HOFS  = 0x0014, // BG1 X-offset
            REG_BG1VOFS  = 0x0016, // BG1 Y-offset
            REG_BG2HOFS  = 0x0018, // BG2 X-offset
            REG_BG2VOFS  = 0x001A, // BG2 Y-offset
            REG_BG3HOFS  = 0x001C, // BG3 X-offset
            REG_BG3VOFS  = 0x001E, // BG3 Y-offset
            REG_BG2PA    = 0x0020, // BG2 rotation/scaling parameter A
            REG_BG2PB    = 0x0022, // BG2 rotation/scaling parameter B
            REG_BG2PC    = 0x0024, // BG2 rotation/scaling parameter C
            REG_BG2PD    = 0x0026, // BG2 rotation/scaling parameter D
            REG_BG2X     = 0x0028, // BG2 reference point X-coordinate
            REG_BG2Y     = 0x002C, // BG2 reference point Y-coordinate
            REG_BG3PA    = 0x0030, // BG3 rotation/scaling parameter A
            REG_BG3PB    = 0x0032, // BG3 rotation/scaling parameter B
            REG_BG3PC    = 0x0034, // BG3 rotation/scaling parameter C
            REG_BG3PD    = 0x0036, // BG3 rotation/scaling parameter D
            REG_BG3X     = 0x0038, // BG3 reference point X-coordinate
            REG_BG3Y     = 0x003C, // BG3 reference point Y-coordinate
            REG_WIN0H    = 0x0040, // Win0 horizontal dimensions
            REG_WIN1H    = 0x0042, // Win1 horizontal dimensions
            REG_WIN0V    = 0x0044, // Win0 vertical dimensions
            REG_WIN1V    = 0x0046, // Win1 vertical dimensions
            REG_WININ    = 0x0048, // Control inside of windows
            REG_WINOUT   = 0x004A, // Control outside of windows and inside of OBJ window
            REG_MOSAIC   = 0x004C, // Mosaic size
            REG_BLDMOD   = 0x0050, // Color special effects selection
            REG_COLEV    = 0x0052, // Alpha blending coefficients
            REG_COLY     = 0x0054; // Brightness (fade-in/out) coefficient
    
    // DMA registers
    public final static int
            REG_DMA0SAD   = 0x00B0, // DMA0 source address
            REG_DMA0DAD   = 0x00B4, // DMA0 destination address
            REG_DMA0CNT_L = 0x00B8, // DMA0 word count
            REG_DMA0CNT_H = 0x00BA, // DMA0 control
            REG_DMA1SAD   = 0x00BC, // DMA1 source address
            REG_DMA1DAD   = 0x00C0, // DMA1 destination address
            REG_DMA1CNT_L = 0x00C4, // DMA1 word count
            REG_DMA1CNT_H = 0x00C6, // DMA1 control
            REG_DMA2SAD   = 0x00C8, // DMA2 source address
            REG_DMA2DAD   = 0x00CC, // DMA2 destination address
            REG_DMA2CNT_L = 0x00D0, // DMA2 word count
            REG_DMA2CNT_H = 0x00D2, // DMA2 control
            REG_DMA3SAD   = 0x00D4, // DMA3 source address
            REG_DMA3DAD   = 0x00D8, // DMA3 destination address
            REG_DMA3CNT_L = 0x00DC, // DMA3 word count
            REG_DMA3CNT_H = 0x00DE; // DMA3 control
    
    // Timer registers
    public final static int
            REG_TM0D   = 0x0100, // Timer0 counter/reload
            REG_TM0CNT = 0x0102, // Timer0 control
            REG_TM1D   = 0x0104, // Timer1 counter/reload
            REG_TM1CNT = 0x0106, // Timer1 control
            REG_TM2D   = 0x0108, // Timer2 counter/reload
            REG_TM2CNT = 0x010A, // Timer2 control
            REG_TM3D   = 0x010C, // Timer3 counter/reload
            REG_TM3CNT = 0x010E; // Timer3 control
    
    // Keypad input registers
    public final static int
            REG_P1    = 0x130, // Key status
            REG_P1CNT = 0x132; // Key interrupt control
    
    // Interrupt, Waitstate and Power-down control registers
    public final static int
            REG_IE      = 0x0200, // Interrupt Enable
            REG_IF      = 0x0202, // Interrupt Flags
            REG_WSCNT   = 0x0204, // GamePak waitstate control
            REG_IME     = 0x0208, // Interrupt Master Enable
            REG_HALTCNT = 0x0300; // Power-down control
    
    private short keyInput;
    
    private DMA dma0, dma1, dma2, dma3;
    private GFX gfx;
    private Timer timer0, timer1, timer2, timer3;
    
    
    public IORegMemory() {
        super("I/O Registers RAM", 0x400);
    }
    
    void connectToDMA(DirectMemoryAccess dma) {
        this.dma0 = dma.getDMA(0);
        this.dma1 = dma.getDMA(1);
        this.dma2 = dma.getDMA(2);
        this.dma3 = dma.getDMA(3);
    }
    
    void connectToGraphics(GFX gfx) {
        this.gfx = gfx;
    }
    
    void connectToTime(Time time) {
        timer0 = time.getTimer(0);
        timer1 = time.getTimer(1);
        timer2 = time.getTimer(2);
        timer3 = time.getTimer(3);
    }
    
    
    public byte loadByte(int offset) {
        offset = getInternalOffset(offset);
        int offset16 = offset & 0xFFFFFFFE; // Halfword aligned offset
        
        switch (offset16) {
            // DMA0
            case REG_DMA0SAD:   setHalfWord(offset16, dma0.getSourceLRegister()); break;
            case REG_DMA0SAD+2: setHalfWord(offset16, dma0.getSourceHRegister()); break;
            case REG_DMA0DAD:   setHalfWord(offset16, dma0.getDestinationLRegister()); break;
            case REG_DMA0DAD+2: setHalfWord(offset16, dma0.getDestinationHRegister()); break;
            case REG_DMA0CNT_L: setHalfWord(offset16, dma0.getCountRegister()); break;
            case REG_DMA0CNT_H: setHalfWord(offset16, dma0.getControlRegister()); break;
            
            // DMA1
            case REG_DMA1SAD:   setHalfWord(offset16, dma1.getSourceLRegister()); break;
            case REG_DMA1SAD+2: setHalfWord(offset16, dma1.getSourceHRegister()); break;
            case REG_DMA1DAD:   setHalfWord(offset16, dma1.getDestinationLRegister()); break;
            case REG_DMA1DAD+2: setHalfWord(offset16, dma1.getDestinationHRegister()); break;
            case REG_DMA1CNT_L: setHalfWord(offset16, dma1.getCountRegister()); break;
            case REG_DMA1CNT_H: setHalfWord(offset16, dma1.getControlRegister()); break;
            
            // DMA2
            case REG_DMA2SAD:   setHalfWord(offset16, dma2.getSourceLRegister()); break;
            case REG_DMA2SAD+2: setHalfWord(offset16, dma2.getSourceHRegister()); break;
            case REG_DMA2DAD:   setHalfWord(offset16, dma2.getDestinationLRegister()); break;
            case REG_DMA2DAD+2: setHalfWord(offset16, dma2.getDestinationHRegister()); break;
            case REG_DMA2CNT_L: setHalfWord(offset16, dma2.getCountRegister()); break;
            case REG_DMA2CNT_H: setHalfWord(offset16, dma2.getControlRegister()); break;
            
            // DMA3
            case REG_DMA3SAD:   setHalfWord(offset16, dma3.getSourceLRegister()); break;
            case REG_DMA3SAD+2: setHalfWord(offset16, dma3.getSourceHRegister()); break;
            case REG_DMA3DAD:   setHalfWord(offset16, dma3.getDestinationLRegister()); break;
            case REG_DMA3DAD+2: setHalfWord(offset16, dma3.getDestinationHRegister()); break;
            case REG_DMA3CNT_L: setHalfWord(offset16, dma3.getCountRegister()); break;
            case REG_DMA3CNT_H: setHalfWord(offset16, dma3.getControlRegister()); break;
            
            // Timers
            case REG_TM0D: setHalfWord(offset16, timer0.getTime()); break;
            case REG_TM1D: setHalfWord(offset16, timer1.getTime()); break;
            case REG_TM2D: setHalfWord(offset16, timer2.getTime()); break;
            case REG_TM3D: setHalfWord(offset16, timer3.getTime()); break;
        }
        
        return space[offset];
    }
    
    public short loadHalfWord(int offset) {
        offset &= 0xFFFFFFFE;
        return (short) ((loadByte(offset) & 0x00FF) |
                        (loadByte(offset + 1) << 8));
    }
    
    public int loadWord(int offset) {
        offset &= 0xFFFFFFFC;
        return (((loadByte(offset) & 0x000000FF)) |
                ((loadByte(offset + 1) & 0x000000FF) << 8) |
                ((loadByte(offset + 2) & 0x000000FF) << 16) |
                ((loadByte(offset + 3)) << 24));
    }
    
    private static short getValue16(boolean isOffsetAligned, short oldValue, byte newValue) {
        return (short) (isOffsetAligned ? ((oldValue & 0xFF00) | (newValue & 0x00FF)) : ((oldValue & 0x00FF) | (newValue << 8)));
    }
    
    public void storeByte(int offset, byte value) {
        offset = getInternalOffset(offset);
        int offset16 = offset & 0xFFFFFFFE;
        boolean isOffsetAligned = ((offset & 0x00000001) == 0);
        short value16 = getValue16(isOffsetAligned, getHalfWord(offset16), value);
        
        switch (offset16) {
            // VCOUNT
            case REG_VCOUNT:
                return;
                
            // DMA0
            case REG_DMA0SAD:   dma0.setSourceLRegister(value16); break;
            case REG_DMA0SAD+2: dma0.setSourceHRegister(value16); break;
            case REG_DMA0DAD:   dma0.setDestinationLRegister(value16); break;
            case REG_DMA0DAD+2: dma0.setDestinationHRegister(value16); break;
            case REG_DMA0CNT_L: dma0.setCountRegister(value16); break;
            case REG_DMA0CNT_H: dma0.setControlRegister(value16); break;
            
            // DMA1
            case REG_DMA1SAD:   dma1.setSourceLRegister(value16); break;
            case REG_DMA1SAD+2: dma1.setSourceHRegister(value16); break;
            case REG_DMA1DAD:   dma1.setDestinationLRegister(value16); break;
            case REG_DMA1DAD+2: dma1.setDestinationHRegister(value16); break;
            case REG_DMA1CNT_L: dma1.setCountRegister(value16); break;
            case REG_DMA1CNT_H: dma1.setControlRegister(value16); break;
            
            // DMA2
            case REG_DMA2SAD:   dma2.setSourceLRegister(value16); break;
            case REG_DMA2SAD+2: dma2.setSourceHRegister(value16); break;
            case REG_DMA2DAD:   dma2.setDestinationLRegister(value16); break;
            case REG_DMA2DAD+2: dma2.setDestinationHRegister(value16); break;
            case REG_DMA2CNT_L: dma2.setCountRegister(value16); break;
            case REG_DMA2CNT_H: dma2.setControlRegister(value16); break;
            
            // DMA3
            case REG_DMA3SAD:
                value16 = getValue16(isOffsetAligned, dma3.getSourceLRegister(), value);
                dma3.setSourceLRegister(value16);
                break;
            case REG_DMA3SAD+2:
                value16 = getValue16(isOffsetAligned, dma3.getSourceHRegister(), value);
                dma3.setSourceHRegister(value16);
                break;
            case REG_DMA3DAD:
                value16 = getValue16(isOffsetAligned, dma3.getDestinationLRegister(), value);
                dma3.setDestinationLRegister(value16);
                break;
            case REG_DMA3DAD+2:
                value16 = getValue16(isOffsetAligned, dma3.getDestinationHRegister(), value);
                dma3.setDestinationHRegister(value16);
                break;
            case REG_DMA3CNT_L:
                value16 = getValue16(isOffsetAligned, dma3.getCountRegister(), value);
                dma3.setCountRegister(value16);
                break;
            case REG_DMA3CNT_H:
                value16 = getValue16(isOffsetAligned, dma3.getControlRegister(), value);
                dma3.setControlRegister(value16);
                break;
            
            // Timers
            case REG_TM0D:   timer0.setTime(value16); break;
            case REG_TM0CNT: timer0.updateState(value16); break;
            case REG_TM1D:   timer1.setTime(value16); break;
            case REG_TM1CNT: timer1.updateState(value16); break;
            case REG_TM2D:   timer2.setTime(value16); break;
            case REG_TM2CNT: timer2.updateState(value16); break;
            case REG_TM3D:   timer3.setTime(value16); break;
            case REG_TM3CNT: timer3.updateState(value16); break;
            
            // Keypad
            case REG_P1:
                return;
            
            // Interrupts
            case REG_IF:
                space[offset] &= ~value;
                return;
        }
        
        space[offset] = value;
    }
    
    public void storeHalfWord(int offset, short value) {
        offset &= 0xFFFFFFFE;
        storeByte(offset, (byte) value);
        storeByte(offset + 1, (byte) (value >>> 8));
    }
    
    public void storeWord(int offset, int value) {
        offset &= 0xFFFFFFFC;
        storeByte(offset, (byte) value);
        storeByte(offset + 1, (byte) (value >>> 8));
        storeByte(offset + 2, (byte) (value >>> 16));
        storeByte(offset + 3, (byte) (value >>> 24));
    }
    
    public void softReset() {
        hardReset();
        setHalfWord(REG_DISPCNT, (short) 0x0080);
        setHalfWord(REG_BG2PA, (short) 0x0100);
        setHalfWord(REG_BG2PD, (short) 0x0100);
        setHalfWord(REG_BG3PA, (short) 0x0100);
        setHalfWord(REG_BG3PD, (short) 0x0100);
        setHalfWord(REG_P1, (short) 0x03FF);
        keyInput = 0x03FF;
    }
    
    
    private final static int[]
            BGBit = {
                0x01,
                0x02,
                0x04,
                0x08,
    },
            WinBit = {
                0x20,
                0x40,
    },
            REG_BGxCNT = {
                REG_BG0CNT,
                REG_BG1CNT,
                REG_BG2CNT,
                REG_BG3CNT,
    },
            REG_BGxHOFS = {
                REG_BG0HOFS,
                REG_BG1HOFS,
                REG_BG2HOFS,
                REG_BG3HOFS,
    },
            REG_BGxVOFS = {
                REG_BG0VOFS,
                REG_BG1VOFS,
                REG_BG2VOFS,
                REG_BG3VOFS,
    },
            REG_BGxX =  {
                0,
                0,
                REG_BG2X,
                REG_BG3X,
    },
            REG_BGxY = {
                0,
                0,
                REG_BG2Y,
                REG_BG3Y,
    },
            REG_BGxPA = {
                0,
                0,
                REG_BG2PA,
                REG_BG3PA,
    },
            REG_BGxPB = {
                0,
                0,
                REG_BG2PB,
                REG_BG3PB,
    },
            REG_BGxPC = {
                0,
                0,
                REG_BG2PC,
                REG_BG3PC,
    },
            REG_BGxPD = {
                0,
                0,
                REG_BG2PD,
                REG_BG3PD,
    };
    
    // ----- DISPCNT
    
    public int getVideoMode() {
        return (getByte(REG_DISPCNT) & 0x07);
    }
    
    public boolean isFrame1Selected() {
        return ((getByte(REG_DISPCNT) & 0x10) != 0);
    }
    
    public boolean isHBlankIntervalFree() {
        return ((getByte(REG_DISPCNT) & 0x20) != 0);
    }
    
    public boolean isOBJ1DMapping() {
        return ((getByte(REG_DISPCNT) & 0x40) != 0);
    }
    
    public boolean isForcedBlank() {
        return ((getByte(REG_DISPCNT) & 0x80) != 0);
    }
    
    public boolean isBGEnabled(int bgNumber) {
        return ((getByte(REG_DISPCNT + 1) & BGBit[bgNumber]) != 0);
    }
    
    public boolean isWinEnabled(int winNumber) {
        return ((getByte(REG_DISPCNT + 1) & WinBit[winNumber]) != 0);
    }
    
    public boolean isOBJEnabled() {
        return ((getByte(REG_DISPCNT + 1) & 0x10) != 0);
    }
    
    public boolean isOBJWinEnabled() {
        return ((getByte(REG_DISPCNT + 1) & 0x80) != 0);
    }
    
    // ----- DISPSTAT
    
    public void setVBlankFlag(boolean b) {
        byte value = getByte(REG_DISPSTAT);
        if (b) value |= 0x01;
        else value &= ~0x01;
        setByte(REG_DISPSTAT, value);
    }
    
    public void setHBlankFlag(boolean b) {
        byte value = getByte(REG_DISPSTAT);
        if (b) value |= 0x02;
        else value &= ~0x02;
        setByte(REG_DISPSTAT, value);
    }
    
    public boolean isVCounterMatchInterruptEnabled() {
        return ((getByte(REG_DISPSTAT) & 0x20) != 0);
    }
    
    public int getVCountSetting() {
        return (getByte(REG_DISPSTAT + 1) & 0x000000FF);
    }
    
    // ----- VCOUNT
    
    public int getCurrentScanline() {
        return getByte(REG_VCOUNT) & 0x000000FF;
    }
    
    public void setCurrentScanline(int scanline) {
        setByte(REG_VCOUNT, (byte) scanline);
    }
    
    // ----- BGxCNT
    
    public int getPriority(int bgNumber) {
        return (getByte(REG_BGxCNT[bgNumber]) & 0x03);
    }
    
    public int getCharacterBaseAddress(int bgNumber) {
        return ((getByte(REG_BGxCNT[bgNumber]) & 0x0C) << 12);
    }
    
    public int getScreenBaseAddress(int bgNumber) {
        return ((getByte(REG_BGxCNT[bgNumber] + 1) & 0x1F) << 11);
    }
    
    public boolean isMosaicEnabled(int bgNumber) {
        return ((getByte(REG_BGxCNT[bgNumber]) & 0x40) != 0);
    }
    
    public boolean is256ColorPalette(int bgNumber) {
        return ((getByte(REG_BGxCNT[bgNumber]) & 0x80) != 0);
    }
    
    public boolean isWraparoundOverflow(int bgNumber) {
        return ((getByte(REG_BGxCNT[bgNumber] + 1) & 0x20) != 0);
    }
    
    public int getTextModeXSize(int bgNumber) {
        return ((getByte(REG_BGxCNT[bgNumber] + 1) & 0x40) == 0) ? 256 : 512;
    }
    
    public int getTextModeYSize(int bgNumber) {
        return ((getByte(REG_BGxCNT[bgNumber] + 1) & 0x80) == 0) ? 256 : 512;
    }
    
    public int getRotScalModeXYSize(int bgNumber) {
        switch (getByte(REG_BGxCNT[bgNumber] + 1) & 0xC0) {
            case 0x00: return 128;
            case 0x40: return 256;
            case 0x80: return 512;
            case 0xC0: return 1024;
            default:   return 0;
        }
    }
    
    // ----- BGxHOFS
    // ----- BGxVOFS
    
    public int getXOffset(int bgNumber) {
        return (getHalfWord(REG_BGxHOFS[bgNumber]) & 0x01FF);
    }
    
    public int getYOffset(int bgNumber) {
        return (getHalfWord(REG_BGxVOFS[bgNumber]) & 0x01FF);
    }
    
    // ----- BG(2-3)(X-Y)
    // ----- BG(2-3)(PA-PB-PC-PD)
    
    public int getXCoordinate(int bgNumber) {
        return ((getWord(REG_BGxX[bgNumber]) << 4) >> 4);
    }
    
    public int getYCoordinate(int bgNumber) {
        return ((getWord(REG_BGxY[bgNumber]) << 4) >> 4);
    }
    
    public short getPA(int bgNumber) {
        return getHalfWord(REG_BGxPA[bgNumber]);
    }
    
    public short getPB(int bgNumber) {
        return getHalfWord(REG_BGxPB[bgNumber]);
    }
    
    public short getPC(int bgNumber) {
        return getHalfWord(REG_BGxPC[bgNumber]);
    }
    
    public short getPD(int bgNumber) {
        return getHalfWord(REG_BGxPD[bgNumber]);
    }
    
    // ----- MOSAIC
    
    public int getBGMosaicXSize() {
        return ((getByte(REG_MOSAIC) & 0x0F) + 1);
    }
    
    public int getBGMosaicYSize() {
        return (((getByte(REG_MOSAIC) >>> 4) & 0x0F) + 1);
    }
    
    public int getOBJMosaicXSize() {
        return ((getByte(REG_MOSAIC + 1) & 0x0F) + 1);
    }
    
    public int getOBJMosaicYSize() {
        return (((getByte(REG_MOSAIC + 1) >>> 4) & 0x0F) + 1);
    }
    
    // ----- Keypad
    
    private static short keyInput(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_X:          return 0x0001;
            case KeyEvent.VK_C:          return 0x0002;
            case KeyEvent.VK_BACK_SPACE: return 0x0004;
            case KeyEvent.VK_ENTER:      return 0x0008;
            case KeyEvent.VK_RIGHT:      return 0x0010;
            case KeyEvent.VK_LEFT:       return 0x0020;
            case KeyEvent.VK_UP:         return 0x0040;
            case KeyEvent.VK_DOWN:       return 0x0080;
            case KeyEvent.VK_D:          return 0x0100;
            case KeyEvent.VK_S:          return 0x0200;
            default:                     return 0x0000;
        }
    }
    
    public void keyPressed(int keyCode) {
        keyInput &= ~keyInput(keyCode);
        setHalfWord(REG_P1, keyInput);
    }
    
    public void keyReleased(int keyCode) {
        keyInput |= keyInput(keyCode);
        setHalfWord(REG_P1, keyInput);
    }
    
    // ----- Interrupts
    
    public final static short
            VBlankInterruptBit = 0x0001,
            HBlankInterruptBit = 0x0002,
            VCounterMatchInterruptBit = 0x0004;
    
    public boolean isInterruptMasterEnabled() {
        return ((getByte(REG_IME) & 0x01) != 0);
    }
    
    public boolean isInterruptEnabled(short interruptBit) {
        return ((getHalfWord(REG_IE) & interruptBit) != 0);
    }
    
    public void generateInterrupt(short interruptBit) {
        setHalfWord(REG_IF, (short) (getHalfWord(REG_IF) | (getHalfWord(REG_IE) & interruptBit)));
    }
    
    
    public void enterHBlank() {
        int scanline = getCurrentScanline();
        
        // Draw the line
        gfx.drawLine(scanline);
        
        // Enter HBlank
        dma0.signalHBlank();
        dma1.signalHBlank();
        dma2.signalHBlank();
        dma3.signalHBlank();
        generateInterrupt(HBlankInterruptBit);
        setHBlankFlag(true);
        
        // Handle V-Counter Match interrupt
        if (isVCounterMatchInterruptEnabled() && (scanline == getVCountSetting())) {
            generateInterrupt(VCounterMatchInterruptBit);
        }
    }
    
    public void exitHBlank() {
        setHBlankFlag(false);
    }
    
    public void enterVBlank() {
        dma0.signalVBlank();
        dma1.signalVBlank();
        dma2.signalVBlank();
        dma3.signalVBlank();
        generateInterrupt(VBlankInterruptBit);
        setVBlankFlag(true);
    }
    
    public void exitVBlank() {
        setVBlankFlag(false);
    }
}
