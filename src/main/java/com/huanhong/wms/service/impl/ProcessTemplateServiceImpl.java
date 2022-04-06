package com.huanhong.wms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.SuperServiceImpl;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.ProcessTemplate;
import com.huanhong.wms.entity.User;
import com.huanhong.wms.entity.dto.AddProcessTemplateDTO;
import com.huanhong.wms.entity.dto.UpdateProcessTemplateDTO;
import com.huanhong.wms.entity.vo.ProcessTemplateVO;
import com.huanhong.wms.mapper.ProcessTemplateMapper;
import com.huanhong.wms.mapper.UserMapper;
import com.huanhong.wms.service.IProcessTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2022-03-04
 */
@Slf4j
@Service
public class ProcessTemplateServiceImpl extends SuperServiceImpl<ProcessTemplateMapper, ProcessTemplate> implements IProcessTemplateService {


    @Resource
    private ProcessTemplateMapper processTemplateMapper;
    @Resource
    private UserMapper userMapper;


    /**
     * 分页查询
     *
     * @param processTemplatePage
     * @param processTemplateVO
     * @return
     */
    @Override
    public Page<ProcessTemplate> pageFuzzyQuery(Page<ProcessTemplate> processTemplatePage, ProcessTemplateVO processTemplateVO) {

        //新建QueryWrapper对象
        QueryWrapper<ProcessTemplate> query = new QueryWrapper<>();

        //根据id排序
        query.orderByDesc("id");

        //判断此时的条件对象Vo是否等于空，若等于空，
        //直接进行selectPage查询
        if (ObjectUtil.isEmpty(processTemplateVO)) {
            return processTemplateMapper.selectPage(processTemplatePage, query);
        }

        //若Vo对象不为空，分别获取其中的字段，
        //并对其进行判断是否为空，这一步类似动态SQL的拼装
        query.like(StringUtils.isNotBlank(processTemplateVO.getProcessCode()), "process_code", processTemplateVO.getProcessCode());

        query.like(StringUtils.isNotBlank(processTemplateVO.getWarehouseId()), "warehouse_id", processTemplateVO.getWarehouseId());
        query.eq(null != processTemplateVO.getTemplateType(), "template_type", processTemplateVO.getTemplateType());
        return processTemplateMapper.selectPage(processTemplatePage, query);
    }

    @Override
    public Result addProcessTemplate(List<AddProcessTemplateDTO> listAddProcessTemplateDTO) {

        try {
            int count = 0;
            List<ProcessTemplate> successAddList = new LinkedList<>();
            List<AddProcessTemplateDTO> falseAddList = new LinkedList<>();
            ProcessTemplate processTemplate = new ProcessTemplate();
            for (AddProcessTemplateDTO addProcessTemplateDTO : listAddProcessTemplateDTO
            ) {
                BeanUtil.copyProperties(addProcessTemplateDTO, processTemplate);
                int add = processTemplateMapper.insert(processTemplate);
                if (add > 0) {
                    successAddList.add(processTemplate);
                    count++;
                } else {
                    falseAddList.add(addProcessTemplateDTO);
                }
            }
            if (count == listAddProcessTemplateDTO.size()) {
                return Result.success("添加成功！");
            } else {
                HashMap map = new HashMap();
                map.put("success", successAddList);
                map.put("false", falseAddList);
                return Result.success(map, "部分步骤添加失败！");
            }
        } catch (Exception e) {
            log.error("新增流程预设模板异常，", e);
            return Result.failure("系统异常：新增流程预设模板失败！");
        }
    }

    @Override
    public Result updateProcessTemplate(UpdateProcessTemplateDTO updateProcessTemplateDTO) {
        try {
            ProcessTemplate processTemplate = new ProcessTemplate();
            BeanUtil.copyProperties(updateProcessTemplateDTO, processTemplate);
            int update = processTemplateMapper.updateById(processTemplate);
            return update > 0 ? Result.success("更新成功") : Result.failure("更新失败");
        } catch (Exception e) {
            log.error("更新流程预设模板异常,", e);
            return Result.failure("系统异常：更新流程预设模板失败！");
        }
    }

    /**
     * @param id
     * @return
     */
    @Override
    public ProcessTemplate getProcessTemplateById(Integer id) {
        return processTemplateMapper.selectById(id);
    }


    @Override
    public List<ProcessTemplate> getProcessTemplateListByProcessCodeAndWarhouseId(String processCode, String warehosueId, Integer templateType, Integer deptId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("process_code", processCode);
        queryWrapper.eq("warehouse_id", warehosueId);
        queryWrapper.eq(null != templateType, "template_type", templateType);
        queryWrapper.orderByAsc("step");
        List<ProcessTemplate> processTemplateList = processTemplateMapper.selectList(queryWrapper);
        processTemplateList.forEach(p -> {
            // 模版用户为角色  根据角色和部门 id 查询用户
            if (p.getUserType() == 2) {
                List<User> users = userMapper.selectListByRoleIdsAndDeptId(p.getLoginName(), deptId);
                p.setLoginName(users.stream().map(User::getLoginName).collect(Collectors.joining(",")));
            }
        });
        return processTemplateList;
    }
}
