package com.bsh.projectwemeet.mappers;

import com.bsh.projectwemeet.entities.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LoginMapper {
//  로그인의 경우 데이터베이스에  select로 조회한다.
    UserEntity selectUserByEmail(@Param(value = "email") String email);
}
