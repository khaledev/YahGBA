package ygba.cpu.instruction;

import ygba.cpu.ARM7TDMI;
import ygba.memory.MemoryInterface;
import ygba.util.Hex;

public final class THUMB_13 {
    
    public static void execute(ARM7TDMI cpu, MemoryInterface memory, int opcode) {
        int spValue = cpu.getSP();
        int offset = (opcode & 0x007F) << 2;
        
        if ((opcode & 0x0080) != 0) offset = -offset;
        // ADD SP, #nn
        cpu.setSP(spValue + offset);
    }
    
    
    final static String InstructionName = "add";
    
    public static String disassemble(ARM7TDMI cpu, MemoryInterface memory, int opcode, int offset) {
        String sp = cpu.getRegisterName(cpu.SP);
        String sign = ((opcode & 0x0080) != 0) ? "-" : "";
        int off = (opcode & 0x007F) << 2;
        
        return InstructionName + " " + sp + ", " + sign + Hex.toHexString(off, Hex.Bit_12);
    }
    
}
