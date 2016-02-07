package ygba.cpu.instruction;

import ygba.cpu.ARM7TDMI;
import ygba.memory.MemoryInterface;
import ygba.util.Hex;

public final class THUMB_11 {
    
    public static void execute(ARM7TDMI cpu, MemoryInterface memory, int opcode) {
        int rdIndex = (opcode >>> 8) & 0x0007;
        int spValue = cpu.getSP();
        int offset = (opcode & 0x00FF) << 2;
        
        if ((opcode & 0x0800) == 0) { // STR Rd, [SP, #nn]
            memory.storeWord(spValue + offset, cpu.getRegister(rdIndex));
        } else { // LDR Rd, [SP, #nn]
            cpu.setRegister(rdIndex, memory.loadWord(spValue + offset));
        }
    }
    
    
    final static String[] InstructionName = {
        "str",
        "ldr",
    };
    
    public static String disassemble(ARM7TDMI cpu, MemoryInterface memory, int opcode, int offset) {
        int instruction = (opcode >>> 11) & 0x0001;
        String rd = cpu.getRegisterName((opcode >>> 8) & 0x0007);
        String sp = cpu.getRegisterName(13);
        int off = (opcode & 0x00FF) << 2;
        
        return InstructionName[instruction] + " " + rd + ", [" + sp + ", " + Hex.toHexString(off, Hex.Bit_12) + "]";
    }
    
}
