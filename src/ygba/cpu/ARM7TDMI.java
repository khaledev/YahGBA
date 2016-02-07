package ygba.cpu;

import ygba.memory.MemoryInterface;
import ygba.memory.IORegMemory;
import ygba.cpu.instruction.ARM_10;
import ygba.cpu.instruction.ARM_11;
import ygba.cpu.instruction.ARM_12;
import ygba.cpu.instruction.ARM_13;
import ygba.cpu.instruction.ARM_17;
import ygba.cpu.instruction.ARM_3;
import ygba.cpu.instruction.ARM_4;
import ygba.cpu.instruction.ARM_5;
import ygba.cpu.instruction.ARM_6;
import ygba.cpu.instruction.ARM_7;
import ygba.cpu.instruction.ARM_8;
import ygba.cpu.instruction.ARM_9;
import ygba.cpu.instruction.ARM_CP;
import ygba.cpu.instruction.THUMB_1;
import ygba.cpu.instruction.THUMB_10;
import ygba.cpu.instruction.THUMB_11;
import ygba.cpu.instruction.THUMB_12;
import ygba.cpu.instruction.THUMB_13;
import ygba.cpu.instruction.THUMB_14;
import ygba.cpu.instruction.THUMB_15;
import ygba.cpu.instruction.THUMB_16;
import ygba.cpu.instruction.THUMB_17;
import ygba.cpu.instruction.THUMB_18;
import ygba.cpu.instruction.THUMB_19;
import ygba.cpu.instruction.THUMB_2;
import ygba.cpu.instruction.THUMB_3;
import ygba.cpu.instruction.THUMB_4;
import ygba.cpu.instruction.THUMB_5;
import ygba.cpu.instruction.THUMB_6;
import ygba.cpu.instruction.THUMB_7;
import ygba.cpu.instruction.THUMB_8;
import ygba.cpu.instruction.THUMB_9;
import ygba.cpu.instruction.THUMB_Und;

public final class ARM7TDMI {
    
    public final static int
            USRMode = 0x00000010,
            SYSMode = 0x0000001F,
            FIQMode = 0x00000011,
            SVCMode = 0x00000013,
            ABTMode = 0x00000017,
            IRQMode = 0x00000012,
            UNDMode = 0x0000001B;
    
    public final static int
            ResetVector                = 0x00000000,
            UndefinedInstructionVector = 0x00000004,
            PrefetchAbortVector        = 0x0000000C,
            DataAbortVector            = 0x00000010,
            NormalInterruptVector      = 0x00000018,
            FastInterruptVector        = 0x0000001C,
            SoftwareInterruptVector    = 0x00000008;
    
    public final static int
            IMEAddress      = 0x04000000 + IORegMemory.REG_IME,
            IEAddress       = 0x04000000 + IORegMemory.REG_IE,
            IFAddress       = 0x04000000 + IORegMemory.REG_IF,
            ROMStartAddress = 0x08000000;
    
    public int[] registers;
    
    public final static byte
            R0 = 0,
            R1 = 1,
            R2 = 2,
            R3 = 3,
            R4 = 4,
            R5 = 5,
            R6 = 6,
            R7 = 7,
            R8 = 8, R8_fiq = 17,
            R9 = 9, R9_fiq = 18,
            R10 = 10, R10_fiq = 19,
            R11 = 11, R11_fiq = 20,
            R12 = 12, R12_fiq = 21,
            R13 = 13, R13_fiq = 22, R13_svc = 25, R13_abt = 28, R13_irq = 31, R13_und = 34,
            R14 = 14, R14_fiq = 23, R14_svc = 26, R14_abt = 29, R14_irq = 32, R14_und = 35,
            R15 = 15,
            CPSR = 16, // Non utilisé
            SPSR_fiq = 24, SPSR_svc = 27, SPSR_abt = 30, SPSR_irq = 33, SPSR_und = 36,
            SPSR_null = -1,
            SP = R13,
            LR = R14,
            PC = R15;
    public static byte
            SPSR;
    
    private final static byte NB_REGS = 37;
    
    public int mFlag;
    public boolean
            tFlag,
            fFlag,
            iFlag,
            vFlag,
            cFlag,
            nFlag,
            zFlag;
    
