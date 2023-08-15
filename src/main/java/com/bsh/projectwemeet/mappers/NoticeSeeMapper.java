package com.bsh.projectwemeet.mappers;

import com.bsh.projectwemeet.entities.NoticeWriterArticleEntity;
import com.bsh.projectwemeet.entities.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface NoticeSeeMapper {

    UserEntity selectCheckUser(@Param(value = "email")String email);

    NoticeWriterArticleEntity[] selectCountArticle();

}
