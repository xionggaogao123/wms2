package com.huanhong.common.units.weixin;

public interface TemplateConstant {
    /**
     * 工单创建  1，所有工单创建后，微信小程序提醒 拥有审批流的 管理员 或 成本中心负责人（仅维修备件申请）
     */
    String WORK_ORDER_CREATED = "{}已创建，需要您的确认";
    /**
     * 工单审核并指派给工程师   由管理员审核并流转给工程师后，小程序同时提醒申请人和工程师。
     */
    String WORK_ORDER_REVIEWED = "{}已由工程师受理";
    /**
     * 工单被驳回  3，由审核权限的管理员审核未通过驳回时，小程序提醒申请人
     */
    String WORK_ORDER_REJECTED = "{}被驳回。{}";
    /**
     * 工单已关闭  4，已申请关闭的工单由审核权限的管理员审核通过后，则同时通知申请人和工程师
     */
    String WORK_ORDER_CLOSED = "{}已完成服务，如有任何问题请随时与售后服务部联系。谢谢！";

    /**
     * 工单转移  如有工单转移，则通知 由审核权限的管理员
     */
    String WORK_ORDER_TRANFER = "{}请求服务转移";
    String WORK_ORDER_CLOSING = "{}请求关闭。{}";

//    /**
//     * 维修备件申请创建  1，维修备件创建后，微信小程序提醒 成本中心负责人（仅维修备件申请）
//     */
//    String PARTS_APPLY_CREATED = "{}已创建，需要您的确认";
    /**
     * 维修备件审核并指派给工程师   由管理员审核并流转给工程师后，小程序同时提醒申请人和工程师。
     */
    String PARTS_APPLY_REVIEWED = "{}已由成本中心负责人受理";
//    /**
//     * 维修备件被驳回  3，由审核权限的管理员审核未通过驳回时，小程序提醒申请人
//     */
//    String PARTS_APPLY_REJECTED = "{}被驳回。{}";
}
