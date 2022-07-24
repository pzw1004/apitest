package com.example.apitest.mapper;

import com.example.apitest.Dao.DamageData;
import com.example.apitest.Dao.DamageImage;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @Author 宋宗垚
 * @Date 2019/7/20 23:15
 * @Description TODO
 */
@Mapper
public interface DamageImageMapper {


    @Select("SELECT * FROM picture WHERE picture_id=#{id}")
    @Results({
            @Result(property = "id",column = "picture_id"),
            @Result(property = "sourceImagePath",column = "picture_dir"),
            @Result(property = "transferImagePath",column = "picture_transpath"),
            @Result(property = "width",column = "picture_width"),
            @Result(property = "height",column = "picture_height"),
            @Result(property = "damageDataList",column = "picture_id",
            many = @Many(select = "com.example.apitest.mapper.DamageMapper.findDamageListByImageId"))
    })
    DamageImage findDamageImageById(Integer id);

    @Select("SELECT DISTINCT retangle_picture_id FROM retangle")
    List<Integer> findAllDamageImageId();

    @Select("SELECT COUNT(DISTINCT retangle_picture_id) FROM retangle")
    Integer countDamageImage();
}
