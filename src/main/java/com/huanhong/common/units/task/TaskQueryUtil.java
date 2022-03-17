package com.huanhong.common.units.task;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.huanhong.common.units.StrUtils;
import com.huanhong.wms.bean.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.FileNotFoundException;
import java.sql.ResultSet;

/**
 * 流程引擎接口
 *
 * @author zhls1
 */
@Slf4j
public class TaskQueryUtil {

    public static final String HOST = "http://wms.aiairy.com:9051/v1";
    /**
     * 当前需要处理的任务
     */
    public static final String MY_TASK = HOST + "/myTask";


    /**
     * 获取当前需要处理的任务
     * @return
     */
    public static Result list() {
        String result;
        try {
            result = HttpUtil.get(MY_TASK, 7000);

        } catch (Exception e) {
            log.error("任务列表请求异常：", e);
            return Result.failure("请求任务列表错误");
        }
        log.info("result:{}", result);
        if (StrUtils.isBlank(result)) {
            log.error("任务列表请求返回结果为空");
            return Result.failure(500, "返回信息空");
        }

        return JSONObject.parseObject(result,Result.class);
    }




    public static void main(String[] args) {
       list();
    }

}
