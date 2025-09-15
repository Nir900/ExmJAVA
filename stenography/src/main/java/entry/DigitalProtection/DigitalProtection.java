package entry.DigitalProtection;

import java.security.MessageDigest;

public class DigitalProtection {
    public static byte[] sha256(byte[] input) throws Exception
    {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return md.digest(input);
    }

    public static String toHex(byte[] b)
    {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (byte x : b)
            sb.append(String.format("%02x", x & 0xFF));
        
        return sb.toString();
    }

    public static int bitDifferences(byte[] a, byte[] b)
    {
        if (a.length != b.length)
            throw new IllegalArgumentException("Lengths differ");
        
        int diff = 0;
        for (int i = 0; i < a.length; i++) {
            int xor = (a[i] ^ b[i]) & 0xFF;
            diff += Integer.bitCount(xor);
        }

        return diff;
    }
}
