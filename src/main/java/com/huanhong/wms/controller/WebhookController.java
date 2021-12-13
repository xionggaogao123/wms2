package com.huanhong.wms.controller;

import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.wms.BaseController;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 事件回调控制器
 *
 * @author ThorOde
 * @since 2020/3/5 6:20 下午
 */
@Slf4j
@RestController
@RequestMapping("/webhook")
@Api(tags = "Webhook 🪃")
@ApiSort(98)
public class WebhookController extends BaseController {



}
