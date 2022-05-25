package com.huanhong.wms.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.common.units.JsonUtil;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.TemporaryLibraryInventory;
import com.huanhong.wms.entity.dto.AddTemporaryLibraryInventoryAndDetailsDTO;
import com.huanhong.wms.entity.dto.AddTemporaryLibraryInventoryDTO;
import com.huanhong.wms.entity.dto.AddTemporaryLibraryInventoryDetailsDTO;
import com.huanhong.wms.service.ITemporaryLibraryInventoryDetailsService;
import com.huanhong.wms.service.ITemporaryLibraryInventoryService;
import com.huanhong.wms.service.TemporaryLibraryInventoryV1Service;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @Author wang
 * @date 2022/5/25 9:39
 */
@ApiSort()
@Api(tags = "临库清点单V1")
@Slf4j
@Validated
@RestController
@RequestMapping("/v1/temporary-library-inventory-v1")
public class TemporaryLibraryInventoryV1Controller extends BaseController {

    @Resource
    private TemporaryLibraryInventoryV1Service temporaryLibraryInventoryV1Service;


    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "添加", notes = "生成代码")
    @PostMapping("/add")
    public Result add(@Valid @RequestBody AddTemporaryLibraryInventoryAndDetailsDTO addTemporaryLibraryInventoryAndDetailsDTO) {
        log.info("新增临时清点的数据为:{}", JsonUtil.obj2String(addTemporaryLibraryInventoryAndDetailsDTO));
        return temporaryLibraryInventoryV1Service.addTemporaryMainAndSublistAndWarehouse(addTemporaryLibraryInventoryAndDetailsDTO);
    }
}
