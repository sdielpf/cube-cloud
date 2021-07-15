package cn.philip.common.utils;

import java.util.UUID;

/**
 * @description: 主键生成工具类
 * @author: pfliu
 * @time: 2020/7/17
 */
public class PkGenerateUtil {

    /**
     * 生成UUID主键(带短横线）
     */
    public static String uuid() {
        return UUID.randomUUID().toString();
    }

    /**
     * 生成UUID主键(无短横线）
     */
    public static String uuidNoLine() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 雪花算法主键（long类型）
     */
    public static long snowFlake() {
        return SnowFlakeUtil.getFlowIdInstance().nextId();
    }

    /**
     * 雪花算法主键（String类型）
     */
    public static String snowFlakeStr() {
        return String.valueOf(SnowFlakeUtil.getFlowIdInstance().nextId());
    }

    /**
     * 随机生成六位加盐字符串.
     */
    public static String getSalt() {
        return EncryptUtil.getEncrypt(snowFlakeStr(), "MD5", true, "UTF-8", 6);
    }

    public static void main(String[] args) {
        for(int i=0;i<186;i++){
            System.out.println(snowFlakeStr());
        }
    }
}
