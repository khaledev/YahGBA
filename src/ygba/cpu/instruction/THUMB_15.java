package ygba.cpu.instruction;

import ygba.cpu.ARM7TDMI;
import ygba.memory.MemoryInterface;

public final class THUMB_15 {
    
    public static void execute(ARM7TDMI cpu, MemoryInterface memory, int opcode) {
        int rbIndex = (opcode >>> 8) & 0x0007;
        int rbValue = cpu.getRegister(rbIndex) & 0xFFFFFFFC;
        
        if ((opcode & 0x0800) == 0) { // STMIA Rb!, {Rlist}
            
            for (int i = 0; i <= 7; i++) {
                if ((opcode & (1 << i)) != 0) {
                    memory.storeWord(rbValue, cpu.getRegister(i));
                    rbValue += 4;
                }
            }
            
        } else { // LDMIA Rb!, {Rlist}
            
            for (int i = 0; i <= 7; i++) {
                if ((opcode & (1 << i)) != 0) {
                    cpu.setRegister(i, memory.loadWord(rbValue));
                    rbValue += 4;
                }
            }
            
        }
        
        cpu.setRegister(rbIndex, rbValue);
    }
    
    
    final static String[] InstructionName = {
        "stmia",
        "ldmia",
    };
    
    public static String disassemble(ARM7TDMI cpu, MemoryInterface memory, int opcode, int offset) {
        int instruction = (opcode >>> 11) & 0x0001;
        String rb = cpu.getRegisterName((opcode >>> 8) & 0x0007);
        
        String rlist = "";
        int regs = opcode & 0x00FF;
        int reg = -1;
        for (byte i = 0; i <= 8; i++) {
            if ((regs & (1 << i)) == 0) {
                if (reg != -1) {
                    rlist += "%" + cpu.getRegisterName(reg);
                    int last = i - 1;
                    if (reg < last) {
                        if (reg == (last - 1)) rlist += "%" + cpu.getRegisterName(last);
                        else rlist += "-" + cpu.getRegisterName(last);
                    }
                    reg = -1;
                }
            } else if (reg == -1) reg = i;
        }
        
        rlist = rlist.replaceFirst("%", "").replaceAll("%", ", ");
        
        return InstructionName[instruction] + " " + rb + "!, " + "{" + rlist + "}";
    }
    
}
