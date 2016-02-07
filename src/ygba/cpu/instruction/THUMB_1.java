package ygba.cpu.instruction;

import ygba.cpu.ARM7TDMI;
import ygba.memory.MemoryInterface;

public final class THUMB_1 {
    
    public static void execute(ARM7TDMI cpu, MemoryInterface memory, int opcode) {
        int rdIndex = opcode & 0x0007;
        int rsIndex = (opcode >>> 3) & 0x0007;
        int rsValue = cpu.getRegister(rsIndex);
        int rdValue = 0;
        int shiftAmount = (opcode >>> 6) & 0x001F;
        
        switch (opcode & 0x1800) {
            case 0x0000: // LSL Rd, Rs, #Offset
                if (shiftAmount != 0) {
                    cpu.setCFlag((rsValue & (1 << (32 - shiftAmount))) != 0);
                    rdValue = rsValue << shiftAmount;
                }
                break;
                
            case 0x0800: // LSR Rd, Rs, #Offset
                if (shiftAmount == 0) {
                    cpu.setCFlag(rsValue < 0);
                    rdValue = 0;
                } else {
                    cpu.setCFlag((rsValue & (1 << (shiftAmount - 1))) != 0);
                    rdValue = rsValue >>> shiftAmount;
                }
                break;
                
            case 0x1000: // ASR Rd, Rs, #Offset
                if (shiftAmount == 0) {
                    cpu.setCFlag(rsValue < 0);
                    rdValue = rsValue >> 31;
                } else {
                    cpu.setCFlag((rsValue & (1 << (shiftAmount - 1))) != 0);
                    rdValue = rsValue >> shiftAmount;
                }
                break;
                
            default: // Unknown
        }
        
        cpu.setRegister(rdIndex, rdValue);
        cpu.setZFlag(rdValue == 0);
        cpu.setNFlag(rdValue < 0);
    }
    
    
    final static String[] InstructionName = {
        "lsl",
        "lsr",
        "asr",
    };
    
    public static String disassemble(ARM7TDMI cpu, MemoryInterface memory, int opcode, int offset) {
        int instruction = (opcode >>> 11) & 0x0003;
        if (instruction >= InstructionName.length) return "[ unknown ]";
        String rd = cpu.getRegisterName(opcode & 0x0007);
        String rs = cpu.getRegisterName((opcode >>> 3) & 0x0007);
        int shiftAmount = (opcode >>> 6) & 0x001F;
        
        return InstructionName[instruction] + " " + rd + ", " + rs + ", #" + shiftAmount;
    }
    
}
