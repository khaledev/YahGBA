package ygba.cpu.instruction;

import ygba.cpu.ARM7TDMI;
import ygba.memory.MemoryInterface;
import ygba.util.Hex;

public final class ARM_6 {
    
    public static void execute(ARM7TDMI cpu, MemoryInterface memory, int opcode) {
        if (!ARMState.isPreconditionSatisfied(cpu, opcode)) return;
        
        int psrIndex, psrValue;
        if ((opcode & 0x00400000) == 0) {
            psrIndex = cpu.CPSR;
            psrValue = cpu.getCPSR();
        } else {
            if (cpu.SPSR == cpu.SPSR_null) return;
            psrIndex = cpu.SPSR;
            psrValue = cpu.getSPSR();
        }
        
        if ((opcode & 0x00200000) == 0) {
            // MRS{cond} Rd, Psr
            
            int rdIndex = (opcode >>> 12) & 0x0000000F;
            cpu.setRegister(rdIndex, psrValue);
            
        } else {
            // MSR{cond} Psr{_field}, Op
            
            int operand;
            if ((opcode & 0x02000000) == 0) {
                int rmIndex = opcode & 0x0000000F;
                operand = cpu.getRegister(rmIndex);
            } else {
                operand = opcode & 0x000000FF;
                int shiftAmount = (opcode & 0x00000F00) >>> 7;
                if (shiftAmount != 0) {
                    operand = (operand << (32 - shiftAmount)) | (operand >>> shiftAmount);
                }
            }
            
            if (cpu.getMode() != cpu.USRMode) {
                // We enter here if we are in a privileged mode
                if ((opcode & 0x00010000) != 0) psrValue = (psrValue & 0xFFFFFF00) | (operand & 0x000000FF);
                if ((opcode & 0x00020000) != 0) psrValue = (psrValue & 0xFFFF00FF) | (operand & 0x0000FF00);
                if ((opcode & 0x00040000) != 0) psrValue = (psrValue & 0xFF00FFFF) | (operand & 0x00FF0000);
            }
            if ((opcode & 0x00080000) != 0) psrValue = (psrValue & 0x00FFFFFF) | (operand & 0xFF000000);
            psrValue |= 0x00000010;
            
            if (psrIndex == cpu.CPSR) {
                cpu.setPC(cpu.getCurrentPC());
                cpu.setCPSR(psrValue);
                cpu.flushPipeline();
            } else {
                cpu.setRegister(psrIndex, psrValue);
            }
            
        }
    }
    
    
    final static String[] InstructionName = {
        "mrs",
        "msr",
    };
    
    public static String disassemble(ARM7TDMI cpu, MemoryInterface memory, int opcode, int offset) {
        int instruction = (opcode >>> 21) & 0x00000001;
        String cond = ARMState.getPreconditionSuffix(opcode);
        String psr = cpu.getRegisterName(((opcode >>> 22) & 0x00000001) | 0x00000010);
        
        String op1, op2;
        if (instruction == 0) {
            op1 = cpu.getRegisterName((opcode >>> 12) & 0x0000000F);
            op2 = psr;
        } else {
            op1 = psr + "_";
            if ((opcode & 0x00010000) != 0) op1 += "f";
            if ((opcode & 0x00020000) != 0) op1 += "s";
            if ((opcode & 0x00040000) != 0) op1 += "x";
            if ((opcode & 0x00080000) != 0) op1 += "c";
            if ((opcode & 0x02000000) == 0) {
                op2 = cpu.getRegisterName(opcode & 0x0000000F);
            } else {
                int immediate = opcode & 0x000000FF;
                int shiftAmount = (opcode & 0x00000F00) >>> 7;
                if (shiftAmount != 0) {
                    immediate = (immediate << (32 - shiftAmount)) | (immediate >>> shiftAmount);
                }
                op2 = Hex.toHexString(immediate);
            }
        }
        
        return InstructionName[instruction] + cond + " " + op1 + ", " + op2;
    }
    
}
