package ygba.cpu.instruction;

import ygba.cpu.ARM7TDMI;
import ygba.memory.MemoryInterface;

public final class THUMB_8 {
    
    public static void execute(ARM7TDMI cpu, MemoryInterface memory, int opcode) {
        int rdIndex = opcode & 0x0007;
        int rbIndex = (opcode >>> 3) & 0x0007;
        int roIndex = (opcode >>> 6) & 0x0007;
        int offset = cpu.getRegister(rbIndex) + cpu.getRegister(roIndex);
        
        switch (opcode & 0x0C00) {
            case 0x0000: // STRH Rd, [Rb, Ro]
                memory.storeHalfWord(offset, (short) cpu.getRegister(rdIndex));
                break;
                
            case 0x0400: // LDSB Rd, [Rb, Ro]
                cpu.setRegister(rdIndex, memory.loadByte(offset));
                break;
                
            case 0x0800: // LDRH Rd, [Rb, Ro]
                cpu.setRegister(rdIndex, memory.loadHalfWord(offset) & 0x0000FFFF);
                break;
                
            case 0x0C00: // LDSH Rd, [Rb, Ro]
                cpu.setRegister(rdIndex, memory.loadHalfWord(offset));
                break;
                
            default: // Unknown
        }
    }
    
    
    final static String[] InstructionName = {
        "strh",
        "ldsb",
        "ldrh",
        "ldsh",
    };
    
    public static String disassemble(ARM7TDMI cpu, MemoryInterface memory, int opcode, int offset) {
        int instruction = (opcode >>> 10) & 0x0003;
        String rd = cpu.getRegisterName(opcode & 0x0007);
        String rb = cpu.getRegisterName((opcode >>> 3) & 0x0007);
        String ro = cpu.getRegisterName((opcode >>> 6) & 0x0007);
        
        return InstructionName[instruction] + " " + rd + ", [" + rb + ", " + ro + "]";
    }
    
}