    private final static int
            MMask = 0x0000001F,
            TMask = 0x00000020,
            FMask = 0x00000040,
            IMask = 0x00000080,
            VMask = 0x10000000,
            CMask = 0x20000000,
            ZMask = 0x40000000,
            NMask = 0x80000000;
    
    private int pipelineStage1, pipelineStage2;
    
    private MemoryInterface memory;
    
    
    public ARM7TDMI() {
        // Allouer l'espace dédié aux registres
        registers = new int[NB_REGS];
        // Initialiser les tables de décodage
        initTHUMB();
        initARM();
    }
    
    public void connectToMemory(MemoryInterface memory) {
        this.memory = memory;
    }
    
    // ----- Gestion des registres -----
    
    public int getSP() { return registers[SP]; }
    public void setSP(int value) { registers[SP] = value; }
    
    public int getLR() { return registers[LR]; }
    public void setLR(int value) { registers[LR] = value; }
    
    public int getPC() { return registers[PC]; }
    public int getCurrentPC() { return registers[PC] - (tFlag ? 2 : 4); }
    public void setPC(int value) { registers[PC] = value; }
    
    public int getCPSR() {
        int cpsr = 0x00000000;
        cpsr = setBit(cpsr, mFlag, true);
        cpsr = setBit(cpsr, TMask, tFlag);
        cpsr = setBit(cpsr, FMask, fFlag);
        cpsr = setBit(cpsr, IMask, iFlag);
        cpsr = setBit(cpsr, VMask, vFlag);
        cpsr = setBit(cpsr, CMask, cFlag);
        cpsr = setBit(cpsr, ZMask, zFlag);
        cpsr = setBit(cpsr, NMask, nFlag);
        return cpsr;
    }
    
    public void setCPSR(int value) {
        setMode(value & MMask);
        tFlag = ((value & TMask) != 0);
        fFlag = ((value & FMask) != 0);
        iFlag = ((value & IMask) != 0);
        vFlag = ((value & VMask) != 0);
        cFlag = ((value & CMask) != 0);
        zFlag = ((value & ZMask) != 0);
        nFlag = ((value & NMask) != 0);
    }
    
    public int getSPSR() { return registers[SPSR]; }
    public void setSPSR(int value) { registers[SPSR] = value; }
    
    public int getRegister(int registerIndex) { return registers[registerIndex]; }
    public void setRegister(int registerIndex, int value) { registers[registerIndex] = value; }
    
    public String getRegisterName(int registerIndex) {
        switch (registerIndex) {
            case SP: return "sp";
            case LR: return "lr";
            case PC: return "pc";
            case 16: return "cpsr";
            case 17: return "spsr";
            default: return "r" + registerIndex;
        }
    }
    
    public void swapRegisters(int registerIndex1, int registerIndex2) {
        int regtemp = registers[registerIndex1];
        registers[registerIndex1] = registers[registerIndex2];
        registers[registerIndex2] = regtemp;
    }
    
    // ----- Manipulation des bits d'un nombre entier -----
    
    private static int setBit(int reg, int mask, boolean condition) {
        return (condition ? (reg | mask) : (reg & ~mask));
    }
    
    // ----- Gestion des drapeaux -----
    
