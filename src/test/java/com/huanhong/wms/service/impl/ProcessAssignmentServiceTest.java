package com.huanhong.wms.service.impl;

import com.huanhong.wms.service.IDeptService;
import com.huanhong.wms.service.IProcessAssignmentService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author ldy81
 * @date 2020/4/16 11:47
 */
@Slf4j
@SpringBootTest
@DisplayName("待办事项测试类")
public class ProcessAssignmentServiceTest {

    @Resource
    private IProcessAssignmentService processAssignmentService;

    @Test
    @DisplayName("同步数据")
    void test() {
        processAssignmentService.syncProcessAssignment();
    }

}
