package com.bsh.projectwemeet.mappers;

import com.bsh.projectwemeet.entities.ArticleEntity;
import com.bsh.projectwemeet.entities.ParticipantsEntity;
import com.bsh.projectwemeet.entities.UserEntity;
import com.bsh.projectwemeet.entities.*;
import com.bsh.projectwemeet.models.PagingModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ArticleMapper {

    int insertArticle(ArticleEntity articles);

    int insertParticipants(ParticipantsEntity participants);

    int selectCountCategory(@Param(value = "searchCriterion") String searchCriterion,
                            @Param(value = "searchQuery") String searchQuery,
                            @Param(value = "category") String category);
    //param을 이용해 관련 카테고리만 조회해서 가져온다

    int insertLike(LikeReportEntity likeEntity);

    int insertReport(LikeReportEntity likeEntity);


    ArticleEntity[] selectCountCategoryByPage(@Param(value = "pagingModel") PagingModel pagingModel,
                                              @Param(value = "searchCriterion") String searchCriterion,
                                              @Param(value = "searchQuery") String searchQuery,
                                              @Param(value = "category") String category);
    //카테고리 관련 페이징을 위한 매퍼


    ArticleEntity[] selectArticleMain(@Param(value = "isFinished")boolean isFinished);
    //메인에 게시물 6개 나타내기 위한 매퍼

    ArticleEntity[] selectDifferentArticle();
    //이런만남은 어때요?를 나타내기 위한 매퍼

    ArticleEntity selectArticleByIndex(@Param(value = "index") int index);
    //클릭시 해당게시물을 가기위해 index값으로 찾기위해 사용되는 매퍼


    ArticleEntity selectArticleProfileByIndex(@Param(value = "index") int index);


    ArticleEntity selectParticipantProfileByIndex(@Param(value = "index") int index);

    ArticleEntity selectParticipantProfileByIndexTwo(@Param(value = "index") int index);


    ArticleEntity selectArticleByPatchIndex(@Param(value = "index") int index);
    //게시물 수정 인덱스 찾기

    ParticipantsEntity selectParticipants(@Param(value = "ArticleIndex") int ArticleIndex);
    // 참여인원 중복되있는지 select

    ParticipantsEntity selectParticipantsArticle1(@Param(value = "ArticleIndex") int ArticleIndex);
    // 프로필사진을 위해 참여인원 구하기

    ParticipantsEntity selectParticipantsArticle2(@Param(value = "ArticleIndex") int ArticleIndex);
    // 프로필사진을 위해 참여인원 구하기


    ParticipantsEntity selectCheckParticipants(@Param(value = "ArticleIndex") int ArticleIndex,
                                               @Param(value = "email") String email);


    LikeReportEntity selectLike(@Param(value = "ArticleIndex") int ArticleIndex,
                                @Param(value = "email") String email,
                                @Param(value = "likeFlag") boolean likeFlag);

    LikeReportEntity selectReport(@Param(value = "ArticleIndex") int ArticleIndex,
                                @Param(value = "email") String email,
                                @Param(value = "reportFlag") boolean reportFlag);


    ProfileEntity selectProfile(@Param(value = "email")String email);


    ParticipantsEntity[] selectParticipantsProfile(@Param(value = "index") int index);
    // 참여인원 중복되있는지 select

    ProfileEntity[] selectParticipantsProfiless(@Param(value = "ArticleIndex") int ArticleIndex);
    // 참여인원 중복되있는지 select


    ParticipantsEntity[] selectParticipantsProfiles(@Param(value = "ArticleIndex") int ArticleIndex,
                                                   @Param(value = "email")String email);
    // 참여인원 중복되있는지 select




    int updateLike(ArticleEntity article);
    //좋아요를 업데이트 하기위한 매퍼

    int updateReport(ArticleEntity article);
    //신고를 업데이트 하기위한 매퍼

    int updateArticle(ArticleEntity article);
    //조회수 수정

    int updateArticleContent(ArticleEntity article);
    //게시판 수정

    int updateParticipate(ArticleEntity article);
    //참여자의 업데이트를 위한 매퍼

    int deleteByArticle(@Param(value = "index")int index);
    //게시물의 인덱스로 삭제하기 위해 사용되는 매퍼

    int deleteByParticipants(@Param(value = "index")int index);
    //참여취소를 위해 Delete하기 위한 매퍼

    int deleteByLike(@Param(value = "articleIndex")int articleIndex,
                     @Param(value = "email")String email,
                     @Param(value = "likeFlag")boolean likeFlag);

    int deleteByReport(@Param(value = "articleIndex")int articleIndex,
                     @Param(value = "email")String email,
                     @Param(value = "reportFlag")boolean reportFlag);




    ArticleEntity selectArticleByIndexEmail(@Param(value="index") int index);
    ArticleEntity[] selectArticleBylimitPeople(@Param(value="index") int index);

    int updateFinished(ArticleEntity article);
//    댓글
    CommentEntity[] selectCommentByArticleIndex(@Param(value = "articleIndex")int articleIndex);
    CommentEntity selectComment(@Param(value = "index") int index);


    int insertComment(CommentEntity comment);

    int updateComment(CommentEntity comment);

    UserEntity selectUser(@Param(value = "email") String email);


    //    완료 페이지로 넘기기
    ArticleEntity selectArticleByCompleteIndex(@Param(value = "index") int index);
    int updateCategory(ArticleEntity article);
    ArticleEntity[] selectCategory(@Param(value = "category") String category);
    ArticleEntity selectUpdateCategoryByIndex(@Param(value="index")int index);



    ArticleEntity selectasd(@Param(value="email")String email);

}
