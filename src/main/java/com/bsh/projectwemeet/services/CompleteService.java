package com.bsh.projectwemeet.services;

import com.bsh.projectwemeet.entities.ArticleEntity;
import com.bsh.projectwemeet.entities.ParticipantsEntity;
import com.bsh.projectwemeet.entities.ProfileEntity;
import com.bsh.projectwemeet.mappers.CompleteMapper;
import com.bsh.projectwemeet.models.PagingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service

public class CompleteService {

    public final CompleteMapper completeMapper;

    public static final int PAGE_COUNT=6;

    @Autowired
    public CompleteService(CompleteMapper completeMapper) {
        this.completeMapper = completeMapper;
    }


    public int getCountCategory(String searchCriterion, String searchQuery,String category){
        return  this.completeMapper.selectCountCategory(searchCriterion, searchQuery, category);
    }

    public ArticleEntity[] getCountCategoryByPage(PagingModel pagingModel,String searchCriterion, String searchQuery, String category){

        return this.completeMapper.selectCountCategoryByPage(pagingModel,searchCriterion, searchQuery, category);
    }


    public ArticleEntity readArticle(int index) {
        ArticleEntity article = this.completeMapper.selectArticleByIndex(index);
        // 주어진 index를 사용하여 articleMapper에서 해당 index에 해당하는 게시글을 조회합니다.
        if (article != null && !article.isDeleted()) {
            // 게시글이 존재하고 삭제되지 않은 경우에만 처리합니다.
            article.setView(article.getView() + 1);
            // 게시글의 조회수(view)를 1 증가시킵니다.
            this.completeMapper.updateArticle(article);
            // 조회수를 업데이트합니다.
        }
        return article;
        //결과적으로 삭제되지않거나
    } //게시판 나타내기

    public ProfileEntity profileBulletin(int index){
        ArticleEntity article = completeMapper.selectArticleByIndex(index);
        ProfileEntity profile = completeMapper.selectProfile(article.getEmail());
        if(profile == null){
            return null;
        }
        if(Objects.equals(article.getEmail(), profile.getEmail())){
            return completeMapper.selectProfile(article.getEmail());
        }
        return null;
    }


    public ProfileEntity profile1(int index){
        ParticipantsEntity participants1 = completeMapper.selectParticipantsArticle1(index);
        ParticipantsEntity participants2 = completeMapper.selectParticipantsArticle2(index);

        if(participants1 == null){
            return null;
        }
        if(Objects.equals(participants1.getEmail(), participants2.getEmail())) {
            return null;
        }else {
            ProfileEntity profile = completeMapper.selectProfile(participants1.getEmail());
            return profile;
        }
    }

    public ProfileEntity profile2(int index){
        ParticipantsEntity participants1 = completeMapper.selectParticipantsArticle1(index);
        ParticipantsEntity participants2 = completeMapper.selectParticipantsArticle2(index);

        if(participants2 == null){
            return null;
        }
        if(Objects.equals(participants1.getEmail(), participants2.getEmail())){
            return null;
        }else {
            ProfileEntity profile = completeMapper.selectProfile(participants2.getEmail());
            return profile;
        }
    }

}
