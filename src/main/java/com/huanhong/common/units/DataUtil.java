package com.huanhong.common.units;

public class DataUtil {
    /**
     * 获取货主
     *
     * @param consignor
     * @return
     */
    public static String getConsignor(Integer consignor) {
        String name = "泰丰盛和";
        //货主 0-泰丰盛和  1-润中，2-雅店，3-蒋家河，4-下沟，5-精煤
        switch (consignor) {
            case 0:
                name = "泰丰盛和";
                break;
            case 1:
                name = "润中";
                break;
            case 2:
                name = "雅店";
                break;
            case 3:
                name = "蒋家河";
                break;
            case 4:
                name = "下沟";
                break;
            case 5:
                name = "精煤";
                break;
            default:
                break;
        }
        return name;
    }

    /**
     * 获取入库类型
     *
     * @param enterType
     * @return
     */
    public static String getEnterType(Integer enterType) {
        String name = "未知";
        //入库类型：1-采购入库 2-调拨入库
        switch (enterType) {
            case 1:
                name = "采购入库";
                break;
            case 2:
                name = "调拨入库";
                break;
            default:
                break;
        }
        return name;
    }
}
