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
@RequestMapping("/v1")
@ApiSort(1)
@Api(tags = "公共方法 🏔")
public class MainController extends BaseController {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private IUserService userService;

    @Resource
    private OssProperties ossProperties;


    @ApiOperationSupport(order = 1)
    @ApiOperation("统一登录接口")
    @PostMapping("/login")
    public Result login(HttpServletRequest request, @Valid @RequestBody LoginDTO login) {
        login.setIp(request.getRemoteAddr());
        Result<User> result = userService.checkLogin(login);
        if (!result.isOk()) {
            return result;
        }
        if (StrUtil.isNotBlank(login.getOpenid())) {
            User tempUser = new User();
            tempUser.setId(result.getData().getId());
            tempUser.setWxOpenId(login.getOpenid());
            userService.updateById(tempUser);
        }
        int timeout = 10080;
        if ("miniProgram".equals(login.getTerminal())) {
            timeout = -1;
        }
        // 登录token 有效期3天
        String token = TokenUtil.createJWT(result.getData(), timeout);
        Map<String, Object> dataMap = new HashMap<>(2);
        result.getData().setId(null);
        dataMap.put("user", result.getData());
        dataMap.put("token", token);
        return Result.success(dataMap);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "获取验证码")
    @GetMapping("/code")
    public Result<String> getCode(@RequestParam String tel, @RequestParam Integer type) {
        Validator.validateMobile(tel, "请输入正确的手机号");
        String code = RandomUtil.randomNumbers(4);
        SMSResult result = AliSmsTool.sendCode(type, tel, code);
        log.info("发送短信验证码[{}]: {}", code, result.toString());
        if (result.getCode().equals("OK")) {
            String key = StrUtil.format("code_{}_{}", type, tel);
            redisTemplate.opsForValue().set(key, code);
            redisTemplate.expire(key, 10, TimeUnit.MINUTES);
            return Result.success();
        } else {
            return Result.failure(result.getMessage());
        }
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "获取我的信息")
    @GetMapping("/me")
    public Result getMyInfo(HttpServletResponse response) {
        User user = userService.getById(this.getLoginUserId());
        if(user == null) {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/html");
            response.setStatus(401);
            return null;
        }
        user.setId(null);
        user.setPassword(null);
        return Result.success(user);
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "刷新token")
    @GetMapping("/refreshToken")
    public Result refreshToken(@RequestParam String token) {
        Object tempToken = redisTemplate.opsForValue().get(RedisKey.TEMP + token);
        if (tempToken == null) {
            return renderAgainLogin();
        }
        redisTemplate.delete(RedisKey.TEMP + token);
        return Result.success(TokenUtil.refreshJWT(tempToken.toString(), 4320));
    }

    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "小程序登录")
    @PostMapping("/miniprogram/login")
    public Result miniProgramLogin(@RequestBody LoginDTO login) {
        if (StrUtil.isBlank(login.getJs_code())) {
            return Result.failure(1005, "js_code 为空");
        }
        JSONObject weixinResult = WeiXinUtil.code2Session(WeixinConstant.MINI_APP_ID, WeixinConstant.MINI_APP_SECRET, login.getJs_code());
        if (null == weixinResult) {
            return Result.failure(8004, "code2Session接口调用失败");
        }
        String openid = weixinResult.getString("openid");

        //查询根据openid查找用户
        Result<User> result = userService.selectByOpenid(openid);
        // 不存在
        if (!result.isOk()) {
            return Result.failure(1023, "用户不存在", openid);
        }
        User user = result.getData();
        //登录token
        String token;
        List<String> authority = new ArrayList<>();
        authority.add("user");
        token = TokenUtil.createJWT(user, -1);
        Map<String, Object> dataMap = new HashMap<>(8);
        result.getData().completionPicURL(ossProperties.getUrl());
        result.getData().setId(null);
        result.getData().setPassword(null);
        dataMap.put("user", result.getData());
        dataMap.put("token", token);
        dataMap.put("authority", authority);
        dataMap.put("openid", openid);
        return Result.success(dataMap);
    }

    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "小程序登出")
    @GetMapping("/miniprogram/logout")
    public Result miniProgramLogout() {
        LoginUser loginUser = this.getLoginUser();
        User user = userService.getById(loginUser.getId());
        if (null == user) {
            return Result.failure("无效操作");
        }
        userService.delOpenIdById(user.getId());
        return Result.success(user.getWxOpenId());
    }
}
