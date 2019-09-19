package com.purpleshine.general.helpers;

public class NumberUtil {
    /**
     *  判斷是否為數字
     * @param str - 需判斷字串
     * @return - true if is number
     */
    static public boolean isFloatNumeric(String str) {
        return str.matches("[\\.0-9]+\\.*[0-9]*");
    }
    
    /**
     *  判斷是否為整數數字
     * @param str - 需判斷字串
     * @return - true if is int number
     */
    static public boolean isNumeric(String str) {
        return str.matches("-?[0-9]+");
    }
    
    /**
     * 派斷某個數字字串是否大於某值
     * @param num - 數字字串
     * @param min - 判斷值
     * @return - true if number if bigger than min num.
     */
    static public boolean isNumericAndValue(String num, int min) {
        return isFloatNumeric(num) && Integer.parseInt(num) > min;
    }
    
    /**
     * 數字不為0
     * 
     * @param num
     * @return
     */
    static public boolean isZero(Object num) {
        return ((Number) num).longValue() == 0;
    }
    
    /**
     * 數字不為0
     * 
     * @param num
     * @return
     */
    static public boolean isNotZero(Object num) {
        return ((Number) num).longValue() != 0;
    }
    
    /**
     * 數字大於等於0
     * 
     * @param num
     * @return
     */
    static public boolean isMaxZero(Object num) {
        return ((Number) num).longValue() >= 0;
    }
    
    /**
     * 數字大於0
     * 
     * @param num
     * @return
     */
    static public boolean isMaxThanZero(Object num) {
        return ((Number) num).longValue() > 0;
    }
    
    /**
     * 數字小於等於0
     * 
     * @param num
     * @return
     */
    static public boolean isMinZero(Object num) {
        return ((Number) num).longValue() <= 0;
    }
    
    /**
     * 數字小於0
     * 
     * @param num
     * @return
     */
    static public boolean isMinThanZero(Object num) {
        return ((Number) num).longValue() < 0;
    }
}
