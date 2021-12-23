package com.huanhong.wms.service;

import com.huanhong.wms.SuperService;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.Company;

/**
 * <p>
 * 公司表 服务类
 * </p>
 *
 * @author liudeyi
 * @since 2021-12-14
 */
public interface ICompanyService extends SuperService<Company> {

    Result<Integer> addCompany(Company company);


    Result<Integer> deleteCompany(Integer id);

}
