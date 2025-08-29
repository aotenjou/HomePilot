package com.example.manager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.manager.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    String selectPasswordByPhone(@Param("phone") String phone);

    String selectPhone(@Param("phone") String phone);

    void createUser(@Param("user") User user);

    Long getLastInsertId();

    Long selectUserIdByPhone(@Param("phone") String phone);

    List<User> selectByUserId(@Param("userId") List<Long> userId);

    User selectByPhone(String phone);
}
