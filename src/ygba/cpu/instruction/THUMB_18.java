package ygba.cpu.instruction;

import ygba.cpu.ARM7TDMI;
import ygba.memory.MemoryInterface;
import ygba.util.Hex;

public final class THUMB_18 {
    
    public static void execute(ARM7TDMI cpu, MemoryInterface memory, int opcode) {
        // B label
        int offset = (opcode & 0x03FF) << 1;
        if ((opcode & 0x0400) != 0) offset |= 0xFFFFF800;
        
        cpu.setPC(cpu.getPC() + offset);
        cpu.flushTHUMBPipeline();
    }
    
    
    final static String InstructionName = "b";
    
    public static String disassemble(ARM7TDMI cpu, MemoryInterface memory, int opcode, int offset) {
        int off = (opcode & 0x03FF) << 1;
        if ((opcode & 0x0400) != 0) off |= 0xFFFFF800;
        int label = offset + 4 + off;
        
        return InstructionName + " " + Hex.toAddrString(label, Hex.Word);
    }
    
}
