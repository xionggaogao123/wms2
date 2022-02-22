package com.huanhong.common.handler;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        //this.strictInsertFill(metaObject, "createTime", Date.class, new Date()); // 起始版本 3.3.0(推荐使用)s
        this.setFieldValByName("createTime", DateUtil.toLocalDateTime(new Date()),metaObject);
        this.setFieldValByName("version",1,metaObject);
        this.setFieldValByName("lastUpdate",DateUtil.toLocalDateTime(new Date()),metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("lastUpdate",DateUtil.toLocalDateTime(new Date()),metaObject);
    }
}
