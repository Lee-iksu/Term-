package util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

/**
 * 비밀번호 암호화 및 복호화를 위한 유틸리티 클래스
 */
public class PasswordEncryption {
    // 암호화 알고리즘 및 키 생성을 위한 상수
    private static final String SECRET_KEY = "chatAppSecretKey";
    private static final String SALT = "chatAppSalt";
    private static final byte[] IV = new byte[16]; // 초기화 벡터

    static {
        // IV 값 초기화 (고정된 값 사용)
        for (int i = 0; i < IV.length; i++) {
            IV[i] = (byte) i;
        }
    }

    /**
     * 주어진 비밀번호를 암호화하여 반환
     * @param password 암호화할 비밀번호
     * @return 암호화된 비밀번호 문자열
     */
    public static String encrypt(String password) {
        try {
            // 키 생성
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(SECRET_KEY.toCharArray(), SALT.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

            // Cipher 초기화
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec ivspec = new IvParameterSpec(IV);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);

            // 암호화 실행
            byte[] encrypted = cipher.doFinal(password.getBytes(StandardCharsets.UTF_8));
            
            // Base64로 인코딩하여 반환
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            System.out.println("암호화 오류: " + e.toString());
            return null;
        }
    }

    /**
     * 암호화된 비밀번호를 복호화하여 반환
     * @param encryptedPassword 암호화된 비밀번호 문자열
     * @return 복호화된 원본 비밀번호
     */
    public static String decrypt(String encryptedPassword) {
        try {
            // 키 생성
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(SECRET_KEY.toCharArray(), SALT.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

            // Cipher 초기화
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec ivspec = new IvParameterSpec(IV);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);

            // 복호화 실행
            byte[] original = cipher.doFinal(Base64.getDecoder().decode(encryptedPassword));
            
            // 문자열로 변환하여 반환
            return new String(original, StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.out.println("복호화 오류: " + e.toString());
            return null;
        }
    }
}