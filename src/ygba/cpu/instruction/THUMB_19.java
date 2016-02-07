package ygba.cpu.instruction;

import ygba.cpu.ARM7TDMI;
import ygba.memory.MemoryInterface;
import ygba.util.Hex;

public final class THUMB_19 {
    
    public static void execute(ARM7TDMI cpu, MemoryInterface memory, int opcode) {
        int offset = opcode & 0x07FF;
        
        if ((opcode & 0x0800) == 0) {
            // BLL label
            if ((opcode & 0x0400) != 0) offset |= 0xFFFFF800;
            cpu.setLR(cpu.getPC() + (offset << 12));
        } else {
            // BLH label
            int lrValue = cpu.getLR();
            int pcValue = cpu.getCurrentPC();
            cpu.setPC(lrValue + (offset << 1));
            cpu.setLR(pcValue | 0x00000001);
            cpu.flushTHUMBPipeline();
        }
    }
    
    
    final static String InstructionName = "bl";
    
    public static String disassemble(ARM7TDMI cpu, MemoryInterface memory, int opcode, int offset) {
        if ((opcode & 0x0800) == 0) {
            int hi = (((opcode & 0x07FF) << 21) >> 21) << 12;
            int lo = (memory.getHalfWord(offset - 2) & 0x07FF) << 1;
            
            return InstructionName + " " + Hex.toAddrString((hi | lo) + offset + 2, Hex.Word);
        } else {
            
            return InstructionName + "h" + " " + Hex.toAddrString((opcode & 0x07FF) << 1, Hex.HalfWord);
        }
    }
    
}
