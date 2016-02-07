package ygba.cpu.instruction;

import ygba.cpu.ARM7TDMI;
import ygba.memory.MemoryInterface;

public final class ARM_CP {
    
    public static void execute(ARM7TDMI cpu, MemoryInterface memory, int opcode) {
        
    }
    
    
    final static String InstructionName = "[ cp ]";
    
    public static String disassemble(ARM7TDMI cpu, MemoryInterface memory, int opcode, int offset) {
        return InstructionName;
    }
}
