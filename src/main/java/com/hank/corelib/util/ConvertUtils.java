package com.hank.corelib.util;

/**
 * @version V1.0 <Describes the function of the current version>
 * @FileName: com.hank.corelib.util.ConvertUtils.java
 * @author: Hank
 * @date: 2016-10-19 14:53
 */

public class ConvertUtils {
    private static final String TAG = "ConvertUtils";
    /**
     *
     * @Title: convertToInt
     * @Description: 对象转化为整数数字类型
     * @param value
     * @param defaultValue
     * @return integer
     * @throws
     */
    public final static int convertToInt(Object value, int defaultValue) {
        if (value == null || "".equals(value.toString().trim())) {
            return defaultValue;
        }
        try {
            return Integer.valueOf(value.toString());
        } catch (Exception e) {
            try {
                return Double.valueOf(value.toString()).intValue();
            } catch (Exception e1) {
                return defaultValue;
            }
        }
    }
}
