package ygba.cpu.instruction;

import ygba.cpu.ARM7TDMI;
import ygba.memory.MemoryInterface;

public final class ARM_12 {
    
    public static void execute(ARM7TDMI cpu, MemoryInterface memory, int opcode) {
        if (!ARMState.isPreconditionSatisfied(cpu, opcode)) return;
        
        int rnIndex = (opcode >>> 16) & 0x0000000F;
        int rnValue = cpu.getRegister(rnIndex);
        int rmIndex = opcode & 0x0000000F;
        int rmValue = cpu.getRegister(rmIndex);
        int rdIndex = (opcode >>> 12) & 0x0000000F;
        
        int value = memory.loadWord(rnValue);
        if ((opcode & 0x00400000) == 0) { // Swap word quantity
            cpu.setRegister(rdIndex, value);
            memory.storeWord(rnValue, rmValue);
        } else { // Swap byte quantity
            cpu.setRegister(rdIndex, value & 0x000000FF);
            memory.storeByte(rnValue, (byte) rmValue);
        }
    }
    
    
    final static String InstructionName = "swp";
    
    public static String disassemble(ARM7TDMI cpu, MemoryInterface memory, int opcode, int offset) {
        String cond = ARMState.getPreconditionSuffix(opcode);
        String bBit = ((opcode & 0x00400000) != 0) ? "b" : "";
        String rn = cpu.getRegisterName((opcode >>> 16) & 0x0000000F);
        String rd = cpu.getRegisterName((opcode >>> 12) & 0x0000000F);
        String rm = cpu.getRegisterName(opcode & 0x0000000F);
        
        return InstructionName + cond + bBit + " " + rd + ", " + rm + ", " + "[" + rn + "]";
    }
    
}