    public int getMode() { return mFlag; }
    public void setMode(int mode) {
        int oldMode = mFlag;
        int newMode = mode;
        
        if (newMode != oldMode) {
            // Sauvegarder le contexte des registres de l'ancien mode
            switch (oldMode) {
                case FIQMode:
                    swapRegisters(R8_fiq, R8);
                    swapRegisters(R9_fiq, R9);
                    swapRegisters(R10_fiq, R10);
                    swapRegisters(R11_fiq, R11);
                    swapRegisters(R12_fiq, R12);
                    swapRegisters(R13_fiq, R13);
                    swapRegisters(R14_fiq, R14);
                    break;
                case SVCMode:
                    swapRegisters(R13_svc, R13);
                    swapRegisters(R14_svc, R14);
                    break;
                case ABTMode:
                    swapRegisters(R13_abt, R13);
                    swapRegisters(R14_abt, R14);
                    break;
                case IRQMode:
                    swapRegisters(R13_irq, R13);
                    swapRegisters(R14_irq, R14);
                    break;
                case UNDMode:
                    swapRegisters(R13_und, R13);
                    swapRegisters(R14_und, R14);
                    break;
            }
            
            // Installer le contexte des registres du nouveau mode
            switch (newMode) {
                case USRMode:
                case SYSMode:
                    SPSR = SPSR_null;
                    break;
                case FIQMode:
                    swapRegisters(R8, R8_fiq);
                    swapRegisters(R9, R9_fiq);
                    swapRegisters(R10, R10_fiq);
                    swapRegisters(R11, R11_fiq);
                    swapRegisters(R12, R12_fiq);
                    swapRegisters(R13, R13_fiq);
                    swapRegisters(R14, R14_fiq);
                    SPSR = SPSR_fiq;
                    break;
                case SVCMode:
                    swapRegisters(R13, R13_svc);
                    swapRegisters(R14, R14_svc);
                    SPSR = SPSR_svc;
                    break;
                case ABTMode:
                    swapRegisters(R13, R13_abt);
                    swapRegisters(R14, R14_abt);
                    SPSR = SPSR_abt;
                    break;
                case IRQMode:
                    swapRegisters(R13, R13_irq);
                    swapRegisters(R14, R14_irq);
                    SPSR = SPSR_irq;
                    break;
                case UNDMode:
                    swapRegisters(R13, R13_und);
                    swapRegisters(R14, R14_und);
                    SPSR = SPSR_und;
                    break;
            }
            
            mFlag = newMode;
        }
    }
    
    public String getModeName() {
        switch (mFlag) {
            case USRMode: return "usr";
            case SYSMode: return "sys";
            case FIQMode: return "fiq";
            case SVCMode: return "svc";
            case ABTMode: return "abt";
            case IRQMode: return "irq";
            case UNDMode: return "und";
            default:      return "";
        }
    }
    
    public boolean getTFlag() { return tFlag; }
    public void setTFlag(boolean b) { tFlag = b; }
    
    public boolean getFFlag() { return fFlag; }
    public void setFFlag(boolean b) { fFlag = b; }
    
    public boolean getIFlag() { return iFlag; }
    public void setIFlag(boolean b) { iFlag = b; }
    
    public boolean getVFlag() { return vFlag; }
    public void setVFlag(boolean b) { vFlag = b; }
    
    public boolean getCFlag() { return cFlag; }
    public void setCFlag(boolean b) { cFlag = b; }
    
    public boolean getNFlag() { return nFlag; }
    public void setNFlag(boolean b) { nFlag = b; }
    
    public boolean getZFlag() { return zFlag; }
    public void setZFlag(boolean b) { zFlag = b; }
    
    public void setVCFlagsForADD(int operand1, int operand2, int result) {
        boolean op1 = (operand1 < 0);
        boolean op2 = (operand2 < 0);
        boolean res = (result < 0);
        
        vFlag = (op1 && op2 && !res) || (!op1 && !op2 && res);
        cFlag = (op1 && op2) || (op1 && !res) || (op2 && !res);
    }
    
    public void setVCFlagsForSUB(int operand1, int operand2, int result) {
        boolean op1 = (operand1 < 0);
        boolean op2 = (operand2 < 0);
        boolean res = (result < 0);
        
        vFlag = (op1 && !op2 && !res) || (!op1 && op2 && res);
        cFlag = (op1 && !op2) || (op1 && !res) || (!op2 && !res);
    }
    
    // ----- Gestion des interruptions -----
    
    private void generateInterrupt(int newMode, int interruptVector, int pcValue) {
        int oldCPSR = getCPSR();
        setMode(newMode);
        tFlag = false;
        iFlag = true;
        setSPSR(oldCPSR);
        setLR(pcValue);
        setPC(interruptVector);
        flushARMPipeline();
    }
    
    public void generateNormalInterrupt(int pcValue) {
        generateInterrupt(IRQMode, NormalInterruptVector, pcValue);
    }
    
    public void generateSoftwareInterrupt(int pcValue) {
        generateInterrupt(SVCMode, SoftwareInterruptVector, pcValue);
    }
    
    public void generateUndefinedInstructionInterrupt(int pcValue) {
        generateInterrupt(UNDMode, UndefinedInstructionVector, pcValue);
    }
    
    // ----- Fonctions élémentaires du noyau CPU -----
    
    public final static int CyclesPerInstruction = 4;
    
