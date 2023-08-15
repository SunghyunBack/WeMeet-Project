package com.bsh.projectwemeet.mappers;

import com.bsh.projectwemeet.entities.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface EventMapper {

    UserEntity selectCheckUser(@Param(value = "email")String email);
    //관리자인지 확인하는 쿼리

    int insertArticle(EventEntity event);

    int insertImage(EventImagesEntity eventImages);

    EventEntity[] selectCountArticle();

    EventEntity selectArticleByIndex(@Param(value = "index") int index);

    EventEntity selectArticleByPatchIndex(@Param(value = "index") int index);

    EventImagesEntity selectImage(@Param(value = "index") int index);


    int deleteArticleByIndex(@Param(value = "index") int index);

    int updateArticleContent(EventEntity event);

}
