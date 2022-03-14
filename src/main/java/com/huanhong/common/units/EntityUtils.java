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
                jsonObject.put(prefixKey,fieldNameEnterWarehouse(prefixKey));
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
                jsonObject.put("name","id");
                jsonObject.put("type","text");
                jsonObject.put("class","readOnly");
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

}