    public void reset() {
        // Initialiser les registres
        for (byte i = 0; i < NB_REGS; i++) {
            registers[i] = 0;
        }
        // Initialiser les drapeaux
        mFlag = 0;
        tFlag = vFlag = cFlag = zFlag = nFlag = false;
        fFlag = iFlag = true;
        setMode(SVCMode);
        // Initialiser le PC
        setPC(ResetVector);
        
        // Sauter l'exécution du BIOS
        iFlag = false;
        setMode(SYSMode);
        setPC(ROMStartAddress);
        setRegister(R13, 0x03007F00);
        setRegister(R13_svc, 0x03007FE0);
        setRegister(R13_irq, 0x03007FA0);
        
        flushPipeline();
    }
    
    public void run(int cycles) {
        while (cycles > 0) {
            if (!iFlag &&
                (memory.getByte(IMEAddress) != 0) &&
                ((memory.getHalfWord(IEAddress) & memory.getHalfWord(IFAddress)) != 0)) {
                generateNormalInterrupt(getPC() + (tFlag ? 2 : 0)); // (getPC() + 2) & 0xFFFFFFFC
            } else {
                int opcode;
                byte instruction;
                if (tFlag) { // THUMB state
                    opcode = fetchTHUMB();
                    instruction = decodeTHUMB(opcode);
                    executeTHUMB(opcode, instruction);
                } else { // ARM state
                    opcode = fetchARM();
                    instruction = decodeARM(opcode);
                    executeARM(opcode, instruction);
                }
            }
            cycles -= CyclesPerInstruction;
        }
    }
    
    // ----- Gestion du Pipeline -----
    
    public void flushPipeline() {
        if (tFlag) flushTHUMBPipeline();
        else flushARMPipeline();
    }
    
    public void flushTHUMBPipeline() {
        registers[PC] &= 0xFFFFFFFE;
        pipelineStage1 = memory.getHalfWord(registers[PC]) & 0xFFFF;
        registers[PC] += 2;
        pipelineStage2 = memory.getHalfWord(registers[PC]) & 0xFFFF;
    }
    
    public void flushARMPipeline() {
        registers[PC] &= 0xFFFFFFFC;
        pipelineStage1 = memory.getWord(registers[PC]);
        registers[PC] += 4;
        pipelineStage2 = memory.getWord(registers[PC]);
    }
    
    // ----- Décodage des instructions -----
    
    private final static byte
            THUMBInstructionFormat1   = 0x01,
            THUMBInstructionFormat2   = 0x02,
            THUMBInstructionFormat3   = 0x03,
            THUMBInstructionFormat4   = 0x04,
            THUMBInstructionFormat5   = 0x05,
            THUMBInstructionFormat6   = 0x06,
            THUMBInstructionFormat7   = 0x07,
            THUMBInstructionFormat8   = 0x08,
            THUMBInstructionFormat9   = 0x09,
            THUMBInstructionFormat10  = 0x0A,
            THUMBInstructionFormat11  = 0x0B,
            THUMBInstructionFormat12  = 0x0C,
            THUMBInstructionFormat13  = 0x0D,
            THUMBInstructionFormat14  = 0x0E,
            THUMBInstructionFormat15  = 0x0F,
            THUMBInstructionFormat16  = 0x10,
            THUMBInstructionFormat17  = 0x11,
            THUMBInstructionFormat18  = 0x12,
            THUMBInstructionFormat19  = 0x13,
            THUMBInstructionFormatUnd = 0x7F;
    
    private static byte[] thumbInstruction;
    
