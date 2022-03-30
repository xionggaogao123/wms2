package com.huanhong.common.units;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ldy81
 * @date 2019/12/12 15:16
 */
@Slf4j
public class StrUtils extends StrUtil {

    /**
     * 格式化文本，使用 {varName} 占位<br>
     * map = {a: "aValue", b: "bValue"} format("{a} and {b}", map) ---=》 aValue and bValue
     *
     * @param template 文本模板，被替换的部分用 {key} 表示
     * @param map      参数值对
     * @param prex     占位符前缀 例 $   ${varName}
     * @return 格式化后的文本
     */
    public static String format(CharSequence template, Map<?, ?> map, String prex) {
        if (null == template) {
            return null;
        }
        if (null == map || map.isEmpty()) {
            return template.toString();
        }

        String template2 = template.toString();
        String value;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            value = utf8Str(entry.getValue());
            if (null != value) {
                template2 = replace(template2, prex + "{" + entry.getKey() + "}", value);
            }
        }
        return template2;
    }

    /**
     * 获取后缀
     *
     * @param str 字符  test.mp4
     * @return mp4
     * @author 刘德宜 wudihaike@vip.qq.com
     * @since 2018/4/17 20:31
     */
    public static String getSuffix(CharSequence str) {
        return getSuffix(str, ".", null);
    }

    /**
     * 获取后缀
     *
     * @param str    字符  test.mp4
     * @param suffix 后缀  .
     * @return mp4
     * @author 刘德宜 wudihaike@vip.qq.com
     * @since 2018/4/17 20:31
     */
    public static String getSuffix(CharSequence str, CharSequence suffix) {
        return getSuffix(str, suffix, null);
    }

    public static String getSuffix(CharSequence str, CharSequence suffix, CharSequence suffix2) {
        int lastIndex = lastIndexOfIgnoreCase(str, suffix);
        int endIndex;
        if (null != suffix2) {
            endIndex = indexOfIgnoreCase(str, suffix2);
        } else {
            endIndex = str.length();
        }
        return sub(str, lastIndex, endIndex);
    }

    /**
     * 将字符串的首字母转大写
     *
     * @param str 需要转换的字符串
     * @return String
     */
    private static String captureName(String str) {
        // 进行字母的ascii编码前移，效率要高于截取字符串进行转换的操作
        char[] cs = str.toCharArray();
        cs[0] -= 32;
        return String.valueOf(cs);
    }

    /**
     * 对象转换JSON字符串
     *
     * @param data 对象
     * @return JSON字符串
     */
    public static String toJSONStr(Object data) {
        try {
            return JSON.toJSONString(data);
        } catch (Exception e) {
            log.error("转换JSON字符串错误", e);
        }
        return null;
    }

    /**
     * 过滤特殊字符
     *
     * @param str
     * @return
     */
    public static String HandleData(String str) {
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\-\\-\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    /**
     * 判断是否含有非数字
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        Pattern p = Pattern.compile("[0-9]*");
        return p.matcher(str).matches();
    }

    /**
     * 判断是否是除I和O以外的大写字母
     */
    public static boolean isEnglish(String str){
        Pattern p = Pattern.compile("[ABCDEFGHJKLMNPQRSTUVWXYZ]");
        return p.matcher(str).matches();
    }


    /**
     * 截取str字符串
     * @param str
     * @param start
     * @return
     */
    public static String subStr(String str, int start) {
        if (str == null || str.equals("") || str.length() == 0)
            return "";
        if (start < str.length()) {
            return str.substring(start);
        } else {
            return "";
        }
    }


    public static JSONObject toJSONObject(String str) {
        try {
            return JSON.parseObject(str);
        } catch (Exception e) {
            log.error("JSON Exchange Error", e);
            return null;
        }
    }

}
