package ygba.cpu.instruction;

import ygba.cpu.ARM7TDMI;
import ygba.memory.MemoryInterface;
import ygba.util.Hex;

public final class ARM_9 {
    
    public static void execute(ARM7TDMI cpu, MemoryInterface memory, int opcode) {
        if (!ARMState.isPreconditionSatisfied(cpu, opcode)) return;
        
        int rdIndex = (opcode >>> 12) & 0x0000000F;
        int rnIndex = (opcode >>> 16) & 0x0000000F;
        int rnValue = cpu.getRegister(rnIndex);
        
        int offset;
        if ((opcode & 0x02000000) == 0) {
            offset = opcode & 0x00000FFF;
        } else {
            int rmIndex = opcode & 0x0000000F;
            int rmValue = cpu.getRegister(rmIndex);
            int shiftType = opcode & 0x00000060;
            int shiftAmount = (opcode >>> 7) & 0x0000001F;
            
            offset = rmValue;
            
            switch (shiftType) {
                case 0x00000000: // LSL
                    if (shiftAmount != 0) offset <<= shiftAmount;
                    break;
                    
                case 0x00000020: // LSR
                    if (shiftAmount != 0) offset >>>= shiftAmount;
                    else offset = 0;
                    break;
                    
                case 0x00000040: // ASR
                    if (shiftAmount != 0) offset >>= shiftAmount;
                    else offset >>= 31;
                    break;
                    
                case 0x00000060: // ROR
                    if (shiftAmount != 0) {
                        offset = (offset << (32 - shiftAmount)) | (offset >>> shiftAmount);
                    } else {
                        offset >>>= 1;
                        if (cpu.getCFlag()) offset |= 0x80000000;
                    }
                    break;
            }
        }
        
        boolean isPostIndexing = ((opcode & 0x01000000) == 0);
        boolean isDown = ((opcode & 0x00800000) == 0);
        boolean isWordTransfer = ((opcode & 0x00400000) == 0);
        boolean isWriteBack = isPostIndexing || ((opcode & 0x00200000) != 0);
        
        if (isDown) offset = -offset;
        if (!isPostIndexing) rnValue += offset;
        
        if ((opcode & 0x00100000) == 0) { // STR{cond}{B}{T} Rd, <Address>
            int rdValue = cpu.getRegister(rdIndex);
            if (rdIndex == cpu.PC) rdValue += 4;
            if (isWordTransfer) {
                memory.storeWord(rnValue, rdValue);
            } else {
                memory.storeByte(rnValue, (byte) rdValue);
            }
        } else { // LDR{cond}{B}{T} Rd, <Address>
            if (isWordTransfer) {
                cpu.setRegister(rdIndex, memory.loadWord(rnValue));
            } else {
                cpu.setRegister(rdIndex, memory.loadByte(rnValue) & 0x000000FF);
            }
            
            if (rdIndex == cpu.PC) cpu.flushARMPipeline();
            if (rdIndex == rnIndex) return; // Don't update Rn if it's equal to Rd
        }
        
        if (isWriteBack) { // Update Rn
            if (isPostIndexing) rnValue += offset;
            cpu.setRegister(rnIndex, rnValue);
            if (rnIndex == cpu.PC) cpu.flushARMPipeline();
        }
    }
    
    
    final static String[] InstructionName = {
        "str",
        "ldr",
    };
    
    final static String[] ShiftInstructionName = {
        "lsl",
        "lsr",
        "asr",
        "ror",
    };
    
    public static String disassemble(ARM7TDMI cpu, MemoryInterface memory, int opcode, int offset) {
        int instruction = (opcode >>> 20) & 0x00000001;
        String cond = ARMState.getPreconditionSuffix(opcode);
        boolean isPostIndexing = ((opcode & 0x01000000) == 0);
        boolean isTWBitSet = ((opcode & 0x00200000) != 0);
        String bBit = ((opcode & 0x00400000) != 0) ? "b" : "";
        String tBit = (isTWBitSet && isPostIndexing) ? "t" : "";
        String rd = cpu.getRegisterName((opcode >>> 12) & 0x0000000F);
        String rn = cpu.getRegisterName((opcode >>> 16) & 0x0000000F);
        String sign = ((opcode & 0x00800000) == 0) ? "-" : "";
        
        String address;
        if ((opcode & 0x02000000) == 0) { // <#{+/-}expression>
            address = "<" + sign + Hex.toHexString(opcode & 0x00000FFF, Hex.Bit_12) + ">";
        } else { // {+/-}Rm{,<shift>}
            int shiftType = (opcode >>> 5) & 0x00000003;
            int shiftAmount = (opcode >>> 7) & 0x0000000F;
            address = sign + cpu.getRegisterName(opcode & 0x0000000F);
            if ((shiftType != 0) || (shiftAmount != 0)) { // Ignore lsl#0
                address += ", " + ShiftInstructionName[shiftType] + "#" + shiftAmount;
            }
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
        
        return InstructionName[instruction] + cond + bBit + tBit + " " + rd + ", " + address;
    }
    
}
