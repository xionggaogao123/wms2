package com.huanhong.wms.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.wms.bean.ErrorCode;
import com.huanhong.wms.bean.LoginUser;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.dto.AddMessageDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.entity.Message;
import com.huanhong.wms.mapper.MessageMapper;
import com.huanhong.wms.service.IMessageService;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Map;

@ApiSort()
@Api(tags = "消息表")
@RestController
@RequestMapping("/message")
public class MessageController extends BaseController {

    @Resource
    private IMessageService messageService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页码"),
            @ApiImplicitParam(name = "size", value = "每页行数"),
            @ApiImplicitParam(name = "search", value = "聚合搜索（标题）"),
    })
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "分页查询消息表", notes = "生成代码")
    @GetMapping("/page")
    public Result<Page<Message>> page(@RequestParam(defaultValue = "1") Integer current, @RequestParam(defaultValue = "10") Integer size,
                                      @RequestParam Map<String, Object> search) {
        QueryWrapper<Message> query = new QueryWrapper<>();
        query.orderByDesc("id");
        if (search.containsKey("search")) {
            String text = search.get("search").toString();
            if (StrUtil.isNotEmpty(text)) {
                query.and(qw -> qw.like("title", text).or()
                        .like("user_name", text)
                );
            }
        }
        return Result.success(messageService.page(new Page<>(current, size), query));
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "添加消息表", notes = "生成代码")
    @PostMapping
    public Result<Integer> add(@Valid @RequestBody AddMessageDTO dto) {
        LoginUser loginUser = this.getLoginUser();
        dto.setHandleUserId(loginUser.getId());
        dto.setHandleUserName(loginUser.getUserName());
        return messageService.addMessage(dto);
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "更新消息为已读")
    @PostMapping("/read")
    public Result<Integer> updateStatus(@Valid @RequestBody Integer id) {
        LoginUser loginUser = this.getLoginUser();
        Message message = messageService.getById(id);
        if (!loginUser.getId().equals(message.getUserId())){
            return Result.failure("非该用户消息");
        }
        if (message.getStatus() == 0){
            Message up = new Message();
            up.setId(id);
            up.setStatus(1);
            boolean update = messageService.updateById(up);
            if (update){
                return Result.success();
            }
            return Result.failure(ErrorCode.SYSTEM_ERROR,"系统异常，请稍后重试");
        }
        return Result.failure("信息已读");
    }



    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "删除消息表", notes = "生成代码")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        return render(messageService.removeById(id));
    }


}

