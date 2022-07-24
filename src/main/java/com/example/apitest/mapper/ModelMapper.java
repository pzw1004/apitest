package com.example.apitest.mapper;

import com.example.apitest.Dao.Model;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ModelMapper {

    @Select("SELECT m.mAP FROM model m ORDER BY m.mAP DESC LIMIT 0,1")
    Double getBestMAP();

    @Insert("INSERT INTO model(path,date_time,mAP)VALUES (#{modelPath},#{date},#{mAP})")
    void saveModel(Model model);
}
