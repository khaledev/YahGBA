package ygba.cpu.instruction;

import ygba.cpu.ARM7TDMI;
import ygba.memory.MemoryInterface;
import ygba.util.Hex;

public final class THUMB_3 {
    
    public static void execute(ARM7TDMI cpu, MemoryInterface memory, int opcode) {
        int rdIndex = (opcode >>> 8) & 0x0007;
        int rdOldValue = cpu.getRegister(rdIndex);
        int rdNewValue = 0;
        int immediate = opcode & 0x00FF;
        
        switch (opcode & 0x1800) {
            case 0x0000: // MOV Rd, #nn
                rdNewValue = immediate;
                cpu.setRegister(rdIndex, rdNewValue);
                break;
                
            case 0x0800: // CMP Rd, #nn
                rdNewValue = rdOldValue - immediate;
                cpu.setVCFlagsForSUB(rdOldValue, immediate, rdNewValue);
                break;
                
            case 0x1000: // ADD Rd, #nn
                rdNewValue = rdOldValue + immediate;
                cpu.setVCFlagsForADD(rdOldValue, immediate, rdNewValue);
                cpu.setRegister(rdIndex, rdNewValue);
                break;
                
            case 0x1800: // SUB Rd, #nn
                rdNewValue = rdOldValue - immediate;
                cpu.setVCFlagsForSUB(rdOldValue, immediate, rdNewValue);
                cpu.setRegister(rdIndex, rdNewValue);
                break;
                
            default: // Unknown
        }
        
        cpu.setZFlag(rdNewValue == 0);
        cpu.setNFlag(rdNewValue < 0);
    }
    
    
    final static String[] InstructionName = {
        "mov",
        "cmp",
        "add",
        "sub",
    };
    
    public static String disassemble(ARM7TDMI cpu, MemoryInterface memory, int opcode, int offset) {
        int instruction = (opcode >>> 11) & 0x0003;
        String rd = cpu.getRegisterName((opcode >>> 8) & 0x0007);
        int immediate = opcode & 0x00FF;
        
        return InstructionName[instruction] + " " + rd + ", " + Hex.toHexString(immediate, Hex.Byte);
    }
    
}
