package com.huanhong.wms.service.impl;

import com.huanhong.wms.service.HikCloudService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
@DisplayName("云眸测试类")
public class HikCloudServiceImplTest {

    @Autowired
    private HikCloudService hikCloudService;

    @DisplayName("取流认证")
    @Test
    void getEzvizToken() {
        log.info("取流认证:{}",hikCloudService.getEzvizToken());
    }
}
