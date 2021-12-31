package com.huanhong.common.units;

import com.huanhong.wms.entity.MaterialClassification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据目录树工具类
 *
 * @author zhoujunyi10
 * @date 2020-12-09 14:36
 */
@Component
public class TreeUtils {

    private List<MaterialClassification> menuCommon;

    /**
     * @param results
     * @return
     */
    public List<Map<String, Object>> menuList(List<MaterialClassification> results) {

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        this.menuCommon = results;
        // 通过遍历menu，找到父节点为0的节点，它是顶级父节点
        // 然后调用menuChild，递归遍历所有子节点
        for (MaterialClassification result : results) {
            Map<String, Object> mapArr = new LinkedHashMap<String, Object>();
            if (0 == result.getLevelType()) {
                mapArr.put("name", result.getTypeName());
                mapArr.put("pid", result.getParentCode());
                mapArr.put("typeCode", result.getTypeCode());
                mapArr.put("level", result.getLevelType());
                //遍历开始
                mapArr.put("childList", menuChild(Integer.valueOf(result.getTypeCode())));
                list.add(mapArr);
            }
        }
        return list;
    }

    /**
     * 获取子菜单
     *
     * @param parentCode
     * @return
     */
    private List<?> menuChild(Integer parentCode) {
        List<Object> lists = new ArrayList<Object>();
        //继续遍历menu
        for (MaterialClassification result : menuCommon) {
            Map<String, Object> childArray = new LinkedHashMap<String, Object>();
            //找到父ID等于父节点ID的子节点
            if (parentCode.equals(Integer.valueOf(result.getParentCode()))) {
                childArray.put("name", result.getTypeName());
                childArray.put("pid", result.getParentCode());
                childArray.put("typeCode", result.getTypeCode());
                childArray.put("level", result.getLevelType());
                //向下递归
                childArray.put("childList", menuChild(Integer.valueOf(result.getTypeCode())));
                lists.add(childArray);
            }
        }
        return lists;
    }
}