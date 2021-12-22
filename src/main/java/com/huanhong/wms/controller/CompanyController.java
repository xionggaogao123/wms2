package com.huanhong.wms.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.bean.LoginUser;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.Company;
import com.huanhong.wms.entity.Dept;
import com.huanhong.wms.entity.User;
import com.huanhong.wms.entity.dto.AddCompanyDTO;
import com.huanhong.wms.mapper.CompanyMapper;
import com.huanhong.wms.mapper.DeptMapper;
import com.huanhong.wms.mapper.UserMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/v1/company")
@ApiSort(23)
@Api(tags = "公司管理 🏦")
public class CompanyController extends BaseController {

    @Resource
    private CompanyMapper companyMapper;
    @Resource
    private DeptMapper deptMapper;
    @Resource
    private UserMapper userMapper;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页码"),
            @ApiImplicitParam(name = "size", value = "每页行数"),
            @ApiImplicitParam(name = "search", value = "聚合搜索"),
    })
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "分页查询公司表")
    @GetMapping("/page")
    public Result<Page<Company>> page(@RequestParam(defaultValue = "1") Integer current, @RequestParam(defaultValue = "10") Integer size,
                                      @RequestParam Map<String, Object> search) {
        QueryWrapper<Company> query = new QueryWrapper<>();
        query.orderByDesc("id");
        if (search.containsKey("search")) {
            String text = search.get("search").toString();
            if (StrUtil.isNotEmpty(text)) {
                query.and(qw -> qw.like("name", text).or()
                        .like("full_name", text).or()
                        .like("contact", text)
                );
            }
        }
        return Result.success(companyMapper.selectPage(new Page<>(current, size), query));
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "添加公司")
    @PostMapping
    public Result add(@Valid @RequestBody AddCompanyDTO dto) {
        LoginUser loginUser = this.getLoginUser();

        Company company = new Company();
        BeanUtil.copyProperties(dto, company);

        Company parentCompany = companyMapper.selectById(loginUser.getCompanyId());

        company.setParentId(parentCompany.getParentId());
        company.setLevel(parentCompany.getLevel() + 1);
        int insert = companyMapper.insert(company);
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
            user.setUserName(company.getContact());
            user.setPhoneNumber(company.getTelephone());
            user.setCompanyName(company.getName());
            user.setDeptId(dept.getId());
            user.setDeptName(dept.getName());
            user.setRemark("生成管理员账号");
            user.setParentCompanyId(company.getParentId());
            user.setCompanyId(company.getId());

            userMapper.insert(user);
        }
        return render(insert > 0);
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "更新公司表", notes = "生成代码")
    @PutMapping
    public Result update(@Valid @RequestBody Company company) {
        int update = companyMapper.updateById(company);
        return render(update > 0);
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "删除公司表", notes = "生成代码")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        int i = companyMapper.deleteById(id);
        return render(i > 0);
    }


}

