package com.huanhong.common.units;

import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Field;

public class EntityUtils {

    /**
     * 获取实体类的字段名
     *
     * @param object
     * @return
     */
    public JSONObject jsonField(String key, Object object) {
        Field[] fields = object.getClass().getDeclaredFields();
        JSONObject jsonObject = new JSONObject();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            String prefixKey = field.getName();
            if ("planUseOut".equals(key)) {
                jsonObject.put(prefixKey, fieldNamePlanUseOut(prefixKey));
            } else if ("enterWarehouse".equals(key)) {
                jsonObject.put(prefixKey, fieldNameEnterWarehouse(prefixKey));
            } else if ("requirementsPlanning".equals(key)) {
                jsonObject.put(prefixKey, fieldNameRequirementsPlanning(prefixKey));
            } else if ("procurementPlan".equals(key)) {
                jsonObject.put(prefixKey, fieldNameProcurementPlan(prefixKey));
            } else if ("arrivalVerification".equals(key)) {
                jsonObject.put(prefixKey, fieldNameArrivalVerification(prefixKey));
            } else if ("allocationPlan".equals(key)){
                jsonObject.put(prefixKey, fieldNameAllocationPlan(prefixKey));
            } else if ("temporaryEnter".equals(key)){
                jsonObject.put(prefixKey,fieldNameTemporaryEnter(prefixKey));
            } else if ("temporaryOut".equals(key)){
                jsonObject.put(prefixKey,fieldNameTemporaryOut(prefixKey));
            }else if ("makeInventory".equals(key)){
                jsonObject.put(prefixKey,fieldNameMakeInventory(prefixKey));
            }else if ("makeInventoryReport".equals(key)){
                jsonObject.put(prefixKey,fieldNameMakeInventoryReport(prefixKey));
            }
        }
        return jsonObject;
    }


    public JSONObject fieldNamePlanUseOut(String key) {
        JSONObject jsonObject = new JSONObject();
        JSONObject value = new JSONObject();
        switch (key) {
            case "serialVersionUID":
                jsonObject.put("name", "id");
                jsonObject.put("type", "text");
                jsonObject.put("class", "hiden");
                return jsonObject;
            case "documentNumber":
                jsonObject.put("name", "单据编号");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "processInstanceId":
                jsonObject.put("name", "流程id");
                jsonObject.put("type", "text");
                jsonObject.put("class", "hiden");
                return jsonObject;
            case "status":
                jsonObject.put("name", "单据状态");
                jsonObject.put("type", "select");
                jsonObject.put("class", "readOnly");
                //下拉菜单的值
                value.put("1", "草拟");
                value.put("2", "审批中");
                value.put("3", "审批生效");
                value.put("4", "作废");
                jsonObject.put("value", value);
                return jsonObject;
            case "planClassification":
                jsonObject.put("name", "计划类别");
                jsonObject.put("type", "select");
                jsonObject.put("class", "readOnly");
                //下拉菜单的值
                value.put("1", "正常");
                value.put("2", "加急");
                value.put("3", "补计划");
                jsonObject.put("value", value);
                return jsonObject;
            case "outType":
                jsonObject.put("name", "出库类型");
                jsonObject.put("type", "select");
                jsonObject.put("class", "readOnly");
                //下拉菜单的值
                value.put("0", "暂存库出库");
                value.put("1", "正式库出库");
                jsonObject.put("value", value);
                return jsonObject;
            case "requisitioningUnit":
                jsonObject.put("name", "领用单位");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "warehouseId":
                jsonObject.put("name", "库房ID");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "librarian":
                jsonObject.put("name", "库管员");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "costBearingUnit":
                jsonObject.put("name", "费用承担单位");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "expenseItem":
                jsonObject.put("name", "费用项目");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "materialUse":
                jsonObject.put("name", "物资用途");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "requisitionUse":
                jsonObject.put("name", "领用用途");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "outStatus":
                jsonObject.put("name", "出库状态");
                jsonObject.put("type", "select");
                jsonObject.put("class", "hiden");
                value.put("0", "未出库");
                value.put("1", "部分出库");
                value.put("2", "全部出库");
                jsonObject.put("value", value);
                return jsonObject;
            case "remark":
                jsonObject.put("name", "备注");
                jsonObject.put("type", "text");
                jsonObject.put("class", "input");
                return jsonObject;
            case "createTime":
                jsonObject.put("name", "创建时间");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "lastUpdate":
                jsonObject.put("name", "最后更新时间");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "usePlanningDocumentNumber":
                jsonObject.put("name", "原单据编号");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "materialCoding":
                jsonObject.put("name", "物料编码");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "materialName":
                jsonObject.put("name", "物料名称");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "requisitionQuantity":
                jsonObject.put("name", "领用数量");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "approvalsQuantity":
                jsonObject.put("name", "批准数量");
                jsonObject.put("type", "text");
                jsonObject.put("class", "input");
                return jsonObject;
            case "outboundQuantity":
                jsonObject.put("name", "实出数量");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "inventoryCredit":
                jsonObject.put("name", "库存数量");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "usePlace":
                jsonObject.put("name", "使用地点");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "purpose":
                jsonObject.put("name", "用途");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "version":
                jsonObject.put("name", "版本号");
                jsonObject.put("type", "text");
                jsonObject.put("class", "hiden");
                return jsonObject;
            default:
                return null;
        }
    }

    public JSONObject fieldNameEnterWarehouse(String key) {
        JSONObject jsonObject = new JSONObject();
        JSONObject value = new JSONObject();
        switch (key) {
            case "id":
                jsonObject.put("name", "id");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "serialVersionUID":
                jsonObject.put("name", "id");
                jsonObject.put("type", "text");
                jsonObject.put("class", "hiden");
                return jsonObject;
            case "documentNumber":
                jsonObject.put("name", "采购入库单据编号");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "processInstanceId":
                jsonObject.put("name", "流程id");
                jsonObject.put("type", "text");
                jsonObject.put("class", "hiden");
                return jsonObject;
            case "storageType":
                jsonObject.put("name", "入库类型");
                jsonObject.put("type", "select");
                jsonObject.put("class", "readOnly");
                //下拉菜单的值
                value.put("1", "暂估入库");
                value.put("2", "正式入库");
                jsonObject.put("value", value);
                return jsonObject;
            case "contractNumber":
                jsonObject.put("name", "采购合同编号");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "rfqNumber":
                jsonObject.put("name", "询价单编号");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "state":
                jsonObject.put("name", "单据状态");
                jsonObject.put("type", "select");
                jsonObject.put("class", "readOnly");
                //下拉菜单的值
                value.put("1", "草拟");
                value.put("2", "审批中");
                value.put("3", "审批生效");
                value.put("4", "作废");
                jsonObject.put("value", value);
                return jsonObject;
            case "verificationDocumentNumber":
                jsonObject.put("name", "到货检验单编号");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "planClassification":
                jsonObject.put("name", "计划类别");
                jsonObject.put("type", "select");
                jsonObject.put("class", "readOnly");
                //下拉菜单的值
                value.put("1", "正常");
                value.put("2", "加急");
                value.put("3", "补计划");
                jsonObject.put("value", value);
                return jsonObject;
            case "receiptNumber":
                jsonObject.put("name", "发票号");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "deliveryDate":
                jsonObject.put("name", "到货日期");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "manager":
                jsonObject.put("name", "经办人");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "supplierName":
                jsonObject.put("name", "供应商名称");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "warehouse":
                jsonObject.put("name", "库房编号");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "remark":
                jsonObject.put("name", "备注");
                jsonObject.put("type", "text");
                jsonObject.put("class", "input");
                return jsonObject;
            case "createTime":
                jsonObject.put("name", "创建时间");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "lastUpdate":
                jsonObject.put("name", "最后更新时间");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "originalDocumentNumber":
                jsonObject.put("name", "原单据编号");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "materialCoding":
                jsonObject.put("name", "物料编码");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "batch":
                jsonObject.put("name", "批次");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "validPeriod":
                jsonObject.put("name", "有效日期");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "quantityReceivable":
                jsonObject.put("name", "应收数量");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "actualQuantity":
                jsonObject.put("name", "实收数量");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "unitPriceWithoutTax":
                jsonObject.put("name", "不含税单价");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "excludingTaxAmount":
                jsonObject.put("name", "不含税金额");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "unitPriceIncludingTax":
                jsonObject.put("name", "含税单价");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "taxIncludedAmount":
                jsonObject.put("name", "含税金额");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "taxRate":
                jsonObject.put("name", "税率");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "loanTax":
                jsonObject.put("name", "贷款税额");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "version":
                jsonObject.put("name", "版本号");
                jsonObject.put("type", "text");
                jsonObject.put("class", "hiden");
                return jsonObject;
            default:
                return null;
        }
    }

    public JSONObject fieldNameRequirementsPlanning(String key) {
        JSONObject jsonObject = new JSONObject();
        JSONObject value = new JSONObject();
        switch (key) {
            case "id":
                jsonObject.put("name", "id");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "serialVersionUID":
                jsonObject.put("name", "id");
                jsonObject.put("type", "text");
                jsonObject.put("class", "hiden");
                return jsonObject;
            case "planNumber":
                jsonObject.put("name", "需求计划单据编号");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "processInstanceId":
                jsonObject.put("name", "流程id");
                jsonObject.put("type", "text");
                jsonObject.put("class", "hiden");
                return jsonObject;
            case "planUnit":
                jsonObject.put("name", "计划部门");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "warehouseId":
                jsonObject.put("name", "仓库编号");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "applicant":
                jsonObject.put("name", "申请人");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "planStatus":
                jsonObject.put("name", "单据状态");
                jsonObject.put("type", "select");
                jsonObject.put("class", "readOnly");
                //下拉菜单的值
                value.put("1", "草拟");
                value.put("2", "审批中");
                value.put("3", "审批生效");
                value.put("4", "作废");
                jsonObject.put("value", value);
                return jsonObject;
            case "planClassification":
                jsonObject.put("name", "计划类别");
                jsonObject.put("type", "select");
                jsonObject.put("class", "readOnly");
                //下拉菜单的值
                value.put("1", "正常");
                value.put("2", "加急");
                value.put("3", "补计划");
                jsonObject.put("value", value);
                return jsonObject;
            case "vestimatedTotalAmount":
                jsonObject.put("name", "预估总金额");
                jsonObject.put("type", "text");
                jsonObject.put("class", "input");
                return jsonObject;
            case "materialUse":
                jsonObject.put("name", "物料用途");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "remark":
                jsonObject.put("name", "备注");
                jsonObject.put("type", "text");
                jsonObject.put("class", "input");
                return jsonObject;
            case "createTime":
                jsonObject.put("name", "创建时间");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "lastUpdate":
                jsonObject.put("name", "最后更新时间");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "materialCoding":
                jsonObject.put("name", "物料编码");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "materialId":
                jsonObject.put("name", "物料ID");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "materialName":
                jsonObject.put("name", "物料名称");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "requiredQuantity":
                jsonObject.put("name", "需求数量");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "plannedPurchaseQuantity":
                jsonObject.put("name", "计划采购数量");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "approvedQuantity":
                jsonObject.put("name", "批准数量");
                jsonObject.put("type", "text");
                jsonObject.put("class", "input");
                return jsonObject;
            case "estimatedUnitPrice":
                jsonObject.put("name", "预估单价");
                jsonObject.put("type", "text");
                jsonObject.put("class", "input");
                return jsonObject;
            case "estimatedAmount":
                jsonObject.put("name", "预估金额");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readonly");
                return jsonObject;
            case "arrivalTime":
                jsonObject.put("name", "要求到货时间");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readonly");
                return jsonObject;
            case "usePurpose":
                jsonObject.put("name", "使用用途");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readonly");
                return jsonObject;
            case "usePlace":
                jsonObject.put("name", "使用地点");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readonly");
                return jsonObject;
            case "version":
                jsonObject.put("name", "版本号");
                jsonObject.put("type", "text");
                jsonObject.put("class", "hiden");
                return jsonObject;
            default:
                return null;
        }
    }

    public JSONObject fieldNameProcurementPlan(String key) {
        JSONObject jsonObject = new JSONObject();
        JSONObject value = new JSONObject();
        switch (key) {
            case "id":
                jsonObject.put("name", "id");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "serialVersionUID":
                jsonObject.put("name", "id");
                jsonObject.put("type", "text");
                jsonObject.put("class", "hiden");
                return jsonObject;
            case "originalDocumentNumber":
                jsonObject.put("name", "需求计划单据编号");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "planNumber":
                jsonObject.put("name", "采购计划单据编号");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "processInstanceId":
                jsonObject.put("name", "流程id");
                jsonObject.put("type", "text");
                jsonObject.put("class", "hiden");
                return jsonObject;
            case "warehouseId":
                jsonObject.put("name", "仓库编号");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "materialUse":
                jsonObject.put("name", "物料用途");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "planClassification":
                jsonObject.put("name", "计划类别");
                jsonObject.put("type", "select");
                jsonObject.put("class", "readOnly");
                //下拉菜单的值
                value.put("1", "正常");
                value.put("2", "加急");
                value.put("3", "补计划");
                jsonObject.put("value", value);
                return jsonObject;
            case "status":
                jsonObject.put("name", "单据状态");
                jsonObject.put("type", "select");
                jsonObject.put("class", "readOnly");
                //下拉菜单的值
                value.put("1", "草拟");
                value.put("2", "审批中");
                value.put("3", "审批生效");
                value.put("4", "作废");
                jsonObject.put("value", value);
                return jsonObject;
            case "planningDepartment":
                jsonObject.put("name", "计划部门");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "planner":
                jsonObject.put("name", "计划员");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "demandDepartment":
                jsonObject.put("name", "需求部门");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "remark":
                jsonObject.put("name", "备注");
                jsonObject.put("type", "text");
                jsonObject.put("class", "input");
                return jsonObject;
            case "createTime":
                jsonObject.put("name", "创建时间");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "lastUpdate":
                jsonObject.put("name", "最后更新时间");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "materialCoding":
                jsonObject.put("name", "物料编码");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "requiredQuantity":
                jsonObject.put("name", "需求数量");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "plannedPurchaseQuantity":
                jsonObject.put("name", "计划采购数量");
                jsonObject.put("type", "text");
                jsonObject.put("class", "input");
                return jsonObject;
            case "approvedQuantity":
                jsonObject.put("name", "批准数量");
                jsonObject.put("type", "text");
                jsonObject.put("class", "input");
                return jsonObject;
            case "estimatedUnitPrice":
                jsonObject.put("name", "预估单价");
                jsonObject.put("type", "text");
                jsonObject.put("class", "input");
                return jsonObject;
            case "estimatedAmount":
                jsonObject.put("name", "预估金额");
                jsonObject.put("type", "text");
                jsonObject.put("class", "input");
                return jsonObject;
            case "inventory":
                jsonObject.put("name", "库存数量");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "safetyStock":
                jsonObject.put("name", "安全库存");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "requestArrivalTime":
                jsonObject.put("name", "要求到货时间");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readonly");
                return jsonObject;
            case "usePurpose":
                jsonObject.put("name", "使用用途");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readonly");
                return jsonObject;
            case "usePlace":
                jsonObject.put("name", "使用地点");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readonly");
                return jsonObject;
            case "version":
                jsonObject.put("name", "版本号");
                jsonObject.put("type", "text");
                jsonObject.put("class", "hiden");
                return jsonObject;
            default:
                return null;
        }
    }

    public JSONObject fieldNameArrivalVerification(String key) {
        JSONObject jsonObject = new JSONObject();
        JSONObject value = new JSONObject();
        switch (key) {
            case "id":
                jsonObject.put("name", "id");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "serialVersionUID":
                jsonObject.put("name", "id");
                jsonObject.put("type", "text");
                jsonObject.put("class", "hiden");
                return jsonObject;
            case "verificationDocumentNumber":
                jsonObject.put("name", "到货检验单编号");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "originalDocumentNumber":
                jsonObject.put("name", "清点单编号");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "verificationStatus":
                jsonObject.put("name", "检验状态");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "contractNumber":
                jsonObject.put("name", "采购合同编号");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "rfqNumber":
                jsonObject.put("name", "询价单编号");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "processInstanceId":
                jsonObject.put("name", "流程id");
                jsonObject.put("type", "text");
                jsonObject.put("class", "hiden");
                return jsonObject;
            case "planClassification":
                jsonObject.put("name", "计划类别");
                jsonObject.put("type", "select");
                jsonObject.put("class", "readOnly");
                //下拉菜单的值
                value.put("1", "正常");
                value.put("2", "加急");
                value.put("3", "补计划");
                jsonObject.put("value", value);
                return jsonObject;
            case "planStatus":
                jsonObject.put("name", "单据状态");
                jsonObject.put("type", "select");
                jsonObject.put("class", "readOnly");
                //下拉菜单的值
                value.put("1", "草拟");
                value.put("2", "审批中");
                value.put("3", "审批生效");
                value.put("4", "作废");
                jsonObject.put("value", value);
                return jsonObject;
            case "warehouseId":
                jsonObject.put("name", "仓库编号");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "deliveryDate":
                jsonObject.put("name", "到货日期");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readonly");
                return jsonObject;
            case "checkerId":
                jsonObject.put("name", "检验人id");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "checkerName":
                jsonObject.put("name", "检验人名");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "inspector":
                jsonObject.put("name", "理货人");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "supplierName":
                jsonObject.put("name", "供应商名称");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "carNumber":
                jsonObject.put("name", "车号");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "remark":
                jsonObject.put("name", "备注");
                jsonObject.put("type", "text");
                jsonObject.put("class", "input");
                return jsonObject;
            case "createTime":
                jsonObject.put("name", "创建时间");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "lastUpdate":
                jsonObject.put("name", "最后更新时间");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "documentNumber":
                jsonObject.put("name", "检验单编号");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "materialCoding":
                jsonObject.put("name", "物料编码");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "materialId":
                jsonObject.put("name", "物料ID");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "materialName":
                jsonObject.put("name", "物料名称");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "batch":
                jsonObject.put("name", "批次");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "validPeriod":
                jsonObject.put("name", "有效期");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "arrivalQuantity":
                jsonObject.put("name", "到货数量");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "qualifiedQuantity":
                jsonObject.put("name", "合格数量");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "unqualifiedQuantity":
                jsonObject.put("name", "不合格数量");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "version":
                jsonObject.put("name", "版本号");
                jsonObject.put("type", "text");
                jsonObject.put("class", "hiden");
                return jsonObject;
            default:
                return null;
        }
    }

    public JSONObject fieldNameAllocationPlan(String key) {
        JSONObject jsonObject = new JSONObject();
        JSONObject value = new JSONObject();
        switch (key) {
            case "id":
                jsonObject.put("name", "id");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "serialVersionUID":
                jsonObject.put("name", "id");
                jsonObject.put("type", "text");
                jsonObject.put("class", "hiden");
                return jsonObject;
            case "allocation_number":
                jsonObject.put("name", "调拨申请单据编号");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "processInstanceId":
                jsonObject.put("name", "流程id");
                jsonObject.put("type", "text");
                jsonObject.put("class", "hiden");
                return jsonObject;
            case "businessType":
                jsonObject.put("name", "业务类型");
                jsonObject.put("type", "select");
                jsonObject.put("class", "readOnly");
                //下拉菜单的值
                value.put("1", "计划调拨");
                value.put("2", "预备调拨");
                jsonObject.put("value", value);
                return jsonObject;
            case "planStatus":
                jsonObject.put("name", "单据状态");
                jsonObject.put("type", "select");
                jsonObject.put("class", "readOnly");
                //下拉菜单的值
                value.put("1", "草拟");
                value.put("2", "审批中");
                value.put("3", "审批生效");
                value.put("4", "作废");
                jsonObject.put("value", value);
                return jsonObject;
            case "assignmentDat":
                jsonObject.put("name", "调拨日期");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readonly");
                return jsonObject;
            case "sendWarehouse":
                jsonObject.put("name", "调出仓库");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "sendUser":
                jsonObject.put("name", "调出负责人");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "receiveWarehouse":
                jsonObject.put("name", "调入仓库");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "receiveUser":
                jsonObject.put("name", "调入负责人");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "applicant":
                jsonObject.put("name", "申请人");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "remark":
                jsonObject.put("name", "备注");
                jsonObject.put("type", "text");
                jsonObject.put("class", "input");
                return jsonObject;
            case "createTime":
                jsonObject.put("name", "创建时间");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "lastUpdate":
                jsonObject.put("name", "最后更新时间");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "materialCoding":
                jsonObject.put("name", "物料编码");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "requestQuantity":
                jsonObject.put("name", "请调数量");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "calibrationQuantity":
                jsonObject.put("name", "准调数量");
                jsonObject.put("type", "text");
                jsonObject.put("class", "input");
                return jsonObject;
            case "batch":
                jsonObject.put("name", "批次");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "unitPrice":
                jsonObject.put("name", "单价");
                jsonObject.put("type", "text");
                jsonObject.put("class", "input");
                return jsonObject;
            case "consignor":
                jsonObject.put("name", "货主");
                jsonObject.put("type", "select");
                jsonObject.put("class", "readOnly");
                //下拉菜单的值
                value.put("0", "泰丰盛和");
                value.put("1", "润中");
                value.put("2", "雅店");
                value.put("3", "蒋家河");
                value.put("4", "下沟");
                value.put("5", "精煤");
                jsonObject.put("value", value);
                return jsonObject;
            case "totalAmount":
                jsonObject.put("name", "总金额");
                jsonObject.put("type", "text");
                jsonObject.put("class", "input");
                return jsonObject;
            case "version":
                jsonObject.put("name", "版本号");
                jsonObject.put("type", "text");
                jsonObject.put("class", "hiden");
                return jsonObject;
            default:
                return null;
        }
    }

    public JSONObject fieldNameTemporaryEnter(String key){
        JSONObject jsonObject = new JSONObject();
        JSONObject value = new JSONObject();
        switch (key) {
            case "id":
                jsonObject.put("name", "id");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "serialVersionUID":
                jsonObject.put("name", "id");
                jsonObject.put("type", "text");
                jsonObject.put("class", "hiden");
                return jsonObject;
            case "documentNumber":
                jsonObject.put("name", "临时库入库单据编号");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "processInstanceId":
                jsonObject.put("name", "流程id");
                jsonObject.put("type", "text");
                jsonObject.put("class", "hiden");
                return jsonObject;
            case "state":
                jsonObject.put("name", "单据状态");
                jsonObject.put("type", "select");
                jsonObject.put("class", "readOnly");
                //下拉菜单的值
                value.put("1", "草拟");
                value.put("2", "审批中");
                value.put("3", "审批生效");
                value.put("4", "作废");
                jsonObject.put("value", value);
                return jsonObject;
            case "validPeriod":
                jsonObject.put("name", "有效日期");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "actualQuantity":
                jsonObject.put("name", "实收数量");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "materialCoding":
                jsonObject.put("name", "物料编码");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "batch":
                jsonObject.put("name", "批次");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "warehouseId":
                jsonObject.put("name", "库房编号");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "enterTime":
                jsonObject.put("name", "入库时间");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "remark":
                jsonObject.put("name", "备注");
                jsonObject.put("type", "text");
                jsonObject.put("class", "input");
                return jsonObject;
            case "createTime":
                jsonObject.put("name", "创建时间");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "lastUpdate":
                jsonObject.put("name", "最后更新时间");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "version":
                jsonObject.put("name", "版本号");
                jsonObject.put("type", "text");
                jsonObject.put("class", "hiden");
                return jsonObject;
            default:
                return null;
        }
    }

    public JSONObject fieldNameTemporaryOut(String key){
        JSONObject jsonObject = new JSONObject();
        JSONObject value = new JSONObject();
        switch (key) {
            case "id":
                jsonObject.put("name", "id");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "serialVersionUID":
                jsonObject.put("name", "id");
                jsonObject.put("type", "text");
                jsonObject.put("class", "hiden");
                return jsonObject;
            case "documentNumber":
                jsonObject.put("name", "临时库出库单据编号");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "processInstanceId":
                jsonObject.put("name", "流程id");
                jsonObject.put("type", "text");
                jsonObject.put("class", "hiden");
                return jsonObject;
            case "status":
                jsonObject.put("name", "单据状态");
                jsonObject.put("type", "select");
                jsonObject.put("class", "readOnly");
                //下拉菜单的值
                value.put("1", "草拟");
                value.put("2", "审批中");
                value.put("3", "审批生效");
                value.put("4", "作废");
                jsonObject.put("value", value);
                return jsonObject;
            case "materialCoding":
                jsonObject.put("name", "物料编码");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "materialName":
                jsonObject.put("name", "物料名称");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "batch":
                jsonObject.put("name", "批次");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "requisitionQuantity":
                jsonObject.put("name", "领用数量");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "requisitioningUnit":
                jsonObject.put("name", "领用单位");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "recipient":
                jsonObject.put("name", "领用人");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "warehouseId":
                jsonObject.put("name", "库房编号");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "librarian":
                jsonObject.put("name", "库管员");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "remark":
                jsonObject.put("name", "备注");
                jsonObject.put("type", "text");
                jsonObject.put("class", "input");
                return jsonObject;
            case "createTime":
                jsonObject.put("name", "创建时间");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "lastUpdate":
                jsonObject.put("name", "最后更新时间");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "version":
                jsonObject.put("name", "版本号");
                jsonObject.put("type", "text");
                jsonObject.put("class", "hiden");
                return jsonObject;
            default:
                return null;
        }
    }

    public JSONObject fieldNameMakeInventory(String key) {
        JSONObject jsonObject = new JSONObject();
        JSONObject value = new JSONObject();
        switch (key) {
            case "id":
                jsonObject.put("name", "id");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "serialVersionUID":
                jsonObject.put("name", "id");
                jsonObject.put("type", "text");
                jsonObject.put("class", "hiden");
                return jsonObject;
            case "processInstanceId":
                jsonObject.put("name", "流程id");
                jsonObject.put("type", "text");
                jsonObject.put("class", "hiden");
                return jsonObject;
            case "documentNumber":
                jsonObject.put("name", "盘点单单据编号");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                jsonObject.put("sort", 1);
                return jsonObject;
            case "planStatus":
                jsonObject.put("name", "单据状态");
                jsonObject.put("type", "select");
                jsonObject.put("class", "readOnly");
                //下拉菜单的值
                value.put("1", "草拟");
                value.put("2", "审批中");
                value.put("3", "审批生效");
                value.put("4", "作废");
                jsonObject.put("value", value);
                return jsonObject;
            case "checkStatus":
                jsonObject.put("name", "状态");
                jsonObject.put("type", "select");
                jsonObject.put("class", "readOnly");
                //下拉菜单的值
                value.put("0", "待盘点");
                value.put("1", "已盘点");
                jsonObject.put("value", value);
                jsonObject.put("sort", 17);
                return jsonObject;
            case "allMake":
                jsonObject.put("name", "是否全盘");
                jsonObject.put("type", "select");
                jsonObject.put("class", "readOnly");
                //下拉菜单的值
                value.put("0", "非全盘");
                value.put("1", "全盘");
                jsonObject.put("value", value);
                return jsonObject;
            case "startTime":
                jsonObject.put("name", "盘点开始时间");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "endTime":
                jsonObject.put("name", "盘点结束时间");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "warehouseId":
                jsonObject.put("name", "库房编号");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                jsonObject.put("sort", 8);
                return jsonObject;
            case "sublibraryId":
                jsonObject.put("name", "子库编号");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                jsonObject.put("sort", 9);
                return jsonObject;
            case "checkerIds":
                jsonObject.put("name", "盘点人列表");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "auditId":
                jsonObject.put("name", "稽核人Id");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "auditName":
                jsonObject.put("name", "稽核人姓名");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "checkerId":
                jsonObject.put("name", "盘点人");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "checkerName":
                jsonObject.put("name", "盘点人姓名");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "remark":
                jsonObject.put("name", "备注");
                jsonObject.put("type", "text");
                jsonObject.put("class", "input");
                return jsonObject;
            case "createTime":
                jsonObject.put("name", "创建时间");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "lastUpdate":
                jsonObject.put("name", "最后更新时间");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "warehouseAreaId":
                jsonObject.put("name", "库区编号");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                jsonObject.put("sort", 7);
                return jsonObject;
            case "cargoSpaceId":
                jsonObject.put("name", "货位编码");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                jsonObject.put("sort", 5);
                return jsonObject;
            case "materialCoding":
                jsonObject.put("name", "物料编码");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                jsonObject.put("sort", 2);
                return jsonObject;
            case "materialName":
                jsonObject.put("name", "物料名称");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                jsonObject.put("sort", 3);
                return jsonObject;
            case "batch":
                jsonObject.put("name", "批次");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                jsonObject.put("sort", 6);
                return jsonObject;
            case "inventoryType":
                jsonObject.put("name", "库存类型");
                jsonObject.put("type", "select");
                jsonObject.put("class", "readOnly");
                jsonObject.put("sort", 13);
                //下拉菜单的值
                value.put("0", "全部");
                value.put("1", "正式库存");
                value.put("2","暂存库存");
                value.put("3","临时库存");
                jsonObject.put("value", value);
                return jsonObject;
            case "materialType":
                jsonObject.put("name", "物料类型");
                jsonObject.put("type", "select");
                jsonObject.put("class", "readOnly");
                jsonObject.put("sort", 5);
                //下拉菜单的值
                value.put("0", "全部物料");
                value.put("1", "指定物料");
                value.put("2","随机物料");
                jsonObject.put("value", value);
                return jsonObject;
            case "inventoryCredit":
                jsonObject.put("name", "库存数量");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                jsonObject.put("sort", 14);
                return jsonObject;
            case "checkCredit":
                jsonObject.put("name", "实盘数量");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                jsonObject.put("sort", 18);
                return jsonObject;
            case "auditCredit":
                jsonObject.put("name", "稽核数量");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "specificationModel":
                jsonObject.put("name", "规格型号");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                jsonObject.put("sort", 16);
                return jsonObject;
            case "measurementUnit":
                jsonObject.put("name", "计量单位");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                jsonObject.put("sort", 15);
                return jsonObject;
            case "unitPrice":
                jsonObject.put("name", "单价(泰丰盛和)");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                jsonObject.put("sort", 12);
                return jsonObject;
            case "salesUnitPrice":
                jsonObject.put("name", "单价(使用单位)");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "reason":
                jsonObject.put("name", "差异原因");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "supplier":
                jsonObject.put("name", "供应商名称");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                jsonObject.put("sort", 10);
                return jsonObject;
            case "consignor":
                jsonObject.put("name", "货主");
                jsonObject.put("type", "select");
                jsonObject.put("class", "readOnly");
                jsonObject.put("sort", 11);
                //下拉菜单的值
                value.put("0", "泰丰盛和");
                value.put("1", "矿上自有");
                value.put("2", "全部");
                jsonObject.put("value", value);
                return jsonObject;
            case "version":
                jsonObject.put("name", "版本号");
                jsonObject.put("type", "text");
                jsonObject.put("class", "hiden");
                return jsonObject;
            default:
                return null;
        }
    }

    public JSONObject fieldNameMakeInventoryReport(String key) {

        JSONObject jsonObject = new JSONObject();
        JSONObject value = new JSONObject();
        switch (key) {
            case "id":
                jsonObject.put("name", "id");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "serialVersionUID":
                jsonObject.put("name", "id");
                jsonObject.put("type", "text");
                jsonObject.put("class", "hiden");
                return jsonObject;
            case "processInstanceId":
                jsonObject.put("name", "流程id");
                jsonObject.put("type", "text");
                jsonObject.put("class", "hiden");
                return jsonObject;
            case "documentNumber":
                jsonObject.put("name", "盘点单单据编号");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "reportNumber":
                jsonObject.put("name", "盘点报告编号");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                jsonObject.put("sort",1);
                return jsonObject;
            case "planStatus":
                jsonObject.put("name", "单据状态");
                jsonObject.put("type", "select");
                jsonObject.put("class", "readOnly");
                //下拉菜单的值
                value.put("1", "草拟");
                value.put("2", "审批中");
                value.put("3", "审批生效");
                value.put("4", "作废");
                value.put("5","驳回");
                jsonObject.put("value", value);
                return jsonObject;
            case "checkStatus":
                jsonObject.put("name", "状态");
                jsonObject.put("type", "select");
                jsonObject.put("class", "readOnly");
                //下拉菜单的值
                value.put("0", "待盘点");
                value.put("1", "已盘点");
                jsonObject.put("value", value);
                return jsonObject;
            case "allMake":
                jsonObject.put("name", "是否全盘");
                jsonObject.put("type", "select");
                jsonObject.put("class", "readOnly");
                //下拉菜单的值
                value.put("0", "非全盘");
                value.put("1", "全盘");
                jsonObject.put("value", value);
                return jsonObject;
            case "startTime":
                jsonObject.put("name", "盘点开始时间");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "endTime":
                jsonObject.put("name", "盘点结束时间");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "warehouseId":
                jsonObject.put("name", "库房编号");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                jsonObject.put("sort",8);
                return jsonObject;
            case "sublibraryId":
                jsonObject.put("name", "子库编号");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                jsonObject.put("sort",9);
                return jsonObject;
            case "remark":
                jsonObject.put("name", "备注");
                jsonObject.put("type", "text");
                jsonObject.put("class", "input");
                jsonObject.put("sort",22);
                return jsonObject;
            case "rejectReason":
                jsonObject.put("name", "驳回原因");
                jsonObject.put("type", "text");
                jsonObject.put("class", "input");
                return jsonObject;
            case "createTime":
                jsonObject.put("name", "创建时间");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                jsonObject.put("sort",23);
                return jsonObject;
            case "lastUpdate":
                jsonObject.put("name", "最后更新时间");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                jsonObject.put("sort",24);
                return jsonObject;
            case "warehouseAreaId":
                jsonObject.put("name", "库区编号");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                jsonObject.put("sort",7);
                return jsonObject;

            case "cargoSpaceId":
                jsonObject.put("name", "货位编码");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                jsonObject.put("sort",5);
                return jsonObject;
            case "materialCoding":
                jsonObject.put("name", "物料编码");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                jsonObject.put("sort",2);
                return jsonObject;
            case "materialName":
                jsonObject.put("name", "物料名称");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                jsonObject.put("sort",3);
                return jsonObject;
            case "batch":
                jsonObject.put("name", "批次");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                jsonObject.put("sort",6);
                return jsonObject;
            case "inventoryType":
                jsonObject.put("name", "库存类型");
                jsonObject.put("type", "select");
                jsonObject.put("class", "readOnly");
                jsonObject.put("sort",13);
                //下拉菜单的值
                value.put("0", "正式库存");
                value.put("1", "暂存库存");
                value.put("2","临时库存");
                jsonObject.put("value", value);
                return jsonObject;
            case "checkerIds":
                jsonObject.put("name", "盘点人列表");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "auditId":
                jsonObject.put("name", "稽核人Id");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "auditName":
                jsonObject.put("name", "稽核人姓名");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "checkerId":
                jsonObject.put("name", "盘点人");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "checkerName":
                jsonObject.put("name", "盘点人姓名");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                return jsonObject;
            case "inventoryCredit":
                jsonObject.put("name", "库存数量");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                jsonObject.put("sort",14);
                return jsonObject;
            case "checkCredit":
                jsonObject.put("name", "实盘数量");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                jsonObject.put("sort",18);
                return jsonObject;
            case "finalCredit":
                jsonObject.put("name", "盈亏数量");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                jsonObject.put("sort",19);
                return jsonObject;
            case "specificationModel":
                jsonObject.put("name", "规格型号");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                jsonObject.put("sort",16);
                return jsonObject;
            case "measurementUnit":
                jsonObject.put("name", "计量单位");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                jsonObject.put("sort",4);
                return jsonObject;
            case "unitPrice":
                jsonObject.put("name", "单价(泰丰盛和)");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                jsonObject.put("sort",15);
                return jsonObject;
            case "salesUnitPrice":
                jsonObject.put("name", "单价(使用单位)");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                jsonObject.put("sort",12);
                return jsonObject;
            case "reason":
                jsonObject.put("name", "差异原因");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                jsonObject.put("sort",21);
                return jsonObject;
            case "finalAmounts":
                jsonObject.put("name", "盈亏金额");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                jsonObject.put("sort",20);
                return jsonObject;
            case "supplier":
                jsonObject.put("name", "供应商名称");
                jsonObject.put("type", "text");
                jsonObject.put("class", "readOnly");
                jsonObject.put("sort",10);
                return jsonObject;
            case "consignor":
                jsonObject.put("name", "货主");
                jsonObject.put("type", "select");
                jsonObject.put("class", "readOnly");
                jsonObject.put("sort",11);
                //下拉菜单的值
                value.put("0", "泰丰盛和");
                value.put("1", "润中");
                value.put("2", "雅店");
                value.put("3", "蒋家河");
                value.put("4", "下沟");
                value.put("5", "精煤");
                jsonObject.put("value", value);
                return jsonObject;
            case "checkStatusDetails":
                jsonObject.put("name", "盘点状态");
                jsonObject.put("type", "select");
                jsonObject.put("class", "readOnly");
                jsonObject.put("sort",17);
                //下拉菜单的值
                value.put("0", "待盘点");
                value.put("1", "一致");
                value.put("2", "盘盈");
                value.put("3", "盘亏");
                jsonObject.put("value", value);
                return jsonObject;
            case "version":
                jsonObject.put("name", "版本号");
                jsonObject.put("type", "text");
                jsonObject.put("class", "hiden");
                return jsonObject;
            default:
                return null;
        }
    }
}
