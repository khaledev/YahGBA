package ygba.cpu.instruction;

import ygba.cpu.ARM7TDMI;
import ygba.memory.MemoryInterface;
import ygba.util.Hex;

public final class ARM_4 {
    
    public static void execute(ARM7TDMI cpu, MemoryInterface memory, int opcode) {
        if (!ARMState.isPreconditionSatisfied(cpu, opcode)) return;
        
        int offset = opcode & 0x00FFFFFF;
        if ((offset & 0x00800000) != 0) offset |= 0xFF000000;
        offset <<= 2;
        
        if ((opcode & 0x01000000) != 0) cpu.setLR(cpu.getCurrentPC());
        cpu.setPC(cpu.getPC() + offset);
        cpu.flushARMPipeline();
    }
    
    
    final static String[] InstructionName = {
        "b",
        "bl",
    };
    
    public static String disassemble(ARM7TDMI cpu, MemoryInterface memory, int opcode, int offset) {
        int instruction = (opcode & 0x01000000) >>> 24;
        String cond = ARMState.getPreconditionSuffix(opcode);
        int off = opcode & 0x00FFFFFF;
        if ((off & 0x00800000) != 0) off |= 0xFF000000;
        off <<= 2;
        
        return InstructionName[instruction] + cond + " " + Hex.toAddrString(offset + 8 + off, Hex.Word);
    }
    
}
