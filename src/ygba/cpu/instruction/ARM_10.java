package ygba.cpu.instruction;

import ygba.cpu.ARM7TDMI;
import ygba.memory.MemoryInterface;
import ygba.util.Hex;

public final class ARM_10 {
    
    public static void execute(ARM7TDMI cpu, MemoryInterface memory, int opcode) {
        if (!ARMState.isPreconditionSatisfied(cpu, opcode)) return;
        
        int rdIndex = (opcode >>> 12) & 0x0000000F;
        int rnIndex = (opcode >>> 16) & 0x0000000F;
        int rnValue = cpu.getRegister(rnIndex);
        
        int offset;
        if ((opcode & 0x00400000) == 0) {
            int rmIndex = opcode & 0x0000000F;
            int rmValue = cpu.getRegister(rmIndex);
            offset = rmValue;
        } else {
            offset = ((opcode & 0x00000F00) >>> 4) | (opcode & 0x0000000F);
        }
        
        boolean isPostIndexing = ((opcode & 0x01000000) == 0);
        boolean isDown = ((opcode & 0x00800000) == 0);
        boolean isWriteBack = isPostIndexing || ((opcode & 0x00200000) != 0);
        
        if (isDown) offset = -offset;
        if (!isPostIndexing) rnValue += offset;
        
        int instruction = (opcode & 0x00000060);
        
        if ((opcode & 0x00100000) == 0) { // Store to memory
            if (instruction == 0x00000020) { // STR{cond}H  Rd, <Address>
                int rdValue = cpu.getRegister(rdIndex);
                if (rdIndex == cpu.PC) rdValue += 4;
                memory.storeHalfWord(rnValue, (short) rdValue);
            }
        } else { // Load from memory
            switch (instruction) {
                case 0x00000020: // LDR{cond}H  Rd, <Address>
                    cpu.setRegister(rdIndex, memory.loadHalfWord(rnValue) & 0x0000FFFF);
                    break;
                case 0x00000040: // LDR{cond}SB Rd, <Address>
                    cpu.setRegister(rdIndex, memory.loadByte(rnValue));
                    break;
                case 0x00000060: // LDR{cond}SH Rd, <Address>
                    cpu.setRegister(rdIndex, memory.loadHalfWord(rnValue));
                    break;
                default:
                    return;
            }
            
            if (rdIndex == cpu.PC) cpu.flushARMPipeline();
            if (rdIndex == rnIndex) return;
        }
        
        if (isWriteBack) {
            if (isPostIndexing) rnValue += offset;
            cpu.setRegister(rnIndex, rnValue);
            if (rnIndex == cpu.PC) cpu.flushARMPipeline();
        }
    }
    
    
    final static String[] InstructionName = {
        "str%h",
        "ldr%h",
        "ldr%sb",
        "ldr%sh",
    };
    
    public static String disassemble(ARM7TDMI cpu, MemoryInterface memory, int opcode, int offset) {
        int instruction = (opcode >>> 5) & 0x00000003;
        if ((opcode & 0x00100000) == 0) {
            if (instruction != 1) return "[ unknown ]";
            instruction = 0;
        } else {
            if (instruction == 0) return "[ unknown ]";
        }
        String cond = ARMState.getPreconditionSuffix(opcode);
        boolean isPostIndexing = ((opcode & 0x01000000) == 0);
        boolean isTWBitSet = ((opcode & 0x00200000) != 0);
        String rd = cpu.getRegisterName((opcode >>> 12) & 0x0000000F);
        String rn = cpu.getRegisterName((opcode >>> 16) & 0x0000000F);
        String sign = ((opcode & 0x00800000) == 0) ? "-" : "";
        
        String address;
        if ((opcode & 0x00400000) == 0) { // {+/-}Rm
            address = sign + cpu.getRegisterName(opcode & 0x0000000F);
        } else { // <#{+/-}expression>
            int immediate = ((opcode & 0x00000F00) >>> 4) | (opcode & 0x0000000F);
            address = "<" + sign + Hex.toHexString(immediate, Hex.Byte) + ">";
        }
        String addrBegin, addrEnd;
        if (isPostIndexing) {
            addrBegin = "[" + rn + "], ";
            addrEnd = "";
        } else {
            addrBegin = "[" + rn + ", ";
            addrEnd = "]" + (isTWBitSet ? "!" : "");
        }
        address = addrBegin + address + addrEnd;
        
        return InstructionName[instruction].replace("%", cond) + " " + rd + ", " + address;
    }
    
}
