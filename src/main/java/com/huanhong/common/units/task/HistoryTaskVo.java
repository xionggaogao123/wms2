package com.huanhong.common.units.task;

import lombok.Data;

import java.util.Date;

@Data
public class HistoryTaskVo{
	private String processDefinitionId;
	private String processInstanceId;
	private int sequenceCounter;
	private String persistentState;
	private String activityInstanceId;
	private int priority;
	private String processDefinitionKey;
	private String executionId;
	private String taskDefinitionKey;
	private String name;
	private String rootProcessInstanceId;
	private Date startTime;
	private String assignee;
	private String id;
	private String deleteReason;
}