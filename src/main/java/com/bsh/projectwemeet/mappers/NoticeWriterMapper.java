package com.bsh.projectwemeet.mappers;


import com.bsh.projectwemeet.entities.NoticeWriterArticleEntity;
import com.bsh.projectwemeet.entities.NoticeWriterImagesEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface NoticeWriterMapper {

    int insertArticle(NoticeWriterArticleEntity article);

    int insertImage(NoticeWriterImagesEntity image);

    NoticeWriterArticleEntity selectArticleByIndex(@Param(value = "index") int index);

    NoticeWriterArticleEntity selectArticleByPatchIndex(@Param(value = "index") int index);

    NoticeWriterImagesEntity selectImage(@Param(value = "index") int index);


    int deleteArticleByIndex(@Param(value = "index") int index);

    int updateArticleContent(NoticeWriterArticleEntity noticeWriterArticle);
}
