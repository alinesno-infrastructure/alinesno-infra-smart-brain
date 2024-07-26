package com.alinesno.infra.smart.assistant.im.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;

/**
 * 钉钉机器人签名工具
 */
@Slf4j
public class DingtalkSignUtils {

    /**
     * 签名工具
     * @param secret
     * @return
     */
    public static String signWebHook(String secret , String webhook) {
        Long timestamp = System.currentTimeMillis();

        String stringToSign = timestamp + "\n" + secret;
        Mac mac = null;

        try {
            mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"));

            byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
            String sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)),"UTF-8");

            log.debug("secret = {} , sign = {}" , secret , sign);

            return webhook +
                    "&timestamp=" + timestamp +
                    "&sign=" + sign;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
