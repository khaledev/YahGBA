package ygba.cpu.instruction;

import ygba.cpu.ARM7TDMI;
import ygba.memory.MemoryInterface;

public final class THUMB_14 {
    
    public static void execute(ARM7TDMI cpu, MemoryInterface memory, int opcode) {
        int spValue = cpu.getSP() & 0xFFFFFFFC;
        
        if ((opcode & 0x0800) == 0) { // PUSH {Rlist}
            
            if ((opcode & 0x0100) != 0) { // PUSH LR
                spValue -= 4;
                memory.storeWord(spValue, cpu.getLR());
            }
            
            for (int i = 7; i >= 0; i--) {
                if ((opcode & (1 << i)) != 0) {
                    spValue -= 4;
                    memory.storeWord(spValue, cpu.getRegister(i));
                }
            }
            
        } else { // POP {Rlist}
            
            for (int i = 0; i <= 7; i++) {
                if ((opcode & (1 << i)) != 0) {
                    cpu.setRegister(i, memory.loadWord(spValue));
                    spValue += 4;
                }
            }
            
            if ((opcode & 0x0100) != 0) { // POP PC
                cpu.setPC(memory.loadWord(spValue));
                spValue += 4;
                cpu.flushTHUMBPipeline();
            }
            
        }
        
        cpu.setSP(spValue);
    }
    
    
    final static String[] InstructionName = {
        "push",
        "pop",
    };
    
    public static String disassemble(ARM7TDMI cpu, MemoryInterface memory, int opcode, int offset) {
        int instruction = (opcode >>> 11) & 0x0001;
        
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
        
        if ((opcode & 0x0100) != 0) {
            rlist += "%" + cpu.getRegisterName((instruction == 0) ? cpu.LR : cpu.PC);
        }
        
        rlist = rlist.replaceFirst("%", "").replaceAll("%", ", ");
        
        return InstructionName[instruction] + " " + "{" + rlist + "}";
    }
    
}
