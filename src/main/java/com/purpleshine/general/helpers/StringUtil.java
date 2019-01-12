package com.purpleshine.general.helpers;

import java.util.List;
import java.util.Objects;

public class StringUtil {
    
    /**
     * 將第一個字符轉換為大寫
     * @param str
     * @return
     */
    static public String ucfirst(String str) {
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    /**
     * 將陣列或容器串接成字串
     * EX: String[] strings = {"a", "b"};  sign = $, 得到 a$b
     * @param o - 容器
     * @param sign - 元素之間串接的符號
     * @return - 串接後的字串
     */
    static public String strops(Object o, String sign) {
        String[] s = { "" };
        if (o instanceof List) {
            ((List<?>) o).forEach(i -> s[0] += i.toString() + sign);
        } else if (o instanceof String[]) {
            for (String a : (String[]) o) {
                s[0] += a + sign;
            }
        } else
            throw new IllegalArgumentException("只能是List或是String陣列...");
        return s[0].length() > 0 ? s[0].substring(0, s[0].length() - 1) : "";
    }
    
    /**
     * 判斷字串是否為空值
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        return Objects.isNull(str) || str.length() == 0;
    }
    
    /**
     *  String型態數字增加 
     * @param num - 字串數字
     * @param v - 增加多少
     * @return - 回傳增加後的數字字串
     */ 
    static public String addStringNum(String num, int v) {
        return String.valueOf(Integer.parseInt(num) + v);
    }
    
    /**
     * 比對多個內容是否符合其中一個
     * @param value
     * @param equels
     * @return
     */
    static public boolean equealsAny(String value, String... equels) {
        for (String value2 : equels) {
            if (value.equals(value2)) {
                return true;
            }
        }
        return false;
    }
}
