package ygba.cpu.instruction;

import ygba.cpu.ARM7TDMI;

public final class ARMState {
    
    protected static boolean isPreconditionSatisfied(ARM7TDMI cpu, int opcode) {
        boolean condition;
        
        switch (opcode & 0xF0000000) {
            case 0x00000000: // EQ
                condition = cpu.getZFlag();
                break;
            case 0x10000000: // NE
                condition = !cpu.getZFlag();
                break;
            case 0x20000000: // CS
                condition = cpu.getCFlag();
                break;
            case 0x30000000: // CC
                condition = !cpu.getCFlag();
                break;
            case 0x40000000: // MI
                condition = cpu.getNFlag();
                break;
            case 0x50000000: // PL
                condition = !cpu.getNFlag();
                break;
            case 0x60000000: // VS
                condition = cpu.getVFlag();
                break;
            case 0x70000000: // VC
                condition = !cpu.getVFlag();
                break;
            case 0x80000000: // HI
                condition = (cpu.getCFlag() && !cpu.getZFlag());
                break;
            case 0x90000000: // LS
                condition = (!cpu.getCFlag() || cpu.getZFlag());
                break;
            case 0xA0000000: // GE
                condition = (cpu.getNFlag() == cpu.getVFlag());
                break;
            case 0xB0000000: // LT
                condition = (cpu.getNFlag() != cpu.getVFlag());
                break;
            case 0xC0000000: // GT
                condition = (!cpu.getZFlag() && (cpu.getNFlag() == cpu.getVFlag()));
                break;
            case 0xD0000000: // LE
                condition = (cpu.getZFlag() || (cpu.getNFlag() != cpu.getVFlag()));
                break;
            case 0xE0000000: // AL
                condition = true;
                break;
            default:
                condition = false;
        }
        
        return condition;
    }
    
    
    private final static String[] PreconditionSuffix = {
        "eq",
        "ne",
        "cs",
        "cc",
        "mi",
        "pl",
        "vs",
        "vc",
        "hi",
        "ls",
        "ge",
        "lt",
        "gt",
        "le",
        "",
        "nv",
    };
    
    protected static String getPreconditionSuffix(int opcode) {
        return PreconditionSuffix[(opcode >>> 28) & 0x0000000F];
    }
    
}
