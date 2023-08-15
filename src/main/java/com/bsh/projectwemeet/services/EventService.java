package com.bsh.projectwemeet.services;

import com.bsh.projectwemeet.entities.*;
import com.bsh.projectwemeet.enums.PatchNoticeViewResult;
import com.bsh.projectwemeet.mappers.EventMapper;
import com.bsh.projectwemeet.mappers.NoticeSeeMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;

@Service
public class EventService {
    private final EventMapper eventMapper;

    public EventService(EventMapper eventMapper) {
        this.eventMapper = eventMapper;
    }

    // 로그인의 여부
    public UserEntity CheckUser(HttpSession session) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        if (user == null) {
            return null;
        }

        UserEntity check = eventMapper.selectCheckUser(user.getEmail());
        return check;
    }

    public boolean putEventWriter(HttpServletRequest request, EventEntity event) {
        event.setCreatedAt(new Date())
                .setClientIp(request.getRemoteAddr())
                .setClientUa(request.getHeader("User-Agent"));
        return this.eventMapper.insertArticle(event) > 0
                ? true
                : false;
    }

    public EventEntity[] getCountArticle() {
        return this.eventMapper.selectCountArticle();
    }

    public EventEntity readArticle(int index) {
        EventEntity article = this.eventMapper.selectArticleByIndex(index);
        return article;
    }

    public EventImagesEntity putImage(HttpServletRequest request, MultipartFile file) throws IOException {
        EventImagesEntity image = new EventImagesEntity()
                .setCreatedAt(new Date())
                .setClientIp(request.getRemoteAddr())
                .setClientUa(request.getHeader("User-Agent"))
                .setName(file.getOriginalFilename())
                .setSize((file.getSize()))
                .setContentType(file.getContentType())
                .setData(file.getBytes());
        this.eventMapper.insertImage(image);
        return image;

    }

    //    ckeditor에 넣은 그림을 표시
    public EventImagesEntity getImage(int index) {
        return this.eventMapper.selectImage(index);
    }


    public boolean deleteNoticeView(int index, HttpSession session) {
        UserEntity loginUser = (UserEntity) session.getAttribute("user");
        if (loginUser.isAdmin() != true) {
            return false; //사용자가 관리자가 아니라면
        }
        return this.eventMapper.deleteArticleByIndex(index) > 0;
    }

    //    수정한다면 원래 있던 내용들을 그대로 표시하기
    public EventEntity getPatchIndexArticle(int index, HttpSession session) {
        UserEntity loginUser = (UserEntity) session.getAttribute("user");
        if (loginUser.isAdmin() != true) {
            return null; //사용자가 관리자가 아니라면
        }
        return this.eventMapper.selectArticleByPatchIndex(index);
    }

    public PatchNoticeViewResult UpdateArticle(EventEntity event, HttpSession session, HttpServletRequest request) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        if (user == null) {
            return PatchNoticeViewResult.FAILURE;
        }
        event.setClientIp(request.getRemoteAddr())
                .setClientUa(request.getHeader("User-Agent"))
                .setCreatedAt(new Date());
        return this.eventMapper.updateArticleContent(event) > 0
                ? PatchNoticeViewResult.SUCCESS
                : PatchNoticeViewResult.FAILURE;
    }


}
