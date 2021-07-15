package cn.philip.common.utils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @description: 加密工具类
 * @author: pfliu
 * @time: 2020/7/17
 */
public class EncryptUtil {

    private static MessageDigest SHA_Degist = null;

    /**
     * constructor.
     */
    public EncryptUtil() throws Exception {
        if (SHA_Degist == null) {
            SHA_Degist = MessageDigest.getInstance("SHA");
        }
    }

    /**
     * 生成加密字符串
     *
     * @param str         字符串
     * @param algorithm   加密方式
     * @param isLowerCase 是否小写
     * @param encoding    编码
     * @param length      长度
     */
    public static String getEncrypt(String str, String algorithm, boolean isLowerCase, String encoding, int length) {
        MessageDigest msgDigest = null;
        String code = "";
        try {
            algorithm = algorithm.toUpperCase();
            msgDigest = MessageDigest.getInstance(algorithm);
            // 汉字加密需加"UTF-8"编码
            msgDigest.update(str.getBytes(encoding));
            byte[] bytes = msgDigest.digest();
            BigInteger bigInt = new BigInteger(1, bytes);
            if (isLowerCase) {
                code = bigInt.toString(16).toLowerCase();
            } else {
                code = bigInt.toString(16).toUpperCase();
            }
            if (code.length() >= length) {
                code = code.substring(0, length);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return code;
    }

    /**
     * SHA加密
     *
     * @param src 源字符串
     */
    public static String getSha(String src) throws Exception {
        if ((src != null) && (src.indexOf("{SHA}") == 0)) {
            return src.substring(5);
        }
        return new String(Base64Util.encode(shaCrypt(src)));
    }

    private static byte[] shaCrypt(String src) throws Exception {
        if (SHA_Degist == null) {
            SHA_Degist = MessageDigest.getInstance("SHA");
        }
        SHA_Degist.update(src.getBytes());
        return SHA_Degist.digest();
    }

    public static void main(String[] args) {
        try{
            String salt = PkGenerateUtil.getSalt();
            System.out.println(salt);
            System.out.println(getSha("111111" + salt));
        } catch (Exception e){

        }
    }
}
