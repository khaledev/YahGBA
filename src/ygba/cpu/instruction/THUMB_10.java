package ygba.cpu.instruction;

import ygba.cpu.ARM7TDMI;
import ygba.memory.MemoryInterface;
import ygba.util.Hex;

public final class THUMB_10 {
    
    public static void execute(ARM7TDMI cpu, MemoryInterface memory, int opcode) {
        int rdIndex = opcode & 0x0007;
        int rbIndex = (opcode >>> 3) & 0x0007;
        int rbValue = cpu.getRegister(rbIndex);
        int offset = (opcode >>> 5) & 0x003E;
        
        if ((opcode & 0x0800) == 0) { // STRH Rd, [Rb, #nn]
            memory.storeHalfWord(rbValue + offset, (short) cpu.getRegister(rdIndex));
        } else { // LDRH Rd, [Rb, #nn]
            cpu.setRegister(rdIndex, memory.loadHalfWord(rbValue + offset) & 0x0000FFFF);
        }
    }
    
    
    final static String[] InstructionName = {
        "strh",
        "ldrh",
    };
    
    public static String disassemble(ARM7TDMI cpu, MemoryInterface memory, int opcode, int offset) {
        int instruction = (opcode >>> 11) & 0x0001;
        String rd = cpu.getRegisterName(opcode & 0x0007);
        String rb = cpu.getRegisterName((opcode >>> 3) & 0x0007);
        int off = (opcode >>> 5) & 0x003E;
        
        return InstructionName[instruction] + " " + rd + ", [" + rb + ", " + Hex.toHexString(off, Hex.Byte) + "]";
    }
    
}
