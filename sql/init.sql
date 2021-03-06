CREATE TABLE `temporary_enter_warehouse`
(
    `id`                  bigint(20) NOT NULL AUTO_INCREMENT,
    `document_number`     varchar(255) NOT NULL COMMENT '清点单据编号',
    `enter_number`        varchar(255) NOT NULL COMMENT '临时库入库单据编号',
    `process_instance_id` varchar(255)          DEFAULT NULL COMMENT '流程Id',
    `state`               int          NOT NULL DEFAULT '1' COMMENT '状态:1.草拟\r\n2.审批中\r\n3.审批生效\r\n4.作废\n5.驳回',
    `warehouse_id`        varchar(255) NOT NULL COMMENT '仓库编号',
    `warehouse`           varchar(255) NOT NULL COMMENT '仓库管理员',
    `enter_time`          timestamp    NOT NULL COMMENT '入库时间',
    `remark`              varchar(255)          DEFAULT NULL COMMENT '备注',
    `version`             int                   DEFAULT NULL COMMENT '版本-乐观锁',
    `create_time`         timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `reject_reason`       varchar(255)          DEFAULT '' COMMENT '驳回原因',
    `last_update`         timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    `del`                 tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否删除',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='临时入库主表';

CREATE TABLE `temporary_enter_warehouse_details`
(
    `id`                  bigint(20) NOT NULL AUTO_INCREMENT,
    `enter_number`        varchar(255) NOT NULL COMMENT '临时库入库单据编号',
    `material_coding`     varchar(255) NOT NULL COMMENT '物料编码',
    `receivable_quantity` double       NOT NULL COMMENT '应到数量',
    `arrival_quantity`    double                DEFAULT NULL COMMENT '到货数量',
    `batch`               varchar(255) NOT NULL COMMENT '批次',
    `material_name`       varchar(255)          DEFAULT NULL COMMENT '物料名称',
    `warehouse_id`        varchar(255)          DEFAULT NULL COMMENT '仓库',
    `complete`            int(11) NOT NULL DEFAULT '0' COMMENT '是否完成清点（0-未清点 1-已清点）',
    `remark`              varchar(255)          DEFAULT NULL COMMENT '备注',
    `version`             int(11) DEFAULT NULL COMMENT '版本-乐观锁',
    `effective_time`      timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '有效日期',
    `create_time`         timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
    `last_update`         timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    `del`                 tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '是否删除',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='临时入库子表';

CREATE TABLE `temporary_library_inventory_details`
(
    `id`                  bigint(20) NOT NULL AUTO_INCREMENT,
    `document_number`     varchar(255) NOT NULL COMMENT '清点单编号',
    `material_coding`     varchar(255) NOT NULL COMMENT '物料编码',
    `receivable_quantity` double       NOT NULL COMMENT '应到数量',
    `arrival_quantity`    double                DEFAULT NULL COMMENT '到货数量',
    `batch`               varchar(255) NOT NULL COMMENT '批次',
    `warehouse_id`        varchar(255)          DEFAULT NULL COMMENT '仓库',
    `complete`            int          NOT NULL DEFAULT '0' COMMENT '是否完成清点（0-未清点 1-已清点）',
    `remark`              varchar(255)          DEFAULT NULL COMMENT '备注',
    `material_name`       varchar(255)          DEFAULT NULL COMMENT '物料名称',
    `version`             int                   DEFAULT NULL COMMENT '版本-乐观锁',
    `create_time`         timestamp    NOT NULL COMMENT '创建时间',
    `effective_time`      timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '有效日期',
    `last_update`         timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    `del`                 tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否删除',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='临时清点子表';

CREATE TABLE `temporary_out_warehouse`
(
    `id`                  bigint(20) NOT NULL AUTO_INCREMENT,
    `out_number`          varchar(255)                                                  DEFAULT NULL COMMENT '出库单据编号',
    `process_instance_id` varchar(255)                                                  DEFAULT NULL COMMENT '流程Id',
    `status`              int       NOT NULL                                            DEFAULT '1' COMMENT '审批状态:1.草拟\r\n2.审批中\r\n3.审批生效\r\n4.作废\n5.驳回',
    `requisitioning_unit` varchar(255)                                                  DEFAULT NULL COMMENT '领用单位',
    `recipient`           varchar(255)                                                  DEFAULT NULL COMMENT '领用人',
    `warehouse_id`        varchar(255)                                                  DEFAULT NULL COMMENT '库房ID',
    `librarian`           varchar(255)                                                  DEFAULT NULL COMMENT '库管员',
    `version`             int                                                           DEFAULT NULL COMMENT '版本-乐观锁',
    `remark`              varchar(255)                                                  DEFAULT NULL COMMENT '备注',
    `create_time`         timestamp NOT NULL COMMENT '出库日期',
    `last_update`         timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    `del`                 tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否删除',
    `reject_reason`       varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '驳回原因',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='临时出库主表';


CREATE TABLE `temporary_out_warehouse_details`
(
    `id`                   bigint(20) NOT NULL AUTO_INCREMENT,
    `out_number`           varchar(255) NOT NULL COMMENT '出库单编号',
    `material_coding`      varchar(255) NOT NULL COMMENT '物料编码',
    `material_name`        varchar(255) DEFAULT NULL COMMENT '物料名称',
    `batch`                varchar(255) NOT NULL COMMENT '批次',
    `requisition_quantity` double       NOT NULL COMMENT '领用数量',
    `warehouse_id`         varchar(255) DEFAULT NULL COMMENT '仓库',
    `remark`               varchar(255) DEFAULT NULL COMMENT '备注',
    `version`              int          DEFAULT NULL COMMENT '版本-乐观锁',
    `create_time`          timestamp    NOT NULL COMMENT '出库日期',
    `last_update`          timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    `del`                  tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否删除',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='临时出库子表';


CREATE TABLE `temporary_record`
(
    `id`                           bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `number`                       varchar(255) NOT NULL COMMENT '临时入库单据编号,临时出库单据编号',
    `requirements_planning_number` varchar(255) DEFAULT NULL COMMENT '需求计划单据编号',
    `batch`                        varchar(255) NOT NULL COMMENT '批次',
    `warehouse_manager`            varchar(255) DEFAULT '' COMMENT '库管员',
    `record_type`                  varchar(32)  NOT NULL COMMENT '记录类型：1-临时库入库 2-临时库出库',
    `enter_time`                   timestamp NULL DEFAULT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '入库时间',
    `out_time`                     timestamp NULL DEFAULT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '出库时间',
    `create_time`                  timestamp    NOT NULL COMMENT '创建时间',
    `last_update`                  timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    `del`                          tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否删除',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='出入库清单主表';

CREATE TABLE `temporary_record_details`
(
    `id`                bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `relevance_id`      varchar(255) NOT NULL COMMENT '主表关联Id',
    `warehouse_id`      varchar(255) NOT NULL COMMENT '库房ID',
    `material_coding`   varchar(255) NOT NULL COMMENT '物料编码',
    `material_name`     varchar(255) DEFAULT NULL COMMENT '物料名称',
    `batch`             varchar(255) DEFAULT NULL COMMENT '批次',
    `measurement_unit`  varchar(255) DEFAULT NULL COMMENT '计量单位',
    `cargo_space_id`    varchar(255) DEFAULT NULL COMMENT '货位编码',
    `enter_quantity`    double       DEFAULT NULL COMMENT '入库数量',
    `out_quantity`      double       DEFAULT NULL COMMENT '出库数量',
    `warehouse_manager` varchar(32)  DEFAULT '' COMMENT '库管员',
    `recipient`         varchar(255) DEFAULT NULL COMMENT '领用人',
    `receive_person`    varchar(255) DEFAULT NULL COMMENT '领用单位',
    `remark`            varchar(255) DEFAULT NULL COMMENT '备注',
    `create_time`       timestamp    NOT NULL COMMENT '创建时间',
    `last_update`       timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    `del`               tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否删除',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='出入库清单子表';

CREATE TABLE `record`
(
    `id`                   bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `material_coding`      varchar(255) NOT NULL COMMENT '物料编码',
    `material_name`        varchar(255)          DEFAULT NULL COMMENT '物料名称',
    `batch`                varchar(255)          DEFAULT NULL COMMENT '批次',
    `cargo_space_id`       varchar(255)          DEFAULT NULL COMMENT '货位编码',
    `inventory_type`       varchar(255)          DEFAULT NULL COMMENT '库存类型',
    `type`                 tinyint               DEFAULT NULL COMMENT '出入库变动类型：0-暂存库出库 1-正式库出库',
    `inventory_credit`     double       NOT NULL DEFAULT '0' COMMENT '库存数量',
    `inventory_alteration` double       NOT NULL DEFAULT '0' COMMENT '库存变动数量',
    `consignor`            int          NOT NULL DEFAULT '0' COMMENT '货主 0-泰丰盛和  1-润中，2-雅店，3-蒋家河，4-下沟，5-精煤',
    `change_time`          timestamp    NOT NULL COMMENT '库存变动时间',
    `create_time`          timestamp    NOT NULL COMMENT '创建时间',
    `last_update`          timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    `del`                  tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否删除',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='出入库清单主表';


CREATE TABLE `make_inventory_report_details`
(
    `id`                      int                                                           NOT NULL AUTO_INCREMENT,
    `report_number`           varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '盘点报告编号',
    `warehouse_id`            varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '库房编号',
    `sublibrary_id`           varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci          DEFAULT NULL COMMENT '子库编号',
    `warehouse_area_id`       varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci          DEFAULT NULL COMMENT '库区编号',
    `cargo_space_id`          varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci          DEFAULT NULL COMMENT '货位编码',
    `material_coding`         varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '物料编码',
    `material_name`           varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci          DEFAULT NULL COMMENT '物料名称',
    `batch`                   varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '批次',
    `inventory_type`          int(10) unsigned zerofill DEFAULT NULL COMMENT '库存类型：0-正式库存 1-暂存库存 2-临时库存',
    `inventory_credit`        double                                                        NOT NULL DEFAULT '0' COMMENT '库存数量',
    `check_credit`            double                                                                 DEFAULT NULL COMMENT '实盘数量',
    `check_snap_shoot`        timestamp                                                              DEFAULT NULL COMMENT '实盘快照',
    `check_snap_shoot_time`   double                                                                 DEFAULT NULL COMMENT '实盘快照时间',
    `audit_credit`            double                                                                 DEFAULT NULL COMMENT '稽核数量',
    `audit_credit_snap_shoot` double                                                                 DEFAULT NULL COMMENT '稽核数量',
    `audit_snap_shoot_time`   timestamp                                                              DEFAULT NULL COMMENT '稽核数量时间',
    `final_credit`            double                                                                 DEFAULT NULL COMMENT '盈亏数量',
    `specification_model`     varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci          DEFAULT NULL COMMENT '规格型号',
    `measurement_unit`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci          DEFAULT NULL COMMENT '计量单位',
    `check_status_details`    tinyint                                                       NOT NULL DEFAULT '0' COMMENT '盘点状态: 0-待盘点，1-一致 ，2-盘盈 ，3-盘亏',
    `unit_price`              decimal(10, 2)                                                         DEFAULT NULL COMMENT '单价(泰丰盛和)',
    `sales_unit_price`        decimal(10, 2)                                                         DEFAULT NULL COMMENT '单价(使用单位)',
    `final_amounts`           decimal(10, 2)                                                         DEFAULT NULL COMMENT '盈亏金额',
    `reason`                  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci          DEFAULT '' COMMENT '差异原因',
    `remark`                  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci          DEFAULT NULL COMMENT '备注',
    `supplier`                varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci          DEFAULT '' COMMENT '供应商',
    `consignor`               int                                                           NOT NULL DEFAULT '0' COMMENT '货主 0-泰丰盛和  1-润中，2-雅店，3-蒋家河，4-下沟，5-精煤',
    `version`                 int                                                                    DEFAULT NULL COMMENT '版本-乐观锁',
    `create_time`             timestamp                                                     NOT NULL COMMENT '创建时间',
    `last_update`             timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    `del`                     tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否删除',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=257 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;