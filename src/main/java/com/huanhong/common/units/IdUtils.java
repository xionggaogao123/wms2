package com.huanhong.common.units;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;

import java.time.LocalDate;
import java.util.Date;

/**
 * ID生成工具类
 */
public class IdUtils extends IdUtil {

    /**
     * 生成编号
     *
     * @param type 1.类型
     * @return 编号
     */
    public static String getNo(Integer type) {
        if (type == 1) {
            return createSnowflake(0, 0).nextIdStr();
        }
        return null;
    }

    /**
     * 默认规则
     *
     * @return
     */
    public static String getNo() {
        return getNo(1);
    }

    /**
     * 生成一个简单的10位编号(可能重复)
     *
     * @return 编号 年+月+日+(4~6位随机数字) 例:19 6 18 78023
     */
    public static String fastTenNo() {
        String date = DateUtil.format(new Date(), "yyMd");
        int length = 5;
        if (date.length() == 4) {
            length = 6;
        } else if (date.length() == 6) {
            length = 4;
        }
        return date + RandomUtil.randomNumbers(length);
    }

    /**
     * 生成加密的十位编号(年月日)
     *
     * @return 编号 年+(0~9)+月+(0~9)+日+(4~6位随机数字) 例:20 35 58 7802
     */
    public static String fastEncryptTenNo() {
        LocalDate now = LocalDate.now();
        int month = now.getMonthValue();
        int day = now.getDayOfMonth();
        StringBuilder date = new StringBuilder();
        date.append(now.getYear()).delete(0, 2);
        if (month < 10) {
            date.append(RandomUtil.randomInt(2, 9));
        }
        date.append(month);
        if (day < 10) {
            date.append(RandomUtil.randomInt(4, 9));
        }
        date.append(day);
        return date.append(RandomUtil.randomNumbers(4)).toString();
    }

}
