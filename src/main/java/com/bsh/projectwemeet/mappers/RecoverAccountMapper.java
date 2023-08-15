package com.bsh.projectwemeet.mappers;

import com.bsh.projectwemeet.entities.RecoverContactCodeEntity;
import com.bsh.projectwemeet.entities.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RecoverAccountMapper {

    int insertRecoverContactCode(RecoverContactCodeEntity recoverContactCode);

    UserEntity selectUserByContactName(@Param(value = "contact") String contact,
                                       @Param(value = "name") String name);

    RecoverContactCodeEntity selectRecoverContactCodeByCodeByContactCodeSalt(RecoverContactCodeEntity recoverContactCode);

    int updateRecoverContactCode(RecoverContactCodeEntity recoverContactCode);

//    UserEntity selectUserByContact(@Param(value = "contact") String contact);

}
