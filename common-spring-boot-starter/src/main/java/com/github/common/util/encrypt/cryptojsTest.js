const CryptoJS = require('crypto-js');

const KP = {
    key: '1234567812345678', // 秘钥 16*n:
    iv: '1234567812345678'  // 偏移量
};
function getAesString(data, key, iv) { // 加密
    key = CryptoJS.enc.Utf8.parse(key);
    // alert(key）;
    iv = CryptoJS.enc.Utf8.parse(iv);
    let encrypted = CryptoJS.AES.encrypt(data, key,
        {
            iv,
            mode: CryptoJS.mode.CBC,
            padding: CryptoJS.pad.Pkcs7
        });
    return encrypted.toString();    // 返回的是base64格式的密文
}
function getDAesString(encrypted, key, iv) { // 解密
    key = CryptoJS.enc.Utf8.parse(key);
    iv = CryptoJS.enc.Utf8.parse(iv);
    let decrypted = CryptoJS.AES.decrypt(encrypted, key,
        {
            iv,
            mode: CryptoJS.mode.CBC,
            padding: CryptoJS.pad.Pkcs7
        });
    return decrypted.toString(CryptoJS.enc.Utf8);      //
}
// AES 对称秘钥加密
const aes = {
    en: (data) => getAesString(data, KP.key, KP.iv),
    de: (data) => getDAesString(data, KP.key, KP.iv)
};
// BASE64
const base64 = {
    en: (data) => CryptoJS.enc.Base64.stringify(CryptoJS.enc.Utf8.parse(data)),
    de: (data) => CryptoJS.enc.Base64.parse(data).toString(CryptoJS.enc.Utf8)
};
// SHA256
const sha256 = (data) => {
    return CryptoJS.SHA256(data).toString();
};
// MD5
const md5 = (data) => {
    return CryptoJS.MD5(data).toString();
};

function aesCodec(data, key) {
    // Encrypt
    const ciphertext = CryptoJS.AES.encrypt(data, key).toString();

    // Decrypt
    const bytes = CryptoJS.AES.decrypt(ciphertext, key);
    console.log("js加密后的数据:", bytes);
    const originalText = bytes.toString(CryptoJS.enc.Utf8);
    console.log("js解密后的数据:", originalText);
}
function decrypt(data, key) {
    var key = CryptoJS.enc.Utf8.parse("bWFsbHB3ZA==WNST");
    var decrypt = CryptoJS.AES.decrypt(word, key, {mode: CryptoJS.mode.ECB, padding: CryptoJS.pad.Pkcs7});
    return CryptoJS.enc.Utf8.stringify(decrypt).toString();
}

const key = "bWFsbHB3ZA==WNST";
const data = "helloWorld";
aesCodec(data, key);
console.log(aes.en(data));


