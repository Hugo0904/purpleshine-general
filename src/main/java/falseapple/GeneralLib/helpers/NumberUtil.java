package falseapple.GeneralLib.helpers;

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
}
