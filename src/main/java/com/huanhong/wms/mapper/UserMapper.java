package com.huanhong.wms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huanhong.wms.entity.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;


@Repository
public interface UserMapper extends BaseMapper<User> {

    @Select("select * from `user` where del = 0 and login_name = #{account}")
    User getUserByAccount(String account);

    @Update("update `user` set wx_open_id = null where id = #{id}")
    int delOpenIdById(@Param("id") Integer id);

    @Select("select *  from `user` where del = 0 and wx_open_id = #{openid}")
    User getByWxOpenId(String openid);
}
