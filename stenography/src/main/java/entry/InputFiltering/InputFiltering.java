package entry.InputFiltering;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class InputFiltering {
    private static final Pattern SAFE_USERNAME = Pattern.compile(
        "^[A-Za-z0-9_.-]{3,30}$"
    );
    
    private static final Pattern SAFE_PASSWORD = Pattern.compile(
        "^[A-Za-z0-9!@#$%^&*()_+\\-={}:;\"',.<>/?]{8,64}$"
    );

    private static final Pattern SQL_INJECTION = Pattern.compile(
        "(?i)(\\b(select|insert|update|delete|drop|union|--|;|\\bor\\b|\\band\\b)\\b)"
    );

    private static final int SALT_BYTES = 16;
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;

    private static final SecureRandom RNG = new SecureRandom();

    private final Map<String, String> users = new HashMap<>(); 

    public static String generateSaltBase64()
    {
        byte[] s = new byte[SALT_BYTES];
        RNG.nextBytes(s);

        return Base64.getEncoder().encodeToString(s);
    }

    private static String pbkdf2HashBase64(char[] password, byte[] salt)
        throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] hash = skf.generateSecret(spec).getEncoded();

        return Base64.getEncoder().encodeToString(hash);
    }

    private static boolean constantTimeEquals(String a, String b)
    {
        byte[] x = a.getBytes();
        byte[] y = b.getBytes();

        if (x.length != y.length)
            return false;
        
        int result = 0;
        for (int i = 0; i < x.length; i++)
            result |= x[i] ^ y[i];
        
        return result == 0;
    }

    public static boolean validateInput(String username, String password)
    {
        if (username == null || password == null)
            return false;
        
        if (!SAFE_USERNAME.matcher(username).matches())
            return false;
        
        if (!SAFE_PASSWORD.matcher(password).matches())
            return false;
        
        if (SQL_INJECTION.matcher(username).find())
            return false;
        
        if (SQL_INJECTION.matcher(password).find())
            return false;

        return true;
    }

    public boolean register(String username, String password)
    {
        try {
            if (!validateInput(username, password)) {
                System.out.println("Invalid input (failed validation).");
                return false;
            }

            if (users.containsKey(username)) {
                System.out.println("User already exists.");
                return false;
            }

            String saltB64 = generateSaltBase64();
            String hashB64 = pbkdf2HashBase64(password.toCharArray(), Base64.getDecoder().decode(saltB64));

            users.put(username, saltB64 + ":" + hashB64);
            System.out.println("User registered: " + username);

            return true;
        } catch (Exception e) {
            throw new RuntimeException("Error during registration", e);
        }
    }

    public boolean authenticate(String username, String password)
    {
        try {
            if (!validateInput(username, password)) {
                System.out.println("Invalid input (failed validation).");
                return false;
            }
            
            String stored = users.get(username);
            if (stored == null) {
                System.out.println("Authentication failed.");
                return false;
            }

            String[] parts = stored.split(":", 2);
            if (parts.length != 2)
                return false;
            
            String saltB64 = parts[0];
            String hashB64 = parts[1];
            String candidateHash = pbkdf2HashBase64(password.toCharArray(), Base64.getDecoder().decode(saltB64));

            boolean status = constantTimeEquals(hashB64, candidateHash);
            System.out.println(status ? "Authentication successful." : "Authentication failed.");

            return status;
        } catch (Exception e) {
            throw new RuntimeException("Error during authentication", e);
        }
    }
}
