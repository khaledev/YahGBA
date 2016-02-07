package ygba.cpu.instruction;

import ygba.cpu.ARM7TDMI;
import ygba.memory.MemoryInterface;

public final class THUMB_5 {
    
    public static void execute(ARM7TDMI cpu, MemoryInterface memory, int opcode) {
        int rsIndex = (opcode >>> 3) & 0x000F;
        int rsValue = cpu.getRegister(rsIndex);
        int rdIndex, rdValue;
        
        switch (opcode & 0x0300) {
            case 0x0000: // ADD Rd, Rs
                rdIndex = (opcode & 0x0007) | ((opcode & 0x0080) >>> 4);
                rdValue = cpu.getRegister(rdIndex);
                cpu.setRegister(rdIndex, rdValue + rsValue);
                if (rdIndex == cpu.PC) cpu.flushTHUMBPipeline();
                break;
                
            case 0x0100: // CMP Rd, Rs
                rdIndex = (opcode & 0x0007) | ((opcode & 0x0080) >>> 4);
                rdValue = cpu.getRegister(rdIndex);
                int result = rdValue - rsValue;
                cpu.setVCFlagsForSUB(rdValue, rsValue, result);
                cpu.setZFlag(result == 0);
                cpu.setNFlag(result < 0);
                break;
                
            case 0x0200: // MOV Rd, Rs
                rdIndex = (opcode & 0x0007) | ((opcode & 0x0080) >>> 4);
                cpu.setRegister(rdIndex, rsValue);
                if (rdIndex == cpu.PC) cpu.flushTHUMBPipeline();
                break;
                
            case 0x0300: // BX Rs
                cpu.setTFlag((rsValue & 0x00000001) != 0);
                cpu.setPC(rsValue);
                cpu.flushPipeline();
                break;
                
            default: // Unknown
        }
    }
    
    
    final static String[] InstructionName = {
        "add",
        "cmp",
        "mov",
        "bx",
    };
    
    public static String disassemble(ARM7TDMI cpu, MemoryInterface memory, int opcode, int offset) {
        int instruction = (opcode >>> 8) & 0x0003;
        String rs = cpu.getRegisterName((opcode >>> 3) & 0x000F);
        String rd = cpu.getRegisterName((opcode & 0x0007) | ((opcode & 0x0080) >>> 4));
        
        return InstructionName[instruction] + " " + ((instruction != 3) ? rd + ", " : "") + rs;
    }
    
}
