package com.huanhong.wms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "judge")
@PropertySource(value = "classpath:judge.properties")
@Data
public class JudgeConfig {
    //物料非空字段表
    private List<String> MeterialNotNullList = new ArrayList<>();
    //库存非空字段表
    private List<String> InventoryNotNullList = new ArrayList<>();
    //仓库非空字段表
    private List<String> WarehouseNotNullList = new ArrayList<>();
    //子库非空字段表
    private List<String> SublibraryNotNullList = new ArrayList<>();
    //库区非空字段表
    private List<String> WarehouseAreaNotNullList = new ArrayList<>();
    //货架非空字段表
    private List<String> ShelfNotNullList = new ArrayList<>();
    //货位非空字段表
    private List<String> CargoSpaceNullList = new ArrayList<>();
    //物料分类非空字段
    private List<String> MeterialClassificationNullList = new ArrayList<>();
}
