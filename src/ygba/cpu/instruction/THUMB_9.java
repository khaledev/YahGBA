package ygba.cpu.instruction;

import ygba.cpu.ARM7TDMI;
import ygba.memory.MemoryInterface;
import ygba.util.Hex;

public final class THUMB_9 {
    
    public static void execute(ARM7TDMI cpu, MemoryInterface memory, int opcode) {
        int rdIndex = opcode & 0x0007;
        int rbIndex = (opcode >>> 3) & 0x0007;
        int rbValue = cpu.getRegister(rbIndex);
        int offset = (opcode >>> 6) & 0x001F;
        
        switch (opcode & 0x1800) {
            case 0x0000: // STR Rd, [Rb, #nn]
                memory.storeWord(rbValue + (offset << 2), cpu.getRegister(rdIndex));
                break;
                
            case 0x0800: // LDR Rd, [Rb, #nn]
                cpu.setRegister(rdIndex, memory.loadWord(rbValue + (offset << 2)));
                break;
                
            case 0x1000: // STRB Rd, [Rb, #nn]
                memory.storeByte(rbValue + offset, (byte) cpu.getRegister(rdIndex));
                break;
                
            case 0x1800: // LDRB Rd, [Rb, #nn]
                cpu.setRegister(rdIndex, memory.loadByte(rbValue + offset) & 0x000000FF);
                break;
                
            default: // Unknown
        }
    }
    
    
    final static String[] InstructionName = {
        "str",
        "ldr",
        "strb",
        "ldrb",
    };
    
    public static String disassemble(ARM7TDMI cpu, MemoryInterface memory, int opcode, int offset) {
        int instruction = (opcode >>> 11) & 0x0003;
        String rd = cpu.getRegisterName(opcode & 0x0007);
        String rb = cpu.getRegisterName((opcode >>> 3) & 0x0007);
        int off = (opcode >>> 6) & 0x001F;
        if ((opcode & 0x1000) == 0) off <<= 2;
        
        return InstructionName[instruction] + " " + rd + ", [" + rb + ", " + Hex.toHexString(off, Hex.Byte) + "]";
    }
    
}
