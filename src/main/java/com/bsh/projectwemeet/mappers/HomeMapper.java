package com.bsh.projectwemeet.mappers;

import com.bsh.projectwemeet.entities.ProfileEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface HomeMapper {
    ProfileEntity selectLoginProfile(@Param(value="email")String email);

}
