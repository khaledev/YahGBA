package ygba.cpu.instruction;

import ygba.cpu.ARM7TDMI;
import ygba.memory.MemoryInterface;
import ygba.util.Hex;

public final class ARM_13 {
    
    public static void execute(ARM7TDMI cpu, MemoryInterface memory, int opcode) {
        if (!ARMState.isPreconditionSatisfied(cpu, opcode)) return;
        
        cpu.generateSoftwareInterrupt(cpu.getCurrentPC());
    }
    
    
    final static String InstructionName = "swi";
    
    public static String disassemble(ARM7TDMI cpu, MemoryInterface memory, int opcode, int offset) {
        String cond = ARMState.getPreconditionSuffix(opcode);
        
        return InstructionName + cond + " " + Hex.toHexString(opcode & 0x00FFFFFF, Hex.Bit_24);
    }
    
}
