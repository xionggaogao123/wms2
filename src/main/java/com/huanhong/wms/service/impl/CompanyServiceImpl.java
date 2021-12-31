package com.huanhong.wms.service.impl;

import cn.hutool.core.util.StrUtil;
import com.huanhong.wms.SuperServiceImpl;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.Company;
import com.huanhong.wms.entity.Dept;
import com.huanhong.wms.entity.User;
import com.huanhong.wms.mapper.CompanyMapper;
import com.huanhong.wms.mapper.DeptMapper;
import com.huanhong.wms.mapper.UserMapper;
import com.huanhong.wms.service.ICompanyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * <p>
 * 公司表 服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2021-12-14
 */
@Service
public class CompanyServiceImpl extends SuperServiceImpl<CompanyMapper, Company> implements ICompanyService {

    @Resource
    private DeptMapper deptMapper;
    @Resource
    private UserMapper userMapper;

    @Transactional
    @Override
    public Result<Integer> addCompany(Company company) {
        Company parentCompany = this.baseMapper.selectById(company.getParentId());
        if (parentCompany == null) {
            return Result.failure("父公司不存在或已删除");
        } else if (parentCompany.getLevel() > 2) {
            return Result.failure("已无法创建子公司");
        }
        company.setLevel(parentCompany.getLevel() + 1);
        int insert = this.baseMapper.insert(company);
        if (insert > 0) {
            Dept parentCompanyDept = deptMapper.getParentCompanyDept(parentCompany.getId());
            Dept dept = new Dept();
            dept.setName(company.getName());
            dept.setLevel(parentCompanyDept.getLevel() + 1);
            dept.setParentId(parentCompanyDept.getParentId());
            dept.setSort(1);
            dept.setIsCompany(1);
            dept.setParentCompanyId(parentCompany.getParentId());
            dept.setCompanyId(company.getId());

            deptMapper.insert(dept);

            User user = new User();
            user.setLoginName(company.getAccount());
            if (StrUtil.isNotEmpty(company.getContact())) {
                user.setUserName(company.getContact());
            } else {
                user.setUserName("公司管理员");
            }
            user.setPhoneNumber(company.getTelephone());
            user.setCompanyName(company.getName());
            user.setDeptId(dept.getId());
            user.setDeptName(dept.getName());
            user.setRemark("生成管理员账号");
            user.setParentCompanyId(company.getParentId());
            user.setCompanyId(company.getId());

            userMapper.insert(user);
        }
        return Result.success(company.getId());
    }

    @Override
    public Result<Integer> deleteCompany(Integer id) {
        int delete = this.baseMapper.deleteById(id);

        if (delete > 0) {
            deptMapper.deleteByCompanyId(id);
        }

        return delete > 0 ? Result.success() : Result.failure("删除失败");
    }

    @Override
    public Company getCompanyById(Integer id) {
        Company Company = this.baseMapper.selectById(id);
        return Company;
    }


}
