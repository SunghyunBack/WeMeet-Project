package com.bsh.projectwemeet.services;

import com.bsh.projectwemeet.entities.*;
import com.bsh.projectwemeet.enums.ReviewResult;
import com.bsh.projectwemeet.mappers.ArticleMapper;
import com.bsh.projectwemeet.mappers.ReviewMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.Objects;

@Service
public class ReviewService {
    private final ReviewMapper reviewMapper;
    private final ArticleMapper articleMapper;

    @Autowired
    public ReviewService(ReviewMapper reviewMapper, ArticleMapper articleMapper) {
        this.reviewMapper = reviewMapper;
        this.articleMapper = articleMapper;
    }

// 리뷰 insert
    public ReviewResult reviewWrite(HttpServletRequest request, ReviewEntity reviewEntity, HttpSession session) {

        UserEntity loginUser = (UserEntity) session.getAttribute("user");
//       게시물의 작성자 & 참여자가 아닐경우
        if (1 != reviewMapper.selectParticipant(reviewEntity.getArticleIndex(), loginUser.getEmail()) &&
                1 != reviewMapper.selectArticleWriter(reviewEntity.getArticleIndex(), loginUser.getEmail())) {
            return ReviewResult.FAILURE_EXCEPTION;
        }
        reviewEntity.setCreatedAt(new Date())
                .setClientIp(request.getRemoteAddr())
                .setClientUa(request.getHeader("User-Agent"));
        return this.reviewMapper.insertReview(reviewEntity) > 0
                ? ReviewResult.SUCCESS
                : ReviewResult.FAILURE;
    }

// 데이터 베이스의 리뷰 select
    public ReviewEntity[] getAll(int index) {
        return this.reviewMapper.selectAll(index);
    }

    // 리뷰 평점 계산
    public Double avgStar(int articleIndex) {
        Double averageScore = reviewMapper.avgStar(articleIndex);
        if (averageScore != null) {
            return Math.round(averageScore * 10.0) / 10.0;
        }
        return null;
    }


    //    리뷰 작성자 프로필 select
    public ProfileEntity[] readReviewProfile(int index,String email) {
        ReviewEntity[] reviews = this.reviewMapper.selectIndexByEmail(index,email);
        // 프로필들을 저장할 비어있는 ArrayList 생성
        ProfileEntity[] profileList = new ProfileEntity[reviews.length];

        // participants 배열을 순회하면서 각 참여자의 프로필을 가져와서 profileList에 추가
        for (int i = 0; i < reviews.length; i++) {
            ReviewEntity participant = reviews[i];
            ProfileEntity profile = articleMapper.selectProfile(participant.getEmail());
            // 프로필을 profileList에 추가
            profileList[i] = profile;
        }

        // 프로필 배열 반환
        return profileList;
    } //이미지 추가

    // 리뷰 삭제
    public boolean deleteByIndex(int index) {
        return this.reviewMapper.deleteByReview(index) > 0;
    }



}
