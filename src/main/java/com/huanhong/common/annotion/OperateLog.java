
package com.huanhong.common.annotion;


import com.huanhong.common.enums.OperateType;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface OperateLog {

    /**
     * 业务的名称,例如:"修改菜单"
     */
    String title() default "";

    /**
     * 业务操作类型枚举
     */
    OperateType type() default OperateType.OTHER;
}
