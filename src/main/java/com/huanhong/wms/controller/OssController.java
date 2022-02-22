package com.huanhong.wms.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.common.units.OssUtil;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.bean.ErrorCode;
import com.huanhong.wms.bean.LoginUser;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.Oss;
import com.huanhong.wms.entity.vo.UploadOssVo;
import com.huanhong.wms.mapper.OssMapper;
import com.huanhong.wms.mapper.UserMapper;
import com.huanhong.wms.properties.OssProperties;
import com.obs.services.model.PutObjectResult;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 对象存储表 前端控制器
 * </p>
 *
 * @author liudeyi
 * @since 2019-12-14
 */
@Slf4j
@RestController
@RequestMapping("/v1/oss")
@ApiSort(4)
@Api(tags = "资源存储 💾")
public class OssController extends BaseController {

    @Resource
    private OssProperties ossProperties;

    @Resource
    private OssMapper ossMapper;

    @Resource
    private UserMapper userMapper;

    public static final Logger LOGGER = LoggerFactory.getLogger(MaterialController.class);

    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "目录", required = true, paramType = "form"),
            @ApiImplicitParam(name = "file", value = "资源对象", required = true, paramType = "file"),
            @ApiImplicitParam(name = "objectId", value = "对象ID", paramType = "form"),
            @ApiImplicitParam(name = "sort", value = "排序", paramType = "form"),
            @ApiImplicitParam(name = "objectType",value="对象表名",paramType = "form")
    })
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "上传文件", notes = "请用form表单上传 type in（avatar、face、feedback、repair）")
    @PostMapping("/upload")
    public Result<UploadOssVo> upload(@RequestParam String objectType,
                                      @RequestParam MultipartFile file,
                                      @RequestParam(required = false) Integer objectId,
                                      @RequestParam(required = false, defaultValue = "1") Integer sort) {
        LoginUser loginUser = this.getLoginUser();
//        if (!file.getContentType().contains("image")) {
//            return Result.failure(ErrorCode.PARAM_FORMAT_ERROR, "请上传正确的图片");
//        }
        String md5,
        fileName = file.getOriginalFilename();
        String filePath = ossProperties.getPath() + objectType + "/";
        String fullPath = filePath + fileName;
        long fileSize = file.getSize();

        int fileMaxSize = 102400;
        // 上传文件流
        try {
            FileUtil.mkdir(filePath);
            // 人脸压缩
            if(file.getContentType().contains("image")){
                if ("face".equals(objectType)) {
                    if (fileSize > fileMaxSize) {
                        // 循环压缩
                        commpressPicCycle(filePath, file.getInputStream(), fileMaxSize, 0.9);
                    } else {
                        file.transferTo(Paths.get(fullPath));
                    }
                } else {
                    Thumbnails.of(file.getInputStream())
                            .scale(1f)
                            .outputQuality(0.9f)
                            .outputFormat("jpg")
                            .toFile(fullPath);
                }
            }else{
                file.transferTo(Paths.get(fullPath));
            }
            FileReader reader = new FileReader(fullPath);
            fileSize = reader.getFile().length();
            // 设置上传MD5校验
            md5 = SecureUtil.md5(reader.getInputStream());
            PutObjectResult putResult = OssUtil.putObject(reader.getInputStream(), objectType + "/" + fileName);
            log.info("oss上传报文 ==> {}", JSON.toJSONString(putResult.getEtag()));
            // 关闭释放
            file.getInputStream().close();
            reader.getInputStream().close();
        } catch (Exception e) {
            log.error("文件上传失败", e);
            return Result.failure(500, "上传失败,请稍后再试");
        }
        Oss oss = new Oss();
        oss.setMd5(md5);
        oss.setName(fileName);
        oss.setType(file.getContentType());
        oss.setSize(fileSize);
        if (objectId != null) {
            oss.setObjectId(objectId);
        } else {
            oss.setObjectId(loginUser.getId());
        }
        oss.setObjectType(objectType);
        oss.setUrl(objectType + "/" + fileName);
        oss.setUserId(loginUser.getId());
        oss.setSort(sort);
        oss.setState(2);
        ossMapper.insert(oss);
        UploadOssVo data = new UploadOssVo();
        data.setId(oss.getId());
        data.setFileName(oss.getUrl());
        data.setFileUrl(ossProperties.getYunUrl() +"/" +oss.getUrl());
        data.setObjectId(objectId);
        return Result.success(data);
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "获取我的文件", notes = "只显示最近10条")
    @GetMapping("/my")
    public Result<List<Map<String, Object>>> getMyList(@ApiParam(name = "objectType", value = "目录") String objectType,
                                                       @ApiParam(name = "objectId", value = "资源所属对象id") Integer objectId) {
        QueryWrapper<Oss> query = new QueryWrapper<>();
        query.select("id,size,type,oss_host(url) url,state,create_time")
                .eq("object_id",objectId)
                .orderByDesc("id").last("limit 10");
        if (StrUtil.isNotEmpty(objectType)) {
            query.eq("object_type", objectType);
        }
        return Result.success(ossMapper.selectMaps(query));
    }

    /**
     * 删除
     */
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "删除附件", notes = "根据附件ID删除附件")
    @DeleteMapping("/delete/{Id}")
    public Result delete(@PathVariable Integer Id){
        int i = ossMapper.deleteById(Id);
        if (i>0){
            LOGGER.info("附件: " + Id + " 删除成功");
        }else {
            return Result.failure(ErrorCode.SYSTEM_ERROR,"删除失败,文件异常或不存在");
        }
        return render(i > 0);
    }

    /**
     * 压缩
     *
     * @param desPath
     * @param inputStream
     * @param desFileSize
     * @param accuracy
     * @throws IOException
     */
    private void commpressPicCycle(String desPath, InputStream inputStream, long desFileSize,
                                   double accuracy) throws IOException {
        long srcFileSize = inputStream.available();
        // 判断大小，如果小于desFileSize，不压缩；如果大于等于desFileSize，压缩
        if (srcFileSize <= desFileSize) {
            return;
        }
        File file = Paths.get(desPath).toFile();
        Thumbnails.of(inputStream).scale(0.8f).outputFormat("jpg").outputQuality(accuracy).toFile(file);
        inputStream = FileUtil.getInputStream(file);
        commpressPicCycle(desPath, inputStream, desFileSize, accuracy);
    }
}
