package com.huanhong.common.units;


import com.aliyun.oss.common.utils.BinaryUtil;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.obs.services.ObsClient;
import com.obs.services.model.PutObjectResult;
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

    public static final String OSS_ENDPOINT = "obs.cn-southwest-2.myhuaweicloud.com";
    public static final String OSS_ACCESSKEY_ID = "ZT6H5XDSD3RFCTYEHFIK";
    public static final String OSS_ACCESSKEY_SECRET = "WEGGqxqGQG4hRgdCFij7g95qJnHOJNWZII8ubYWK";
    public static final String OSS_BUCKETNAME = "wmsobs";

//    public static OSS getOSS() {
//        return new OSSClientBuilder().build(OSS_ENDPOINT, OSS_ACCESSKEY_ID, OSS_ACCESSKEY_SECRET);
//    }
    public static ObsClient getOBS(){
        return  new ObsClient(OSS_ACCESSKEY_ID,OSS_ACCESSKEY_SECRET,OSS_ENDPOINT);
    }

    /**
     * 上传url资源
     *
     * @param url      资源地址
     * @param basePath 上传文件夹
     * @return String
     * @since 2018-04-26
     */
    public static String putObject2Url(String url, String basePath) throws IOException {
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
    public static PutObjectResult putObject(InputStream file, String fileName) throws IOException {
        // 创建OSSClient实例
        // 创建ObsClient实例
        ObsClient obsClient = getOBS();

        PutObjectResult result = obsClient.putObject(OSS_BUCKETNAME, fileName, file);

        // 关闭client
        obsClient.close();
        return result;
    }

    public static PutObjectResult putObject(File file, String fileName) throws IOException {
        // 创建OSSClient实例
        ObsClient obsClient = getOBS();
        PutObjectResult result = obsClient.putObject(OSS_BUCKETNAME, fileName, file);
        // 关闭client
        obsClient.close();
        return result;
    }

    public static String calculateBase64MD5(byte[] bytes) {
        return BinaryUtil.toBase64String(BinaryUtil.calculateMd5(bytes));
    }

}
