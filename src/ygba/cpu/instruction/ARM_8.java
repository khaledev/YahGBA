package ygba.cpu.instruction;

import ygba.cpu.ARM7TDMI;
import ygba.memory.MemoryInterface;

public final class ARM_8 {
    
    public static void execute(ARM7TDMI cpu, MemoryInterface memory, int opcode) {
        if (!ARMState.isPreconditionSatisfied(cpu, opcode)) return;
        
        int rdloIndex = (opcode >>> 12) & 0x0000000F;
        int rdloValue;
        int rdhiIndex = (opcode >>> 16) & 0x0000000F;
        int rdhiValue;
        int rmValue = cpu.getRegister(opcode & 0x0000000F);
        int rsValue = cpu.getRegister((opcode >>> 8) & 0x0000000F);
        long rdValue;
        
        switch (opcode & 0x00600000) {
            case 0x00000000: // UMULL{cond}{S} RdLo, RdHi, Rm, Rs
                rdValue = ((long) rmValue & 0xFFFFFFFFL) * ((long) rsValue & 0xFFFFFFFFL);
                break;
                
            case 0x00200000: // UMLAL{cond}{S} RdLo, RdHi, Rm, Rs
                rdloValue = cpu.getRegister(rdloIndex);
                rdhiValue = cpu.getRegister(rdhiIndex);
                rdValue = ((long) rmValue & 0xFFFFFFFFL) * ((long) rsValue & 0xFFFFFFFFL);
                rdValue += ((long) rdhiValue << 32) | ((long) rdloValue & 0xFFFFFFFFL);
                break;
                
            case 0x00400000: // SMULL{cond}{S} RdLo, RdHi, Rm, Rs
                rdValue = rmValue * rsValue;
                break;
                
            case 0x00600000: // SMLAL{cond}{S} RdLo, RdHi, Rm, Rs
                rdloValue = cpu.getRegister(rdloIndex);
                rdhiValue = cpu.getRegister(rdhiIndex);
                rdValue = rmValue * rsValue;
                rdValue += ((long) rdhiValue << 32) | ((long) rdloValue & 0xFFFFFFFFL);
                break;
                
            default:
                rdValue = 0;
                break;
        }
        
        cpu.setRegister(rdloIndex, (int) rdValue);
        cpu.setRegister(rdhiIndex, (int) (rdValue >>> 32));
        
        if ((opcode & 0x00100000) != 0) {
            cpu.setZFlag(rdValue == 0);
            cpu.setNFlag(rdValue < 0);
        }
    }
    
    
    final static String[] InstructionName = {
        "umull",
        "umlal",
        "smull",
        "smlal",
    };
    
    public static String disassemble(ARM7TDMI cpu, MemoryInterface memory, int opcode, int offset) {
        int instruction = (opcode >>> 21) & 0x00000003;
        String cond = ARMState.getPreconditionSuffix(opcode);
        String sBit = ((opcode & 0x00100000) == 0) ? "" : "s";
        String rdlo = cpu.getRegisterName((opcode >>> 12) & 0x0000000F);
        String rdhi = cpu.getRegisterName((opcode >>> 16) & 0x0000000F);
        String rm = cpu.getRegisterName(opcode & 0x0000000F);
        String rs = cpu.getRegisterName((opcode >>> 8) & 0x0000000F);
        
        return InstructionName[instruction] + cond + sBit + " " + rdlo + ", " + rdhi + ", " + rm + ", " + rs;
    }
    
}
