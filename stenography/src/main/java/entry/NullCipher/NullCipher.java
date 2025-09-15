package entry.NullCipher;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NullCipher {
    public static String embedSecret(String coverText, String secret, int wordN, int letterM)
    {
        if (wordN <= 0 || letterM <= 0) 
            throw new IllegalArgumentException("wordN and letterM must be positive.");
        
        List<String> parts = new ArrayList<>();
        Matcher matcher = Pattern.compile("(\\S+)|(\\s+)").matcher(coverText);
        while (matcher.find())
            parts.add(matcher.group());
        
        List<int[]> slots = new ArrayList<>();
        int wordCount = 0;
        
        for (int i = 0; i < parts.size(); i++) {
            String token = parts.get(i);
            
            if (token.trim().isEmpty())
                continue;
            
            wordCount++;
            if (wordCount % wordN == 0) {
                int letterIndex = 0;
                for (int pos = 0; pos < token.length(); pos++) {
                    char c = token.charAt(pos);

                    if (Character.isLetter(c)) {
                        letterIndex++;
                        if (letterIndex % letterM == 0)
                            slots.add(new int[]{i, pos});
                    }
                }
            }
        }    

        if (secret.length() > slots.size())
            throw new IllegalArgumentException("Secret too long for this pattern. Slots: " + slots.size());
    
        for (int s = 0; s < secret.length(); s++) {
            int[] slot = slots.get(s);
            int partIdx = slot[0];
            int charIdx = slot[1];

            StringBuilder sb = new StringBuilder(parts.get(partIdx));
            sb.setCharAt(charIdx, secret.charAt(s));
            parts.set(partIdx, sb.toString());
        }

        return String.join("", parts);
    }

    public static String extractSecret(String text, int wordN, int letterM)
    {
        List<String> parts = new ArrayList<>();
        Matcher matcher = Pattern.compile("(\\S+)|(\\s+)").matcher(text);
        while (matcher.find())
            parts.add(matcher.group());
        
        StringBuilder secret = new StringBuilder();
        int wordCount = 0;

        for (int i = 0; i < parts.size(); i++) {
            String token = parts.get(i);
            
            if (token.trim().isEmpty())
                continue;
            
            wordCount++;
            if (wordCount % wordN == 0) {
                int letterIndex = 0;

                for (int pos = 0; pos < token.length(); pos++) {
                    char c = token.charAt(pos);

                    if (Character.isLetter(c)) {
                        letterIndex++;
                        if (letterIndex % letterM == 0) 
                            secret.append(c);
                    }
                }
            }
        }

        return secret.toString();
    }
}
