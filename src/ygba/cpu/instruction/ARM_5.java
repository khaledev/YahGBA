package ygba.cpu.instruction;

import ygba.cpu.ARM7TDMI;
import ygba.memory.MemoryInterface;
import ygba.util.Hex;

public final class ARM_5 {
    
    public static void execute(ARM7TDMI cpu, MemoryInterface memory, int opcode) {
        if (!ARMState.isPreconditionSatisfied(cpu, opcode)) return;
        
        int rnIndex = (opcode >>> 16) & 0x0000000F;
        int rnValue = cpu.getRegister(rnIndex);
        boolean cFlag = cpu.getCFlag();
        
        int operand1 = rnValue;
        int operand2;
        if ((opcode & 0x02000000) != 0) { // operand2 is an immediate value
            operand2 = opcode & 0x000000FF;
            // shiftType == ROR
            int shiftAmount = (opcode & 0x00000F00) >>> 7;
            if (shiftAmount != 0) {
                cFlag = ((operand2 & (1 << (shiftAmount - 1))) != 0);
                operand2 = (operand2 << (32 - shiftAmount)) | (operand2 >>> shiftAmount);
            }
        } else { // operand2 is a register
            int rmIndex = opcode & 0x0000000F;
            int rmValue = cpu.getRegister(rmIndex);
            int shiftType = opcode & 0x00000060;
            int shiftAmount;
            
            operand2 = rmValue;
            
            if ((opcode & 0x00000010) == 0) { // Shift by immediate
                shiftAmount = (opcode >>> 7) & 0x0000001F;
                
                switch (shiftType) {
                    case 0x00000000: // LSL
                        if (shiftAmount != 0) {
                            cFlag = ((operand2 & (1 << (32 - shiftAmount))) != 0);
                            operand2 <<= shiftAmount;
                        }
                        break;
                        
                    case 0x00000020: // LSR
                        if (shiftAmount != 0) {
                            cFlag = ((operand2 & (1 << (shiftAmount - 1))) != 0);
                            operand2 >>>= shiftAmount;
                        } else {
                            cFlag = ((operand2 & 0x80000000) != 0);
                            operand2 = 0;
                        }
                        break;
                        
                    case 0x00000040: // ASR
                        if (shiftAmount != 0) {
                            cFlag = ((operand2 & (1 << (shiftAmount - 1))) != 0);
                            operand2 >>= shiftAmount;
                        } else {
                            cFlag = ((operand2 & 0x80000000) != 0);
                            operand2 >>= 31; // Fill all bits of operand2 with bit 31
                        }
                        break;
                        
                    case 0x00000060: // ROR
                        if (shiftAmount != 0) {
                            cFlag = ((operand2 & (1 << (shiftAmount - 1))) != 0);
                            operand2 = (operand2 << (32 - shiftAmount)) | (operand2 >>> shiftAmount);
                        } else {
                            cFlag = ((operand2 & 0x00000001) != 0);
                            operand2 >>>= 1;
                            if (cpu.getCFlag()) operand2 |= 0x80000000;
                        }
                        break;
                }
            } else { // Shift by register
                int rsIndex = (opcode >>> 8) & 0x0000000F;
                int rsValue = cpu.getRegister(rsIndex);
                
                shiftAmount = (rsValue + ((rsIndex == cpu.PC) ? 4 : 0)) & 0x000000FF;
                
                switch (shiftType) {
                    case 0x00000000: // LSL
                        if (shiftAmount != 0) {
                            if (shiftAmount < 32) {
                                cFlag = ((operand2 & (1 << (32 - shiftAmount))) != 0);
                                operand2 <<= shiftAmount;
                            } else if (shiftAmount == 32) {
                                cFlag = ((operand2 & 0x00000001) != 0);
                                operand2 = 0;
                            } else {
                                cFlag = false;
                                operand2 = 0;
                            }
                        }
                        break;
                        
                    case 0x00000020: // LSR
                        if (shiftAmount != 0) {
                            if (shiftAmount < 32) {
                                cFlag = ((operand2 & (1 << (shiftAmount - 1))) != 0);
                                operand2 >>>= shiftAmount;
                            } else if (shiftAmount == 32) {
                                cFlag = ((operand2 & 0x80000000) != 0);
                                operand2 = 0;
                            } else {
                                cFlag = false;
                                operand2 = 0;
                            }
                        }
                        break;
                        
                    case 0x00000040: // ASR
                        if (shiftAmount != 0) {
                            if (shiftAmount < 32) {
                                cFlag = ((operand2 & (1 << (shiftAmount - 1))) != 0);
                                operand2 >>= shiftAmount;
                            } else {
                                cFlag = ((operand2 & 0x80000000) != 0);
                                operand2 >>= 31;
                            }
                        }
                        break;
                        
                    case 0x00000060: // ROR
                        if (shiftAmount != 0) {
                            shiftAmount &= 0x0000001F;
                            if (shiftAmount != 0) {
                                cFlag = ((operand2 & (1 << (shiftAmount - 1))) != 0);
                                operand2 = (operand2 << (32 - shiftAmount)) | (operand2 >>> shiftAmount);
                            } else {
                                cFlag = ((operand2 & 0x80000000) != 0);
                            }
                        } else {
                            cFlag = ((operand2 & 0x80000000) != 0);
                        }
                        break;
                }
            }
        }
        
        int rdIndex = (opcode >>> 12) & 0x0000000F;
        int rdValue;
        boolean sBit = ((opcode & 0x00100000) != 0);
        
        switch (opcode & 0x01E00000) {
            case 0x00000000: // AND{cond}{S} Rd, Rn, Op2
                rdValue = operand1 & operand2;
                cpu.setRegister(rdIndex, rdValue);
                if (sBit) cpu.setCFlag(cFlag);
                break;
                
            case 0x00200000: // EOR{cond}{S} Rd, Rn, Op2
                rdValue = operand1 ^ operand2;
                cpu.setRegister(rdIndex, rdValue);
                if (sBit) cpu.setCFlag(cFlag);
                break;
                
            case 0x00400000: // SUB{cond}{S} Rd, Rn, Op2
                rdValue = operand1 - operand2;
                cpu.setRegister(rdIndex, rdValue);
                if (sBit) cpu.setVCFlagsForSUB(operand1, operand2, rdValue);
                break;
                
            case 0x00600000: // RSB{cond}{S} Rd, Rn, Op2
                rdValue = operand2 - operand1;
                cpu.setRegister(rdIndex, rdValue);
                if (sBit) cpu.setVCFlagsForSUB(operand2, operand1, rdValue);
                break;
                
            case 0x00800000: // ADD{cond}{S} Rd, Rn, Op2
                rdValue = operand1 + operand2;
                cpu.setRegister(rdIndex, rdValue);
                if (sBit) cpu.setVCFlagsForADD(operand1, operand2, rdValue);
                break;
                
            case 0x00A00000: // ADC{cond}{S} Rd, Rn, Op2
                rdValue = operand1 + operand2 + (cpu.getCFlag() ? 1 : 0);
                cpu.setRegister(rdIndex, rdValue);
                if (sBit) cpu.setVCFlagsForADD(operand1, operand2, rdValue);
                break;
                
            case 0x00C00000: // SBC{cond}{S} Rd, Rn, Op2
                rdValue = operand1 - operand2 - (cpu.getCFlag() ? 0 : 1);
                cpu.setRegister(rdIndex, rdValue);
                if (sBit) cpu.setVCFlagsForSUB(operand1, operand2, rdValue);
                break;
                
            case 0x00E00000: // RSC{cond}{S} Rd, Rn, Op2
                rdValue = operand2 - operand1 - (cpu.getCFlag() ? 0 : 1);
                cpu.setRegister(rdIndex, rdValue);
                if (sBit) cpu.setVCFlagsForSUB(operand2, operand1, rdValue);
                break;
                
            case 0x01000000: // TST{cond} Rn, Op2
                rdValue = operand1 & operand2;
                cpu.setCFlag(cFlag);
                break;
                
            case 0x01200000: // TEQ{cond} Rn, Op2
                rdValue = operand1 ^ operand2;
                cpu.setCFlag(cFlag);
                break;
                
            case 0x01400000: // CMP{cond} Rn, Op2
                rdValue = operand1 - operand2;
                cpu.setVCFlagsForSUB(operand1, operand2, rdValue);
                break;
                
            case 0x01600000: // CMN{cond} Rn, Op2
                rdValue = operand1 + operand2;
                cpu.setVCFlagsForADD(operand1, operand2, rdValue);
                break;
                
            case 0x01800000: // ORR{cond}{S} Rd, Rn, Op2
                rdValue = operand1 | operand2;
                cpu.setRegister(rdIndex, rdValue);
                if (sBit) cpu.setCFlag(cFlag);
                break;
                
            case 0x01A00000: // MOV{cond}{S} Rd, Op2
                rdValue = operand2;
                cpu.setRegister(rdIndex, rdValue);
                if (sBit) cpu.setCFlag(cFlag);
                break;
                
            case 0x01C00000: // BIC{cond}{S} Rd, Rn, Op2
                rdValue = operand1 & ~operand2;
                cpu.setRegister(rdIndex, rdValue);
                if (sBit) cpu.setCFlag(cFlag);
                break;
                
            case 0x01E00000: // MVN{cond}{S} Rd, Op2
                rdValue = ~operand2;
                cpu.setRegister(rdIndex, rdValue);
                if (sBit) cpu.setCFlag(cFlag);
                break;
                
            default:
                rdValue = 0;
        }
        
        if (sBit) {
            cpu.setNFlag(rdValue < 0);
            cpu.setZFlag(rdValue == 0);
        }
        
        if (rdIndex == cpu.PC) {
            if (sBit) cpu.setCPSR(cpu.getSPSR());
            cpu.flushPipeline();
        }
    }
    
    
    final static String[] InstructionName = {
        "and",
        "eor",
        "sub",
        "rsb",
        "add",
        "adc",
        "sbc",
        "rsc",
        "tst",
        "teq",
        "cmp",
        "cmn",
        "orr",
        "mov",
        "bic",
        "mvn",
    };
    