    private static void initTHUMB() {
        thumbInstruction = new byte[0x100];
        byte instruction;
        
        for (short opcode = 0; opcode < thumbInstruction.length; opcode++) {
            if      ((opcode & 0x00F8) == 0x0018) instruction = THUMBInstructionFormat2;
            else if ((opcode & 0x00E0) == 0x0000) instruction = THUMBInstructionFormat1;
            else if ((opcode & 0x00E0) == 0x0020) instruction = THUMBInstructionFormat3;
            else if ((opcode & 0x00FC) == 0x0040) instruction = THUMBInstructionFormat4;
            else if ((opcode & 0x00FC) == 0x0044) instruction = THUMBInstructionFormat5;
            else if ((opcode & 0x00F8) == 0x0048) instruction = THUMBInstructionFormat6;
            else if ((opcode & 0x00F2) == 0x0050) instruction = THUMBInstructionFormat7;
            else if ((opcode & 0x00F2) == 0x0052) instruction = THUMBInstructionFormat8;
            else if ((opcode & 0x00E0) == 0x0060) instruction = THUMBInstructionFormat9;
            else if ((opcode & 0x00F0) == 0x0080) instruction = THUMBInstructionFormat10;
            else if ((opcode & 0x00F0) == 0x0090) instruction = THUMBInstructionFormat11;
            else if ((opcode & 0x00F0) == 0x00A0) instruction = THUMBInstructionFormat12;
            else if ((opcode & 0x00FF) == 0x00B0) instruction = THUMBInstructionFormat13;
            else if ((opcode & 0x00F6) == 0x00B4) instruction = THUMBInstructionFormat14;
            else if ((opcode & 0x00F0) == 0x00C0) instruction = THUMBInstructionFormat15;
            else if ((opcode & 0x00FF) == 0x00DF) instruction = THUMBInstructionFormat17;
            else if ((opcode & 0x00F0) == 0x00D0) instruction = THUMBInstructionFormat16;
            else if ((opcode & 0x00F8) == 0x00E0) instruction = THUMBInstructionFormat18;
            else if ((opcode & 0x00F0) == 0x00F0) instruction = THUMBInstructionFormat19;
            else instruction = THUMBInstructionFormatUnd;
            
            thumbInstruction[opcode] = instruction;
        }
    }
    
    private int fetchTHUMB() {
        registers[PC] += 2;
        int opcode = pipelineStage1;
        pipelineStage1 = pipelineStage2;
        pipelineStage2 = memory.getHalfWord(registers[PC]) & 0xFFFF;
        return opcode;
    }
    
    private static byte decodeTHUMB(int opcode) {
        int offset = opcode >>> 8;
        return thumbInstruction[offset];
    }
    
    private void executeTHUMB(int opcode, byte instruction) {
        switch (instruction) {
            case THUMBInstructionFormat1:   THUMB_1.execute(this, memory, opcode);   break;
            case THUMBInstructionFormat2:   THUMB_2.execute(this, memory, opcode);   break;
            case THUMBInstructionFormat3:   THUMB_3.execute(this, memory, opcode);   break;
            case THUMBInstructionFormat4:   THUMB_4.execute(this, memory, opcode);   break;
            case THUMBInstructionFormat5:   THUMB_5.execute(this, memory, opcode);   break;
            case THUMBInstructionFormat6:   THUMB_6.execute(this, memory, opcode);   break;
            case THUMBInstructionFormat7:   THUMB_7.execute(this, memory, opcode);   break;
            case THUMBInstructionFormat8:   THUMB_8.execute(this, memory, opcode);   break;
            case THUMBInstructionFormat9:   THUMB_9.execute(this, memory, opcode);   break;
            case THUMBInstructionFormat10:  THUMB_10.execute(this, memory, opcode);  break;
            case THUMBInstructionFormat11:  THUMB_11.execute(this, memory, opcode);  break;
            case THUMBInstructionFormat12:  THUMB_12.execute(this, memory, opcode);  break;
            case THUMBInstructionFormat13:  THUMB_13.execute(this, memory, opcode);  break;
            case THUMBInstructionFormat14:  THUMB_14.execute(this, memory, opcode);  break;
            case THUMBInstructionFormat15:  THUMB_15.execute(this, memory, opcode);  break;
            case THUMBInstructionFormat16:  THUMB_16.execute(this, memory, opcode);  break;
            case THUMBInstructionFormat17:  THUMB_17.execute(this, memory, opcode);  break;
            case THUMBInstructionFormat18:  THUMB_18.execute(this, memory, opcode);  break;
            case THUMBInstructionFormat19:  THUMB_19.execute(this, memory, opcode);  break;
            case THUMBInstructionFormatUnd: THUMB_Und.execute(this, memory, opcode); break;
        }
    }
    
