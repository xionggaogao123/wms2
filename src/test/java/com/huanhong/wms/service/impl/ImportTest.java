package com.huanhong.wms.service.impl;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.huanhong.wms.service.HikCloudService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.List;
import java.util.Map;

@Slf4j
@SpringBootTest
@DisplayName("导入测试类")
public class ImportTest {

    @DisplayName("导入测试")
    @Test
    void importTest() {
        ImportParams params = new ImportParams();
        List<Map<String, Object>> list = ExcelImportUtil.importExcel(
                new File("C:\\Users\\92080\\Pictures\\Documents\\WeChat Files\\zhls1992\\FileStorage\\File\\2022-05\\泰丰盛合物资编码汇总20220524(1).xlsx"), Map.class, params);
        System.out.println(list);
    }
}
