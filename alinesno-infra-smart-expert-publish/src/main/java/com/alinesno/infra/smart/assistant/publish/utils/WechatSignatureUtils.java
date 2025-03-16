package com.alinesno.infra.smart.assistant.publish.utils;

import lombok.extern.slf4j.Slf4j;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * 微信签名验证工具类
 */
@Slf4j
public class WechatSignatureUtils {

    /**
     * 验证微信签名
     *
     * @param signature 微信加密签名，signature结合了开发者填写的token参数和请求中的timestamp参数、nonce参数。
     * @param timestamp 时间戳
     * @param nonce     随机数
     * @param token     公众号配置的token
     * @return 验证结果，true表示验证通过，false表示验证失败
     */
    public static boolean validateSignature(String signature, String timestamp, String nonce, String token) {
        // 将token、timestamp、nonce三个参数进行字典序排序
        String[] arr = new String[]{token, timestamp, nonce};
        Arrays.sort(arr);

        // 将三个参数字符串拼接成一个字符串
        StringBuilder content = new StringBuilder();
        for (String s : arr) {
            content.append(s);
        }

        // 对拼接后的字符串进行sha1加密
        String sha1Str = sha1(content.toString());

        // 将加密后的字符串与微信服务器发送的签名进行比较
        return sha1Str != null && sha1Str.equals(signature);
    }

    /**
     * 对字符串进行sha1加密
     *
     * @param str 待加密的字符串
     * @return 加密后的字符串，若加密失败则返回null
     */
    private static String sha1(String str) {
        try {
            // 获取SHA-1加密算法的MessageDigest实例
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            // 将字符串转换为字节数组并进行加密
            byte[] bytes = digest.digest(str.getBytes());
            // 将字节数组转换为十六进制字符串
            StringBuilder hexStr = new StringBuilder();
            for (byte b : bytes) {
                String hex = Integer.toHexString(0xFF & b);
                if (hex.length() == 1) {
                    hexStr.append('0');
                }
                hexStr.append(hex);
            }
            return hexStr.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error("SHA-1 加密时发生异常", e);
            return null;
        }
    }
}