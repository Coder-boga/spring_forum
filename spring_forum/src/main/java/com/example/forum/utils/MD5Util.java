package com.example.forum.utils;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {
    /**
     * 对字符串加密
     * @param str
     * @return
     */
    public static String md5(String str) {
        return DigestUtils.md5Hex(str);
    }

    public static String md5Salt(String str, String salt) {
        return md5(md5(str) + salt);
    }
}
