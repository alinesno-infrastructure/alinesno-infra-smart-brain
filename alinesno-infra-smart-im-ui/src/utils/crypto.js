// 注意：你需要确保 CryptoJS 已经正确加载或导入。
// 如果你使用的是 ES 模块系统，并且 CryptoJS 是一个模块，你应该这样导入它：
import CryptoJS from 'crypto-js';

/**
 * 随机生成32位的字符串
 */
export function generateRandomString() {
  const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
  let result = '';
  const charactersLength = characters.length;
  for (let i = 0; i < 32; i++) {
    result += characters.charAt(Math.floor(Math.random() * charactersLength));
  }
  return result;
}

/**
 * 随机生成aes 密钥
 */
export function generateAesKey() {
  return CryptoJS.enc.Utf8.parse(generateRandomString());
}

/**
 * 加密base64
 * @param {CryptoJS.lib.WordArray} str
 */
export function encryptBase64(str) {
  return CryptoJS.enc.Base64.stringify(str);
}

/**
 * 解密base64
 * @param {string} str
 */
export function decryptBase64(str) {
  return CryptoJS.enc.Base64.parse(str);
}

/**
 * 使用密钥对数据进行加密
 * @param {string} message
 * @param {CryptoJS.lib.WordArray} aesKey
 */
export function encryptWithAes(message, aesKey) {
  const encrypted = CryptoJS.AES.encrypt(message, aesKey, {
    mode: CryptoJS.mode.ECB,
    padding: CryptoJS.pad.Pkcs7
  });
  return encrypted.toString();
}

/**
 * 使用密钥对数据进行解密
 * @param {string} message
 * @param {CryptoJS.lib.WordArray} aesKey
 */
export function decryptWithAes(message, aesKey) {
  const decrypted = CryptoJS.AES.decrypt(message, aesKey, {
    mode: CryptoJS.mode.ECB,
    padding: CryptoJS.pad.Pkcs7
  });
  return decrypted.toString(CryptoJS.enc.Utf8);
}