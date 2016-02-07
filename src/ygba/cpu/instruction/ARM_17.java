package ygba.cpu.instruction;

import ygba.cpu.ARM7TDMI;
import ygba.memory.MemoryInterface;

public final class ARM_17 {
    
    public static void execute(ARM7TDMI cpu, MemoryInterface memory, int opcode) {
        cpu.generateUndefinedInstructionInterrupt(cpu.getCurrentPC());
    }
    
    
    final static String InstructionName = "[ undefined ]";
    
    public static String disassemble(ARM7TDMI cpu, MemoryInterface memory, int opcode, int offset) {
        return InstructionName;
    }
    
}
