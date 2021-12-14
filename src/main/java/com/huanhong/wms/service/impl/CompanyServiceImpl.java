package com.huanhong.wms.service.impl;

import com.huanhong.wms.SuperServiceImpl;
import com.huanhong.wms.entity.Company;
import com.huanhong.wms.mapper.CompanyMapper;
import com.huanhong.wms.service.ICompanyService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 门店表 服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2021-12-14
 */
@Service
public class CompanyServiceImpl extends SuperServiceImpl<CompanyMapper, Company> implements ICompanyService {

}
