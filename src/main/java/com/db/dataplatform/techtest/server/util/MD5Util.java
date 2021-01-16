package com.db.dataplatform.techtest.server.util;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {
    public static String getMd5Hash(String input) {
        return DigestUtils.md5Hex(input);
    }
}
