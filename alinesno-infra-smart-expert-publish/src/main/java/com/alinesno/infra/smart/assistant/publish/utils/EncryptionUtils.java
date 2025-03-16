package com.alinesno.infra.smart.assistant.publish.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;

/**
 * 加密和解密工具类，包含 MD5、SHA - 256 和 AES 加密解密方法
 */
@Slf4j
public class EncryptionUtils {

    public static final String DEFAULT_KEY = "1234567890123456" ;

    /**
     * MD5 加密方法
     *
     * @param input 待加密的字符串
     * @return 加密后的十六进制字符串
     */
    public static String md5(String input) {
        try {
            // 获取 MD5 算法的 MessageDigest 实例
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 对输入的字符串进行加密
            byte[] messageDigest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            // 将字节数组转换为十六进制字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String hex = Integer.toHexString(0xFF & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error("MD5 加密时发生异常", e);
            return null;
        }
    }

    /**
     * SHA - 256 加密方法
     *
     * @param input 待加密的字符串
     * @return 加密后的十六进制字符串
     */
    public static String sha256(String input) {
        try {
            // 获取 SHA - 256 算法的 MessageDigest 实例
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            // 对输入的字符串进行加密
            byte[] encodedHash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            // 将字节数组转换为十六进制字符串
            StringBuilder hexString = new StringBuilder(2 * encodedHash.length);
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error("SHA-256 加密时发生异常", e);
            return null;
        }
    }

    /**
     * AES 加密方法
     *
     * @param input 待加密的字符串
     * @param key   加密密钥
     * @return 加密后的 Base64 编码字符串
     * @throws Exception 加密过程中可能抛出的异常
     */
    public static String aesEncrypt(String input, String key) throws Exception {
        try {
            // 获取 AES 算法的 KeyGenerator 实例
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            // 使用 SecureRandom 生成安全的随机数
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(key.getBytes());
            // 初始化 KeyGenerator，设置密钥长度为 128 位
            keyGenerator.init(128, secureRandom);
            // 生成密钥
            SecretKey secretKey = keyGenerator.generateKey();
            byte[] raw = secretKey.getEncoded();
            // 创建 SecretKeySpec 对象
            SecretKeySpec secretKeySpec = new SecretKeySpec(raw, "AES");
            // 获取 Cipher 实例，使用 AES 算法进行加密
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            // 对输入的字符串进行加密
            byte[] encrypted = cipher.doFinal(input.getBytes(StandardCharsets.UTF_8));
            // 将加密后的字节数组进行 Base64 编码
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            log.error("AES 加密时发生异常，输入: {}, 密钥: {}", input, key, e);
            throw e;
        }
    }

    /**
     * AES 解密方法
     *
     * @param encrypted 加密后的 Base64 编码字符串
     * @param key       解密密钥
     * @return 解密后的字符串
     * @throws Exception 解密过程中可能抛出的异常
     */
    public static String aesDecrypt(String encrypted, String key) throws Exception {
        try {
            // 获取 AES 算法的 KeyGenerator 实例
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            // 使用 SecureRandom 生成安全的随机数
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(key.getBytes());
            // 初始化 KeyGenerator，设置密钥长度为 128 位
            keyGenerator.init(128, secureRandom);
            // 生成密钥
            SecretKey secretKey = keyGenerator.generateKey();
            byte[] raw = secretKey.getEncoded();
            // 创建 SecretKeySpec 对象
            SecretKeySpec secretKeySpec = new SecretKeySpec(raw, "AES");
            // 获取 Cipher 实例，使用 AES 算法进行解密
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            // 对 Base64 编码的加密字符串进行解码
            byte[] decoded = Base64.getDecoder().decode(encrypted);
            // 对解码后的字节数组进行解密
            byte[] decrypted = cipher.doFinal(decoded);
            // 将解密后的字节数组转换为字符串
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("AES 解密时发生异常，加密字符串: {}, 密钥: {}", encrypted, key, e);
            throw e;
        }
    }
}