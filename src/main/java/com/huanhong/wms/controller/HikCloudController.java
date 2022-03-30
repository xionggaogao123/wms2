package com.huanhong.wms.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.service.HikCloudService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "海康云眸")
@RestController
@RequestMapping("/v1/hik-cloud")
public class HikCloudController {
    @Autowired
    private HikCloudService hikCloudService;

    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "取流认证")
    @GetMapping("/getEzvizToken")
    public Result<Object> getEzvizToken() {
        return hikCloudService.getEzvizToken();
    }
}
