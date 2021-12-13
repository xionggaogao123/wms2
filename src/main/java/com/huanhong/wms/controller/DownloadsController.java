package com.huanhong.wms.controller;

import cn.hutool.core.collection.IterUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.id.NanoId;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.bean.ErrorCode;
import com.huanhong.wms.bean.LoginUser;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.Downloads;
import com.huanhong.wms.entity.User;
import com.huanhong.wms.mapper.DownloadsMapper;
import com.huanhong.wms.mapper.UserMapper;
import com.huanhong.wms.properties.OssProperties;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.File;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 下载中心控制器
 *
 * @author ldy81
 * @date 2019/10/17 15:54
 */
@Slf4j
@RestController
@RequestMapping("/downloads")
@ApiSort(93)
@Api(tags = "下载中心 ⬇️")
public class DownloadsController extends BaseController {

    @Resource
    private OssProperties ossProperties;

    @Resource
    private DownloadsMapper downloadsMapper;
    @Resource
    private UserMapper userMapper;


    @ApiOperation(value = "分页查询下载中心列表")
    @GetMapping("/page")
    public Result<Page<Downloads>> page(@RequestParam(defaultValue = "1") Integer current,
                                        @RequestParam(defaultValue = "10") Integer size) {
        LoginUser loginUser = this.getLoginUser();
        QueryWrapper<Downloads> ew = new QueryWrapper<>();
        ew.eq("user_id", loginUser.getId())
                .orderByDesc("id");
        return Result.success(downloadsMapper.selectPage(new Page<>(current, size), ew));
    }

    @ApiOperation(value = "删除文件")
    @DeleteMapping("/{id}")
    public Result del(@PathVariable Integer id) {
        downloadsMapper.deleteById(id);
        return Result.success();
    }

    @ApiOperation(value = "导出数据列表")
    @GetMapping("/report")
    public Result reportDataList(@RequestParam Map<String, Object> search) {
        LoginUser loginUser = this.getLoginUser();
        Map<String, String> alias = new LinkedHashMap<>();
        Iterable<?> rows;
        Object table = search.get("table");
        String name;
        if (table.equals("USER")) {
            rows = selectUserList(search, alias);
            name = "员工列表";
        } else {
            return Result.failure(ErrorCode.PARAM_ERROR, "未选择需要导出的表");
        }
        if (IterUtil.isEmpty(rows)) {
            return Result.failure(ErrorCode.DATA_IS_NULL, "没有可导出的数据");
        }
        Downloads downloads = new Downloads();
        downloads.setNo(NanoId.randomNanoId());
        downloads.setFileName(SecureUtil.md5(search.toString() + LocalDateTime.now()) + ".xlsx");
        downloads.setLocalPath(ossProperties.getPath() + "download/" + downloads.getFileName());
        downloads.setUrl(ossProperties.getUrl() + "download/" + downloads.getFileName());
        downloads.setFormat("xlsx");
        downloads.setUserId(loginUser.getId());
        downloads.setState(2);
        downloads.setType(table.toString());
        downloads.setTaskName("导出-" + name + "");
        downloads.setQueryCondition(JSON.toJSONString(search));
        int id = downloadsMapper.insert(downloads);
        if (id == 0) {
            return Result.failure(ErrorCode.SYSTEM_ERROR, "创建导出任务失败");
        }
        ExcelWriter writer = ExcelUtil.getWriter(downloads.getLocalPath());
        // 合并单元格后的标题行，使用默认标题样式
        writer.merge(alias.size() - 1, name);
        writer.setHeaderAlias(alias);
        writer.setOnlyAlias(true);
        // 一次性写出内容，使用默认样式
        writer.write(rows, true);
        // 关闭writer，释放内存
        writer.close();
        // 计算文件大小和md5值
        Downloads update = new Downloads();
        update.setId(downloads.getId());
        try {
            File file = FileUtil.file(downloads.getLocalPath());
            update.setState(3);
            update.setSize(file.length());
            update.setMd5(SecureUtil.md5(file));
        } catch (Exception e) {
            update.setState(5);
            log.error("计算文件大小和md5值错误", e);
        }
        downloadsMapper.updateById(update);
        return Result.success();
    }

    /**
     * 填充用户列表
     *
     * @param loginUser 登录用户
     * @param search    查询条件
     * @param alias     别名
     * @return List<User> 数据
     */
    private List<Map<String, Object>> selectUserList(Map<String, Object> search, Map<String, String> alias) {
        QueryWrapper<User> query = new QueryWrapper<>();
        query.select("*")
                .ne("dept_id", "-1")
                .orderByAsc("user_name");
        if (search.containsKey("search")) {
            String text = search.get("search").toString();
            if (StrUtil.isNotEmpty(text)) {
                query.and(qw -> qw.like("user_name", text).or()
                        .like("login_name", text).or()
                        .like("phone_number", text).or()
                        .like("department", text).or()
                        .like("company_name", text)
                );
            }
        }
        this.eq("sex", search, query);
        List<Map<String, Object>> list = userMapper.selectMaps(query);
        if (list.size() > 0) {
            alias.put("deptName", "部门");
            alias.put("position", "职位");
            alias.put("userName", "姓名");
            alias.put("account", "账号");
            alias.put("mobile", "手机号");
            alias.put("licensePlate", "车牌号");
            alias.put("ldapAccount", "域账户(AD账户)");
            alias.put("jobNumber", "微创员工编码");
            alias.put("oneCardNum", "一卡通号");
            alias.put("email", "邮箱");
            alias.put("state", "状态");
            alias.put("isVerify", "是否验证");
            alias.put("faceStatus", "人脸状态");
            alias.put("verifyFail", "验证失败原因");
            return list;
        }
        return null;
    }

}
