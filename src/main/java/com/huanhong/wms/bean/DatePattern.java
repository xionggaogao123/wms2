package com.huanhong.wms.bean;

import java.time.format.DateTimeFormatter;

public class DatePattern extends cn.hutool.core.date.DatePattern {

    public static final String CHINESE_DATE_TIME_PATTERN = "yyyy年MM月dd日 HH:mm";
    public static final DateTimeFormatter CHINESE_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(CHINESE_DATE_TIME_PATTERN);

}
