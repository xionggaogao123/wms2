package com.huanhong.wms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.SuperService;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.ProcessTemplate;
import com.huanhong.wms.entity.dto.AddProcessTemplateDTO;
import com.huanhong.wms.entity.dto.UpdateProcessTemplateDTO;
import com.huanhong.wms.entity.vo.ProcessTemplateVO;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author liudeyi
 * @since 2022-03-04
 */
public interface IProcessTemplateService extends SuperService<ProcessTemplate> {

    /**
     * 分页查询
     * @param processTemplatePage
     * @param processTemplateVO
     * @return
     */
    Page<ProcessTemplate> pageFuzzyQuery(Page<ProcessTemplate> processTemplatePage, ProcessTemplateVO processTemplateVO);


    /**
     * 新增流程预设
     *
     * @param listAddProcessTemplateDTO
     * @return
     */
    Result addProcessTemplate(List<AddProcessTemplateDTO> listAddProcessTemplateDTO);


    /**
     * 更新流程预设
     *
     * @param updateProcessTemplateDTO
     * @return
     */
    Result updateProcessTemplate(UpdateProcessTemplateDTO updateProcessTemplateDTO);


    /**
     * 根据id获取流程预设
     *
     * @param id
     * @return
     */
    ProcessTemplate getProcessTemplateById(Integer id);


    /**
     * 根据流程代码和仓库名获取本仓库某流程的完整List
     */

    List<ProcessTemplate> getProcessTemplateListByProcessCodeAndWarhouseId(String processCode, String warehosueId);

}
