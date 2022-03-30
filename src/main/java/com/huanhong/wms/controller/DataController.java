package com.huanhong.wms.controller;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.common.units.TokenUtil;
import com.huanhong.common.units.sms.AliSmsTool;
import com.huanhong.common.units.sms.SMSResult;
import com.huanhong.common.units.weixin.WeiXinUtil;
import com.huanhong.common.units.weixin.WeixinConstant;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.bean.LoginUser;
import com.huanhong.wms.bean.RedisKey;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.User;
import com.huanhong.wms.entity.dto.LoginDTO;
import com.huanhong.wms.properties.OssProperties;
import com.huanhong.wms.service.IUserService;
import com.huanhong.wms.service.IWarehouseManagementService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Slf4j
@RestController
@RequestMapping("/v1/data")
@ApiSort(1)
@Api(tags = "数据分析 📈")
public class DataController extends BaseController {


    @Resource
    private IWarehouseManagementService warehouseManagementService;



    @ApiOperation(value = "小程序首页")
    @GetMapping("/home")
    public Result<Object> miniProgramLogout() {
        LoginUser loginUser = this.getLoginUser();
        return warehouseManagementService.selectWarehouseInfo(loginUser.getId());
    }

}
