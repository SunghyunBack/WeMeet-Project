package com.bsh.projectwemeet.mappers;

import com.bsh.projectwemeet.entities.EventEntity;
import com.bsh.projectwemeet.entities.EventImagesEntity;
import com.bsh.projectwemeet.entities.FaqEntity;
import com.bsh.projectwemeet.entities.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FaqMapper {

    UserEntity selectCheckUser(@Param(value = "email")String email);
    //관리자인지 확인하는 쿼리

    int insertArticle(FaqEntity faq);

    FaqEntity[] selectCountArticle();

    FaqEntity selectArticleByIndex(@Param(value = "index") int index);

    FaqEntity selectArticleByPatchIndex(@Param(value = "index") int index);

    int deleteArticleByIndex(@Param(value = "index") int index);

    int updateArticleContent(FaqEntity faq);
}
