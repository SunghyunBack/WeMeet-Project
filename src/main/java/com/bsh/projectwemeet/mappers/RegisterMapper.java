package com.bsh.projectwemeet.mappers;

import com.bsh.projectwemeet.entities.RegisterContactCodeEntity;
import com.bsh.projectwemeet.entities.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RegisterMapper {
    UserEntity selectUserByContact (@Param(value = "contact")String contact);
    UserEntity selectUserByEmail (@Param(value = "email")String email);
    UserEntity selectUserByNickname (@Param(value = "nickname")String nickname);

    RegisterContactCodeEntity selectRegisterContactCodeByContactSalt(RegisterContactCodeEntity registerContactCode);


    int insertRegisterContactCode(RegisterContactCodeEntity registerContactCode);
    int insertUser(UserEntity user);


    int updateRegisterCode(RegisterContactCodeEntity registerContactCode);
}
