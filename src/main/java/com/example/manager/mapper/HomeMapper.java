package com.example.manager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.manager.entity.Home;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface HomeMapper extends BaseMapper<Home> {
    void createHome(@Param("name") String name, @Param("address") String address);

    List<Long> selectHomeIdByName(@Param("name") String name);

    Long getLastInsertId();

    Home selectByHomeId(@Param("id") Long id);

    void deleteHomeById(@Param("id") Long id);

    List<Home> selectByHomeIds(@Param("homeIds") List<Long> homeIds);

    String selectHomeNameById(@Param("id") Long id);

    int updateHome(Long homeId, String name, String address);

    String selectAddressById(@Param("id") Long id);

    int updateHomeName(Long homeId, String name);

    int updateHomeAddress(Long homeId, String address);

    List<Home> searchHomeByPartName(@Param("name") String name);

    Home selectById(@Param("id") Long id);
}