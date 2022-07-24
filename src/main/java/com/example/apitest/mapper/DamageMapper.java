package com.example.apitest.mapper;

import com.example.apitest.Dao.DamageData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Author 宋宗垚
 * @Date 2019/7/20 23:03
 * @Description 用于查找损伤信息的mybatis文件
 */
@Mapper
public interface DamageMapper {

    @Select("SELECT retangle_x1,retangle_y1,retangle_x2,retangle_y2,retangle_damage_type FROM retangle r WHERE r.retangle_picture_id=#{imageId}")
    @Results({
            @Result(property = "x_min",column = "retangle_x1"),
            @Result(property = "y_min",column = "retangle_y1"),
            @Result(property = "x_max",column = "retangle_x2"),
            @Result(property = "y_max",column = "retangle_y2"),
            @Result(property = "damageType",column = "retangle_damage_type")
    })
    List<DamageData> findDamageListByImageId(Integer imageId);


}
