package com.huanhong.wms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huanhong.wms.entity.Oss;
import com.huanhong.wms.entity.vo.OssVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 * 对象存储表 Mapper 接口
 * </p>
 *
 * @author liudeyi
 * @since 2019-12-14
 */
public interface OssMapper extends BaseMapper<Oss> {


    @Select("select url from oss where md5 = #{md5} limit 1")
    String checkMD5(String md5);

    @Select("select url from oss where user_id = #{userId} and object_type = 'face' order by id desc limit 1")
    String getUserFaceURL(Integer userId);

    @Select("select id, name, size, md5, type, oss_host(url) as url, state, gmt_create from oss where del = 0 and object_id = #{objectId} and object_type = #{type}")
    List<OssVo> getOssByObjectId(Integer objectId, String type);

    /**
     * 更新资源对象的绑定对象
     *
     * @param objcetId 对象的Id
     * @param id       资源ID
     * @return int
     */
    @Update("update oss set object_id = #{objectId} where id = #{id}")
    int updateObjectIdById(@Param("objectId") Integer objectId, @Param("id") Integer id);


    @Update("update oss set object_id = #{objectId} where id in (${ids})")
    int updateObjectIdByIds(@Param("objectId") Integer objectId, @Param("ids") String ids);
}
