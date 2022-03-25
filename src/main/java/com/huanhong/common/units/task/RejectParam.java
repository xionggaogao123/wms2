package com.huanhong.common.units.task;

import lombok.Data;

import java.util.Map;

@Data
public class RejectParam {
    private String taskId;
    private String message;
    private String username;
    private Boolean isFirst;
    private Map<String, Object> variables;


}
