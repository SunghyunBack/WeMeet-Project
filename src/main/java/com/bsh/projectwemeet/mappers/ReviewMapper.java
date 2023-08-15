package com.bsh.projectwemeet.mappers;

import com.bsh.projectwemeet.entities.ArticleEntity;
import com.bsh.projectwemeet.entities.ParticipantsEntity;
import com.bsh.projectwemeet.entities.ProfileEntity;
import com.bsh.projectwemeet.entities.ReviewEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ReviewMapper {
    int insertReview(ReviewEntity reviewEntity);

    Double avgStar(@Param(value = "articleIndex") int articleIndex);

    int selectArticleWriter(@Param(value = "index") int index,
                            @Param(value = "email") String email);

    int selectParticipant(@Param(value = "ArticleIndex") int ArticleIndex,
                          @Param(value = "email") String email);

    ReviewEntity[] selectAll(@Param(value = "ArticleIndex")int ArticleIndex);
    //변경

    ReviewEntity[] selectIndexByEmail(@Param(value="ArticleIndex")int ArticleIndex, @Param(value = "email")String email);

    ProfileEntity selectProfileImage (@Param(value="email")String email);

    int deleteByReview(@Param(value = "index") int index);

    ReviewEntity[] selectArticleIndex(@Param(value = "articleIndex") int articleIndex,
                                      @Param(value = "email")String email);

    ReviewEntity selectParticipantsEmail(@Param(value = "email") String email);
    //    참여자의 데이터 베이스에서 이메일과 게시글번호와 같은지에 대한 정보가 필요하다.





}
