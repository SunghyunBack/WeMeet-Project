package com.bsh.projectwemeet.mappers;

import com.bsh.projectwemeet.entities.RecoverEmailCodeEntity;
import com.bsh.projectwemeet.entities.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CheckMapper {
    int insertRecoverEmailCode(RecoverEmailCodeEntity recoverEmailCode);
    RecoverEmailCodeEntity selectRecoverEmailCodeByEmailCodeSalt(RecoverEmailCodeEntity recoverEmailCode);

    UserEntity selectUserByEmail(@Param(value = "email") String email);
    int updateRecoverEmailCode(RecoverEmailCodeEntity recoverEmailCode);

    int updateUser(UserEntity user);

    int deleteRecoverEmailCode(RecoverEmailCodeEntity recoverEmailCode);

}
