package com.bsh.projectwemeet.mappers;

import com.bsh.projectwemeet.entities.BulletinEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BulletinMapper {

    int insertComment(BulletinEntity bulletinEntity);
}
