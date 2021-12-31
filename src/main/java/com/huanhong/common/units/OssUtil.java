package com.huanhong.common.units;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.PutObjectResult;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * 阿里oss工具类
 *
 * @author thorode
 */
@Slf4j
public class OssUtil {

    public static final String OSS_ENDPOINT = "https://oss-cn-shanghai.aliyuncs.com";
    public static final String OSS_ACCESSKEY_ID = "LTAIPg51Li0kuPD9";
    public static final String OSS_ACCESSKEY_SECRET = "M455Xcu0uRXdF8jORvTwwm072Lm2In";
    public static final String OSS_BUCKETNAME = "wmstest1";

    public static OSS getOSS() {
        return new OSSClientBuilder().build(OSS_ENDPOINT, OSS_ACCESSKEY_ID, OSS_ACCESSKEY_SECRET);
    }

    /**
     * 上传url资源
     *
     * @param url      资源地址
     * @param basePath 上传文件夹
     * @return String
     * @since 2018-04-26
     */
    public static String putObject2Url(String url, String basePath) {
        // 上传
        InputStream stream = null;
        try {
            stream = new URL(url).openStream();
        } catch (IOException e) {
            log.error("上传文件异常：", e);
        }
        //拼接本地保存路径
        String fileName = IdWorker.getIdStr() + StrUtils.getSuffix(url, ".", "?");
        basePath += fileName;
        PutObjectResult result = putObject(stream, fileName);
        return basePath;
    }

    /**
     * 上传InputStream资源
     *
     * @param file     输入流
     * @param fileName 文件名
     * @return PutObjectResult
     */
    public static PutObjectResult putObject(InputStream file, String fileName) {
        // 创建OSSClient实例
        OSS oss = getOSS();
        PutObjectResult result = oss.putObject(OSS_BUCKETNAME, fileName, file);
        // 关闭client
        oss.shutdown();
        return result;
    }

    public static PutObjectResult putObject(File file, String fileName) {
        // 创建OSSClient实例
        OSS oss = getOSS();
        PutObjectResult result = oss.putObject(OSS_BUCKETNAME, fileName, file);
        // 关闭client
        oss.shutdown();
        return result;
    }

    public static String calculateBase64MD5(byte[] bytes) {
        return BinaryUtil.toBase64String(BinaryUtil.calculateMd5(bytes));
    }

}
