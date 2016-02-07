package ygba.cpu.instruction;

import ygba.cpu.ARM7TDMI;
import ygba.memory.MemoryInterface;
import ygba.util.Hex;

public final class THUMB_12 {
    
    public static void execute(ARM7TDMI cpu, MemoryInterface memory, int opcode) {
        int rdIndex = (opcode >>> 8) & 0x0007;
        int operand2 = ((opcode & 0x0800) == 0) ? (cpu.getPC() & 0xFFFFFFFC) : cpu.getSP();
        int offset = (opcode & 0x00FF) << 2;
        
        cpu.setRegister(rdIndex, operand2 + offset); // ADD Rd, PC/SP, #nn
    }
    
    
    final static String InstructionName = "add";
    
    public static String disassemble(ARM7TDMI cpu, MemoryInterface memory, int opcode, int offset) {
        String rd = cpu.getRegisterName((opcode >>> 8) & 0x0007);
        String rs = cpu.getRegisterName(((opcode & 0x0800) == 0) ? cpu.PC : cpu.SP);
        int off = (opcode & 0x00FF) << 2;
        
        return InstructionName + " " + rd + ", " + rs + ", " + Hex.toHexString(off, Hex.Bit_12);
    }
    
}
