package entry.DataObfuscation;

import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;

public class PhoneObfuscation {
    private static final SecureRandom RAND = new SecureRandom();
    private static final String CHARS = "abcdefghijklmnopqrstuvwxyz";

    public static String obfuscate(String phone, int everyN)
    {
        String digits = phone.replaceAll("\\D", "");
        StringBuilder sb = new StringBuilder();
        int count = 0;

        for (char d : digits.toCharArray()) {
            sb.append(d);
            count++;
            if (count % everyN == 0 && count < digits.length()) 
                sb.append(CHARS.charAt(RAND.nextInt(CHARS.length())));
        }

        return sb.toString();
    }

    public static List<String> obfuscateList(List<String> phones, int everyN) 
    {
        return phones.stream().map(p -> obfuscate(p, everyN)).collect(Collectors.toList());
    }

    public static String deobfuscate(String obf) 
    {
        return obf.replaceAll("\\D", "");
    }

    public static List<String> deobfuscateList(List<String> obfs) 
    {
        return obfs.stream().map(PhoneObfuscation::deobfuscate).collect(Collectors.toList());
    }
}
