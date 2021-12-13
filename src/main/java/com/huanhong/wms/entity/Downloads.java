package com.huanhong.wms.entity;

import com.huanhong.wms.SuperEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 下载中心
 *
 * @author liudeyi.cn
 * @since 2019-10-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Downloads extends SuperEntity {

    /**
     * 下载编号
     */
    private String no;
    /**
     * 下载类型
     */
    private String type;
    /**
     * 下载任务名称
     */
    private String taskName;
    /**
     * 导出条件
     */
    private String queryCondition;
    /**
     * 任务状态 1.准备 2.进行中 3.已完成 4.取消
     */
    private Integer state;
    /**
     * 本地地址
     */
    private String localPath;
    /**
     * 下载路径
     */
    private String url;
    /**
     * 文件名称
     */
    private String fileName;
    /**
     * 文件格式
     */
    private String format;
    /**
     * 文件大小单位b
     */
    private Long size;
    /**
     * 文件MD5值
     */
    private String md5;
    /**
     * 创建人ID
     */
    private Integer userId;

}