    final static String[] ShiftInstructionName = {
        "lsl",
        "lsr",
        "asr",
        "ror",
    };
    
    public static String disassemble(ARM7TDMI cpu, MemoryInterface memory, int opcode, int offset) {
        int instruction = (opcode >>> 21) & 0x0000000F;
        String cond = ARMState.getPreconditionSuffix(opcode);
        boolean isTestInstruction = ((instruction >= 0x08) && (instruction <= 0x0B));
        boolean isMoveInstruction = ((instruction == 0x0D) || (instruction == 0x0F));
        
        String sBit = (((opcode & 0x00100000) == 0) || isTestInstruction) ? "" : "s";
        
        String rd, op1, op2;
        
        rd = cpu.getRegisterName((opcode >>> 12) & 0x0000000F);
        op1 = cpu.getRegisterName((opcode >>> 16) & 0x0000000F);
        
        if ((opcode & 0x02000000) != 0) {
            int immediate = opcode & 0x000000FF;
            int shiftAmount = (opcode & 0x00000F00) >>> 7;
            if (shiftAmount != 0) {
                immediate = (immediate << (32 - shiftAmount)) | (immediate >>> shiftAmount);
            }
            op2 = Hex.toHexString(immediate);
        } else {
            int shiftType = (opcode >>> 5) & 0x00000003;
            op2 = cpu.getRegisterName(opcode & 0x0000000F) + ", " + ShiftInstructionName[shiftType];
            if ((opcode & 0x00000010) != 0) {
                op2 += " " + cpu.getRegisterName((opcode >>> 8) & 0x0000000F);
            } else {
                op2 += "#" + ((opcode >>> 7) & 0x0000001F);
            }
        }
        
        return InstructionName[instruction] + cond + sBit + " " + (isTestInstruction ? "" : (rd + ", ")) + (isMoveInstruction ? "" : (op1 + ", ")) + op2;
    }
    
}
