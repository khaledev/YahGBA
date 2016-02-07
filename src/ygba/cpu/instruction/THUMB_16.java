package ygba.cpu.instruction;

import ygba.cpu.ARM7TDMI;
import ygba.memory.MemoryInterface;
import ygba.util.Hex;

public final class THUMB_16 {
    
    public static void execute(ARM7TDMI cpu, MemoryInterface memory, int opcode) {
        boolean condition;
        
        switch (opcode & 0x0F00) {
            case 0x0000: // BEQ label
                condition = cpu.getZFlag();
                break;
                
            case 0x0100: // BNE label
                condition = !cpu.getZFlag();
                break;
                
            case 0x0200: // BCS label
                condition = cpu.getCFlag();
                break;
                
            case 0x0300: // BCC label
                condition = !cpu.getCFlag();
                break;
                
            case 0x0400: // BMI label
                condition = cpu.getNFlag();
                break;
                
            case 0x0500: // BPL label
                condition = !cpu.getNFlag();
                break;
                
            case 0x0600: // BVS label
                condition = cpu.getVFlag();
                break;
                
            case 0x0700: // BVC label
                condition = !cpu.getVFlag();
                break;
                
            case 0x0800: // BHI label
                condition = (cpu.getCFlag() && !cpu.getZFlag());
                break;
                
            case 0x0900: // BLS label
                condition = (!cpu.getCFlag() || cpu.getZFlag());
                break;
                
            case 0x0A00: // BGE label
                condition = (cpu.getNFlag() == cpu.getVFlag());
                break;
                
            case 0x0B00: // BLT label
                condition = (cpu.getNFlag() != cpu.getVFlag());
                break;
                
            case 0x0C00: // BGT label
                condition = (!cpu.getZFlag() && (cpu.getNFlag() == cpu.getVFlag()));
                break;
                
            case 0x0D00: // BLE label
                condition = (cpu.getZFlag() || (cpu.getNFlag() != cpu.getVFlag()));
                break;
                
            default: // Unknown
                condition = false;
        }
        
        if (condition) {
            int offset = ((byte) (opcode & 0x00FF)) << 1;
            cpu.setPC(cpu.getPC() + offset);
            cpu.flushTHUMBPipeline();
        }
    }
    
    
    final static String[] InstructionName = {
        "beq",
        "bne",
        "bcs",
        "bcc",
        "bmi",
        "bpl",
        "bvs",
        "bvc",
        "bhi",
        "bls",
        "bge",
        "blt",
        "bgt",
        "ble",
    };
    
    public static String disassemble(ARM7TDMI cpu, MemoryInterface memory, int opcode, int offset) {
        int instruction = (opcode >>> 8) & 0x000F;
        if (instruction >= InstructionName.length) return "[ unknown ]";
        int label = offset + 4 + (((byte) (opcode & 0x00FF)) << 1);
        
        return InstructionName[instruction] + " " + Hex.toAddrString(label, Hex.Word);
    }
    
}
