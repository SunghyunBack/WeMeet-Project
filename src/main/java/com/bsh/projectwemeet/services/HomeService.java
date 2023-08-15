package com.bsh.projectwemeet.services;

import com.bsh.projectwemeet.entities.ParticipantsEntity;
import com.bsh.projectwemeet.entities.ProfileEntity;
import com.bsh.projectwemeet.entities.UserEntity;
import com.bsh.projectwemeet.mappers.HomeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Objects;

@Service
public class HomeService {
    public final HomeMapper homeMapper;

    @Autowired
    public HomeService(HomeMapper homeMapper){
        this.homeMapper = homeMapper;
    }

    public ProfileEntity selectLoginProfile(HttpSession session){
        UserEntity loginUser = (UserEntity) session.getAttribute("user");
        return  this.homeMapper.selectLoginProfile(loginUser.getEmail());
    }

//    public ProfileEntity profile1(int index){
//        ParticipantsEntity participants = articleMapper.selectParticipantsArticle1(index);
//
//        if(participants == null){
//            return null;
//        }
//
//        ProfileEntity profile = articleMapper.selectProfile(participants.getEmail());
//
//        return profile;
//    }
//
//    public ProfileEntity profile2(int index){
//        ParticipantsEntity participants1 = articleMapper.selectParticipantsArticle1(index);
//        ParticipantsEntity participants2 = articleMapper.selectParticipantsArticle2(index);
//
//        if(participants1 == null){
//            return null;
//        }
//
//        if(Objects.equals(participants1.getEmail(), participants2.getEmail())){
//            return null;
//        }
//
//        ProfileEntity profile = articleMapper.selectProfile(participants2.getEmail());
//
//        return profile;
//    }

}
