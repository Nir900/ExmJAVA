package entry.MetaEncrypt;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public class MetaEncrypt {
    private static final String AES_GCM = "AES/GCM/NoPadding";
    private static final int GCM_TAG = 128;

    public static String encryptMeta(String meta, SecretKey key, byte[] iv) throws Exception
    {
        Cipher cipher = Cipher.getInstance(AES_GCM);
        cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(GCM_TAG, iv));
        byte[] encrypted = cipher.doFinal(meta.getBytes("UTF-8"));
        
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public static String decryptMeta(String encrypted, SecretKey key, byte[] iv)
    {
        try {
            Cipher cipher = Cipher.getInstance(AES_GCM);
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG, iv));
            byte[] decoded = Base64.getDecoder().decode(encrypted);

            return new String(cipher.doFinal(decoded), "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