    public String disassembleTHUMB(int offset) {
        int opcode = memory.getHalfWord(offset) & 0xFFFF;
        byte instruction = decodeTHUMB(opcode);
        
        switch (instruction) {
            case THUMBInstructionFormat1:   return THUMB_1.disassemble(this, memory, opcode, offset);
            case THUMBInstructionFormat2:   return THUMB_2.disassemble(this, memory, opcode, offset);
            case THUMBInstructionFormat3:   return THUMB_3.disassemble(this, memory, opcode, offset);
            case THUMBInstructionFormat4:   return THUMB_4.disassemble(this, memory, opcode, offset);
            case THUMBInstructionFormat5:   return THUMB_5.disassemble(this, memory, opcode, offset);
            case THUMBInstructionFormat6:   return THUMB_6.disassemble(this, memory, opcode, offset);
            case THUMBInstructionFormat7:   return THUMB_7.disassemble(this, memory, opcode, offset);
            case THUMBInstructionFormat8:   return THUMB_8.disassemble(this, memory, opcode, offset);
            case THUMBInstructionFormat9:   return THUMB_9.disassemble(this, memory, opcode, offset);
            case THUMBInstructionFormat10:  return THUMB_10.disassemble(this, memory, opcode, offset);
            case THUMBInstructionFormat11:  return THUMB_11.disassemble(this, memory, opcode, offset);
            case THUMBInstructionFormat12:  return THUMB_12.disassemble(this, memory, opcode, offset);
            case THUMBInstructionFormat13:  return THUMB_13.disassemble(this, memory, opcode, offset);
            case THUMBInstructionFormat14:  return THUMB_14.disassemble(this, memory, opcode, offset);
            case THUMBInstructionFormat15:  return THUMB_15.disassemble(this, memory, opcode, offset);
            case THUMBInstructionFormat16:  return THUMB_16.disassemble(this, memory, opcode, offset);
            case THUMBInstructionFormat17:  return THUMB_17.disassemble(this, memory, opcode, offset);
            case THUMBInstructionFormat18:  return THUMB_18.disassemble(this, memory, opcode, offset);
            case THUMBInstructionFormat19:  return THUMB_19.disassemble(this, memory, opcode, offset);
            case THUMBInstructionFormatUnd: return THUMB_Und.disassemble(this, memory, opcode, offset);
            default: return "";
        }
    }
    
    private final static byte
            ARMInstructionFormat3 = 0x03,
            ARMInstructionFormat4 = 0x04,
            ARMInstructionFormat5 = 0x05,
            ARMInstructionFormat6 = 0x06,
            ARMInstructionFormat7 = 0x07,
            ARMInstructionFormat8 = 0x08,
            ARMInstructionFormat9 = 0x09,
            ARMInstructionFormat10 = 0x0A,
            ARMInstructionFormat11 = 0x0B,
            ARMInstructionFormat12 = 0x0C,
            ARMInstructionFormat13 = 0x0D,
            ARMInstructionFormat17 = 0x11,
            ARMInstructionFormatCoP = 0x12;
    
    private static byte[] armInstruction;
    
    private static void initARM() {
        armInstruction = new byte[0x10000];
        byte instruction;
        
        for (int i = 0; i < armInstruction.length; i++) {
            int opcode = ((i & 0x0000FF00) << 12) | ((i & 0x000000FF) << 4); // Se baser sur les bits (20-27) et (4-11)
            
            switch ((opcode >>> 25) & 0x00000007) {
                case 0x00:
                    if ((opcode & 0x0FC000F0) == 0x00000090) {
                        instruction = ARMInstructionFormat7;
                        break;
                    } else if ((opcode & 0x0F8000F0) == 0x00800090) {
                        instruction = ARMInstructionFormat8;
                        break;
                    } else if ((opcode & 0x0F0000F0) == 0x01000090) {
                        instruction = ARMInstructionFormat12;
                        break;
                    } else if ((opcode & 0x0E000090) == 0x00000090) {
                        instruction = ARMInstructionFormat10;
                        break;
                    } else if ((opcode & 0x0FF00FF0) == 0x01200F10) {
                        instruction = ARMInstructionFormat3;
                        break;
                    }
                    
                case 0x01:
                    if (((opcode & 0x0FB00FF0) == 0x01000000) || // MRS
                            ((opcode & 0x0FB00FF0) == 0x01200000) || // MSR (I=0)
                            ((opcode & 0x0FB00000) == 0x03200000)) { // MSR (I=1)
                        instruction = ARMInstructionFormat6;
                    } else {
                        instruction = ARMInstructionFormat5;
                    }
                    break;
                    
                case 0x02:
                case 0x03:
                    if ((opcode & 0x0E000010) == 0x06000010) {
                        instruction = ARMInstructionFormat17;
                    } else {
                        instruction = ARMInstructionFormat9;
                    }
                    break;
                    
                case 0x04:
                    instruction = ARMInstructionFormat11;
                    break;
                    
                case 0x05:
                    instruction = ARMInstructionFormat4;
                    break;
                    
                case 0x06:
                case 0x07:
                    if (((opcode & 0x0E000000) == 0x0C000000) ||
                            ((opcode & 0x0F000000) == 0x0E000000)) {
                        instruction = ARMInstructionFormatCoP;
                    } else {
                        instruction = ARMInstructionFormat13;
                    }
                    break;
                    
                default:
                    instruction = ARMInstructionFormat17;
                    break;
            }
            
            armInstruction[i] = instruction;
        }
    }
    
