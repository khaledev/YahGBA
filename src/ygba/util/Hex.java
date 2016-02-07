package ygba.util;

public final class Hex {
    
    public final static byte
            Bit_00 = 0,
            Bit_04 = 1,
            Bit_08 = 2,
            Bit_12 = 3,
            Bit_16 = 4,
            Bit_20 = 5,
            Bit_24 = 6,
            Bit_28 = 7,
            Bit_32 = 8;
    
    public final static byte
            Byte     = Bit_08,
            HalfWord = Bit_16,
            Word     = Bit_32;
    
    private final static char[] HexChar = {
        '0', '1', '2', '3',
        '4', '5', '6', '7',
        '8', '9', 'A', 'B',
        'C', 'D', 'E', 'F'
    };
    
    
    public static String toString(int value) {
        String hex = "";
        do {
            hex = HexChar[value & 0x0000000F] + hex;
            value >>>= 4;
        } while (value != 0);
        
        return hex;
    }
    
    public static String toString(int value, byte format) {
        String hex = toString(value);
        while (hex.length() < format) hex = "0" + hex;
        
        return hex.substring(hex.length() - format, hex.length());
    }
    
    public static String toHexString(int value) {
        return "0x" + toString(value);
    }
    
    public static String toHexString(int value, byte format) {
        return "0x" + toString(value, format);
    }
    
    public static String toAddrString(int value) {
        return "$" + toString(value);
    }
    
    public static String toAddrString(int value, byte format) {
        return "$" + toString(value, format);
    }
    
}
