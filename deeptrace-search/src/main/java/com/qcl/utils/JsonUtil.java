package com.qcl.utils;

import com.alibaba.fastjson.JSON;

public class JsonUtil {
    /**
     * 判断字符串是否为有效的JSON格式
     * @param str 待验证的字符串
     * @return 是否为JSON格式
     */
    public static boolean isValidJson(String str) {
        if (str == null || str.trim().isEmpty()) {
            return false;
        }
        try {
            JSON.parseObject(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
