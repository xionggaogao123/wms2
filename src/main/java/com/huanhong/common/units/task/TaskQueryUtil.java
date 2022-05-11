package com.huanhong.common.units.task;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.huanhong.common.units.StrUtils;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.param.StartProcessParam;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 流程引擎接口
 *
 * @author zhls1
 */
@Slf4j
public class TaskQueryUtil {

//    public static final String HOST = "http://wms.aiairy.com:9051/v1";
    public static final String HOST = "http://localhost:8866/v1";
    /**
     * 当前需要处理的任务
     */
    public static final String MY_TASK = HOST + "/myTask";
    /**
     * 获取当前流程剩余任务数
     */
    public static final String COUNT_TASK = HOST + "/countTask?processInstanceId={}";
    /**
     * 删除任务
     */
    public static final String DELETE_TASK = HOST + "/deleteTask";

    /**
     * 批量删除任务
     */
    public static final String DELETE_TASKS = HOST + "/deleteTasks";

    /**
     * 删除流程
     */
    public static final String DELETE_PROCESS_INSTANCE = HOST + "/deleteProcessInstance";

    /**
     * 驳回
     */
    public static final String REJECT = HOST + "/reject";

    /**
     * 审批通过，在审批的过程中可以随意添加审批信息
     */
    public static final String TASK_COMPLETE = HOST + "/taskComplete";
    /**
     * 启动流程
     */
    public static final String START = HOST + "/start";


    /**
     * 获取当前需要处理的任务
     *
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

        return JSONObject.parseObject(result, Result.class);
    }
    /**
     * 获取当前流程剩余任务数
     *
     * @return
     */
    public static Result countTask(String processInstanceId) {
        String result;
        try {
            result = HttpUtil.get(StrUtil.format(COUNT_TASK,processInstanceId), 7000);

        } catch (Exception e) {
            log.error("获取当前流程剩余任务数请求异常：", e);
            return Result.failure("获取当前流程剩余任务数错误");
        }
        log.info("countTask-result:{}", result);
        if (StrUtils.isBlank(result)) {
            log.error("获取当前流程剩余任务数请求返回结果为空");
            return Result.failure(500, "返回信息空");
        }

        return JSONObject.parseObject(result, Result.class);
    }

    /**
     * 删除任务
     *
     * @return
     */
    public static Result deleteTask(String taskId) {
        String result;
        Map<String, Object> map = new HashMap<>();
        map.put("taskId", taskId);
        try {
            result = HttpUtil.post(DELETE_TASK, map, 7000);

        } catch (Exception e) {
            log.error("删除任务请求异常：", e);
            return Result.failure("删除任务错误");
        }
        log.info("result:{}", result);
        if (StrUtils.isBlank(result)) {
            log.error("删除任务请求返回结果为空");
            return Result.failure(500, "返回信息空");
        }

        return JSONObject.parseObject(result, Result.class);
    }


    /**
     * 删除任务
     *
     * @return
     */
    public static Result deleteTasks(List<String> ids) {
        String result;
        try {
            result = HttpUtil.post(DELETE_TASKS,JSONObject.toJSONString(ids),7000);

        } catch (Exception e) {
            log.error("批量删除任务请求异常：", e);
            return Result.failure("批量删除任务错误");
        }
        log.info("result:{}", result);
        if (StrUtils.isBlank(result)) {
            log.error("批量删除任务请求返回结果为空");
            return Result.failure(500, "返回信息空");
        }

        return JSONObject.parseObject(result, Result.class);
    }

    /**
     * 删除流程
     *
     * @return
     */
    public static Result deleteProcess(String pid, String reason) {
        String result;
        JSONObject json = new JSONObject();
        json.put("processInstanceId", pid);
        json.put("deleteReason", reason == null ? "" : reason);
        try {
            result = HttpUtil.post(DELETE_PROCESS_INSTANCE, json.toJSONString(), 7000);

        } catch (Exception e) {
            log.error("删除流程请求异常：", e);
            return Result.failure("删除流程错误");
        }
        log.info("result:{}", result);
        if (StrUtils.isBlank(result)) {
            log.error("删除流程请求返回结果为空");
            return Result.failure(500, "返回信息空");
        }

        return JSONObject.parseObject(result, Result.class);
    }

    /**
     * 驳回
     *
     * @return
     */
    public static Result reject(RejectParam param) {
        String result;
        try {
            result = HttpUtil.post(REJECT,JSONObject.toJSONString(param),7000);

        } catch (Exception e) {
            log.error("驳回请求异常：", e);
            return Result.failure("驳回任务错误");
        }
        log.info("result:{}", result);
        if (StrUtils.isBlank(result)) {
            log.error("驳回请求返回结果为空");
            return Result.failure(500, "返回信息空");
        }

        return JSONObject.parseObject(result, Result.class);
    }

    /**
     * 审批通过
     *
     * @return
     */
    public static Result complete(TaskCompleteParam param) {
        String result;
        try {
            result = HttpUtil.post(TASK_COMPLETE,JSONObject.toJSONString(param),7000);

        } catch (Exception e) {
            log.error("审批通过请求异常：", e);
            return Result.failure("审批通过任务错误");
        }
        log.info("result:{}", result);
        if (StrUtils.isBlank(result)) {
            log.error("审批通过请求返回结果为空");
            return Result.failure(500, "返回信息空");
        }

        return JSONObject.parseObject(result, Result.class);
    }
    /**
     * 启动流程
     *
     * @return
     */
    public static Result start(StartProcessParam param) {
        String result;
        try {
            result = HttpUtil.post(START,JSONObject.toJSONString(param),7000);

        } catch (Exception e) {
            log.error("启动流程请求异常：", e);
            return Result.failure("启动流程错误");
        }
        log.info("result:{}", result);
        if (StrUtils.isBlank(result)) {
            log.error("启动流程请求返回结果为空");
            return Result.failure(500, "返回信息空");
        }

        return JSONObject.parseObject(result, Result.class);
    }


    public static void main(String[] args) {
        //list();
        deleteProcess("b0d62319-a034-11ec-8ca2-00163e302f7f","ceshi");
    }

}
