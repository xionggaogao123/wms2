package com.huanhong.wms.controller;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.RandomUtil;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.common.units.TokenUtil;
import com.huanhong.wms.BaseController;
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
import java.util.HashMap;
import java.util.Map;


@Slf4j
@RestController
@ApiSort(1)
@Api(tags = "公共方法 🏔")
public class MainController extends BaseController {

    @Resource
    private OssProperties ossProperties;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private IUserService userService;


    @ApiOperationSupport(order = 1)
    @ApiOperation("统一登录接口")
    @PostMapping("/login")
    public Result login(HttpServletRequest request, @Valid @RequestBody LoginDTO login) {
        login.setIp(request.getRemoteAddr());
        Result<User> result = userService.checkLogin(login);
        if (!result.isOk()) {
            return result;
        }
        // 登录token 有效期3天
        String token = TokenUtil.createJWT(result.getData(), 10080);
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
//        SMSResult result = AliSmsTool.sendCode(type, tel, code);
//        log.info("发送短信验证码[{}]: {}", code, result.toString());
//        if (result.getCode().equals("OK")) {
//            String key = StrUtil.format("code_{}_{}", type, tel);
//            redisTemplate.opsForValue().set(key, code);
//            redisTemplate.expire(key, 10, TimeUnit.MINUTES);
//            return Result.success();
//        } else {
//            return Result.failure(result.getMessage());
//        }
        return Result.success(code);
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

}
