package com.purpleshine.general.helpers;

import org.apache.commons.codec.digest.DigestUtils;

public class SecurityUtil {

    /**
     * 轉換MD5
     * 
     * @return
     */
    static public String md5(final String content) {
        return DigestUtils.md5Hex(content);
    }
}
