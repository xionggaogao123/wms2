package com.huanhong.common.units.task;

import lombok.Data;

import java.util.Map;

@Data
public class TaskCompleteParam {
    private String taskId;
    private String message;
    private String username;
    private Map<String, Object> variables;
    private String name;


}
