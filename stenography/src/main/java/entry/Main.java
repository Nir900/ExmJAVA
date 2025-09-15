package entry;

import entry.obfuscation.Obfuscation;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import entry.AccessControl.AccessControl;
import entry.AccessControl.Resource;
import entry.AccessControl.Role;
import entry.AccessControl.User;
import entry.ChaffWinnow.ChaffWinnow;
import entry.SelectiveEncryption.SelectiveEncryption;
import entry.lsb.LSB;
import entry.DataObfuscation.PhoneObfuscation;
import entry.DigitalProtection.DigitalProtection;
import entry.InputFiltering.InputFiltering;
import entry.NullCipher.NullCipher;
import entry.MetaEncrypt.MetaEncrypt;

public class Main {
    public static void main(String[] args) 
    {
        // Code Obfuscation
        {
            System.out.println("----- Code Obfuscation -----");

            int a = Obfuscation.asx245(5, 10); // obfuscated addition function, returns 15
            System.out.println(a + "\n");
        }

        // LSB
        {
            System.out.println("----- LSB -----");
            
            final String paths[] = {
                "src/main/java/entry/lsb/res/cursor.bmp",
                "src/main/java/entry/lsb/res/test.png"
            };
            final String embeddedString = "Hello!!!"; 

            try {
                LSB.embed(
                    paths[0],
                    paths[1],
                    embeddedString
                );

                String extractedString = LSB.extract(paths[1], embeddedString.length());

                System.out.println("Managed to extract string: " + extractedString + "\n");
            } catch (Exception e) {
                System.out.println("LSB exception: " + e + "\n");
            } finally {
                try {
                    File deleteTest = new File(paths[1]);
                    boolean status = deleteTest.delete();
                    
                    System.out.println(status ? "Cleaned resource: " + deleteTest.getName() + "\n" : "Failed to clean resource.\n");
                } catch (Exception e) {
                    System.out.println("Exception: " + e);
                }
            }
        }

        // Selective encryption
        {
            System.out.println("----- Selective Encryption -----");

            byte[] k = "12345678901234567890123456789012".getBytes();
            SecretKey key = new SecretKeySpec(k, "AES");
            String t = "ID: 12345678, Card: 4111-1111-1111-1111.";
            
            try {
                String enc = SelectiveEncryption.protect(t, key);
                System.out.println("Protected: " + enc + "\n");
            } catch (Exception e) {
                System.out.println("SelectiveEncryption.protect() exception: " + e + "\n");
            }
        }
        
        // AccessControl
        {
            System.out.println("----- Access Control -----");

            AccessControl acl = new AccessControl();

            acl.grant(Role.GUEST, "public");
            acl.grant(Role.EMPLOYEE, "public");
            acl.grant(Role.EMPLOYEE, "internal");

            User admin = new User("Alice", Role.ADMIN);
            User employee = new User("Bob", Role.EMPLOYEE);
            User guest = new User("Eve", Role.GUEST);

            Resource pub = new Resource("public", "Public info");
            Resource internal = new Resource("internal", "Internal employee data");
            Resource secret = new Resource("secret", "Admin secret");

            AccessControl.tryRead(guest, pub, acl);
            AccessControl.tryRead(guest, internal, acl);
            AccessControl.tryRead(employee, internal, acl);
            AccessControl.tryRead(employee, secret, acl);
            AccessControl.tryRead(admin, secret, acl);

            System.out.println();
        }

        // Data Obfuscator
        {
            System.out.println("----- Data Obfuscator -----");

            List<String> phones = Arrays.asList("052-1234567", "+1 (555) 987-6543", "03 7654321");
            List<String> obf = PhoneObfuscation.obfuscateList(phones, 3);

            System.out.println("Obfuscated: " + obf);
            System.out.println("Restored: " + PhoneObfuscation.deobfuscateList(obf) + "\n");
        }
        

        // Null Cipher
        {
            System.out.println("----- Null Cipher -----");

            String cover = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer nec bibendum nisi. Mauris egestas nisl a ex convallis, nec maximus mauris dignissim. Aliquam non lacinia dui, eget efficitur arcu. Donec commodo dui eu lorem varius sollicitudin. Nunc sem lorem, fringilla quis congue eget, venenatis at risus. Cras fermentum tortor at ante fringilla accumsan. Phasellus id mi sollicitudin, lobortis ex ac, dapibus ex. Cras imperdiet aliquam feugiat.";

            String secret = "HELLO";

            String stegoText = NullCipher.embedSecret(cover, secret, 3, 2);
            System.out.println("stegoText: " + stegoText + "\n");

            String extracted = NullCipher.extractSecret(stegoText, 3, 2);
            System.out.println("Extracted Text: " + extracted + "\n");
        }

        // Input Filtering
        {
            System.out.println("----- Input Filtering -----");

            InputFiltering auth = new InputFiltering();

            auth.register("dan_01", "dan!Pass"); // succeeds
            auth.authenticate("dan_01", "dan!Pass"); // succeeds
            auth.authenticate("dan_01", "wrong"); // fails
            auth.authenticate("dan_01", "dan!Pass1"); // fails

            auth.register("bad;user", "password"); // fails
            auth.register("bob", "short"); // fails

            System.out.println();
        }

        // Digital Protection
        {
            System.out.println("----- Digital Protection -----");

            String original = "The quick brown fox jumps over the lazy dog";
            String modified = "The quick brown fox jumps over the lazy dog.";

            try { 
                byte[] h1 = DigitalProtection.sha256(original.getBytes("UTF-8")); 
                byte[] h2 = DigitalProtection.sha256(modified.getBytes("UTF-8"));

                System.out.println("Original: " + original);
                System.out.println("Hash 1: " + DigitalProtection.toHex(h1) + "\n");

                System.out.println("Modified: " + modified);
                System.out.println("Hash 2: " + DigitalProtection.toHex(h2) + "\n");

                System.out.println("Equal hashes? " + Arrays.equals(h1, h2));
                System.out.println("Differing bits: " + DigitalProtection.bitDifferences(h1, h2) + " out of " + (h1.length * 8) + "\n");
            } catch (Exception e) {
                System.out.println("Digital Protection Exception: " + e + "\n");
            }
        }

        // Chaffing and Winnowing
        {
            System.out.println("----- Chaffing and Winnowing -----");

            String secret = "AttackAtDawn";
            List<String> chaffMessages = List.of("Hello", "World", "Fun", "Spam", "RuN");

            String secretTag = "KEY0123456789";

            // Chaffing
            List<String> mixed = ChaffWinnow.chaff(secret, chaffMessages, secretTag);
            System.out.println("Mixed messages: " + mixed);

            // Winnowing
            List<String> extracted = ChaffWinnow.winnow(mixed, secretTag);
            System.out.println("Extracted secret: " + extracted + "\n");
        }

        // MetaEncrypt
        {
            System.out.println("----- MetaEncrypt -----");

            String document = "Confidential document!";
            Map<String, String> metadata = new HashMap<>();
            
            metadata.put("Author", "John");
            metadata.put("Date", "1999-05-05");
            metadata.put("Location", "NYC");

            try {
                KeyGenerator kg = KeyGenerator.getInstance("AES");
                kg.init(128);
                SecretKey key = kg.generateKey();
                byte[] iv = new byte[12];

                new java.security.SecureRandom().nextBytes(iv);

                Map<String, String> encryptedMeta = new HashMap<>();
                for (String k : metadata.keySet())
                    encryptedMeta.put(k, MetaEncrypt.encryptMeta(metadata.get(k), key, iv));
                
                System.out.println("Document: " + document);
                System.out.println("Encrypted metadata: " + encryptedMeta);

                Map<String, String> decryptedMeta = new HashMap<>();
                for (String k : encryptedMeta.keySet())
                    decryptedMeta.put(k, MetaEncrypt.decryptMeta(encryptedMeta.get(k), key, iv));
                
                System.out.println("Decrypted metadata: " + decryptedMeta + "\n");
            } catch (Exception e) {
                System.out.println("MetaEncrypt Exception: " + e);
            }
        }
    }
}

