package entry.SelectiveEncryption;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public class SelectiveEncryption {
    private static final Pattern ID = Pattern.compile("\\b\\d{5,9}\\b");
    private static final Pattern CC = Pattern.compile("\\b(?:\\d[ -]*){13,19}\\b");
    private static final SecureRandom RAND = new SecureRandom();

    public static String aes(String txt, SecretKey key, boolean enc) 
        throws Exception 
    {
        Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
        
        if (enc) {
            byte[] iv = new byte[12]; 
            RAND.nextBytes(iv);

            c.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(128, iv));

            byte[] ct = c.doFinal(txt.getBytes(StandardCharsets.UTF_8));
            byte[] out = new byte[iv.length + ct.length];

            System.arraycopy(iv,0,out,0,iv.length);
            System.arraycopy(ct,0,out,iv.length,ct.length);

            return Base64.getEncoder().encodeToString(out);
        } else {
            byte[] all = Base64.getDecoder().decode(txt);
            byte[] iv = java.util.Arrays.copyOfRange(all,0,12);
            byte[] ct = java.util.Arrays.copyOfRange(all,12,all.length);

            c.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(128, iv));

            return new String(c.doFinal(ct), StandardCharsets.UTF_8);
        }
    }

    public static String protect(String text, SecretKey key) throws Exception 
    {
        for (Pattern p : new Pattern[]{CC, ID}) {
            Matcher m = p.matcher(text);
            StringBuffer sb = new StringBuffer();
            
            while (m.find()) 
                m.appendReplacement(sb, "<ENC:"+aes(m.group(),key,true)+">");
            
            m.appendTail(sb); 
            text = sb.toString();
        }

        return text;
    }
}