    private int fetchARM() {
        registers[PC] += 4;
        int opcode = pipelineStage1;
        pipelineStage1 = pipelineStage2;
        pipelineStage2 = memory.getWord(registers[PC]);
        return opcode;
    }
    
    private static byte decodeARM(int opcode) {
        int offset = ((opcode >>> 12) & 0x0000FF00) | ((opcode >>> 4) & 0x000000FF);
        return armInstruction[offset];
    }
    
    private void executeARM(int opcode, byte instruction) {
        switch (instruction) {
            case ARMInstructionFormat3:   ARM_3.execute(this, memory, opcode);   break;
            case ARMInstructionFormat4:   ARM_4.execute(this, memory, opcode);   break;
            case ARMInstructionFormat5:   ARM_5.execute(this, memory, opcode);   break;
            case ARMInstructionFormat6:   ARM_6.execute(this, memory, opcode);   break;
            case ARMInstructionFormat7:   ARM_7.execute(this, memory, opcode);   break;
            case ARMInstructionFormat8:   ARM_8.execute(this, memory, opcode);   break;
            case ARMInstructionFormat9:   ARM_9.execute(this, memory, opcode);   break;
            case ARMInstructionFormat10:  ARM_10.execute(this, memory, opcode);  break;
            case ARMInstructionFormat11:  ARM_11.execute(this, memory, opcode);  break;
            case ARMInstructionFormat12:  ARM_12.execute(this, memory, opcode);  break;
            case ARMInstructionFormat13:  ARM_13.execute(this, memory, opcode);  break;
            case ARMInstructionFormat17:  ARM_17.execute(this, memory, opcode);  break;
            case ARMInstructionFormatCoP: ARM_CP.execute(this, memory, opcode); break;
        }
    }
    
    public String disassembleARM(int offset) {
        int opcode = memory.getWord(offset);
        byte instruction = decodeARM(opcode);
        
        switch (instruction) {
            case ARMInstructionFormat3:   return ARM_3.disassemble(this, memory, opcode, offset);
            case ARMInstructionFormat4:   return ARM_4.disassemble(this, memory, opcode, offset);
            case ARMInstructionFormat5:   return ARM_5.disassemble(this, memory, opcode, offset);
            case ARMInstructionFormat6:   return ARM_6.disassemble(this, memory, opcode, offset);
            case ARMInstructionFormat7:   return ARM_7.disassemble(this, memory, opcode, offset);
            case ARMInstructionFormat8:   return ARM_8.disassemble(this, memory, opcode, offset);
            case ARMInstructionFormat9:   return ARM_9.disassemble(this, memory, opcode, offset);
            case ARMInstructionFormat10:  return ARM_10.disassemble(this, memory, opcode, offset);
            case ARMInstructionFormat11:  return ARM_11.disassemble(this, memory, opcode, offset);
            case ARMInstructionFormat12:  return ARM_12.disassemble(this, memory, opcode, offset);
            case ARMInstructionFormat13:  return ARM_13.disassemble(this, memory, opcode, offset);
            case ARMInstructionFormat17:  return ARM_17.disassemble(this, memory, opcode, offset);
            case ARMInstructionFormatCoP: return ARM_CP.disassemble(this, memory, opcode, offset);
            default: return "";
        }
    }
    
}
