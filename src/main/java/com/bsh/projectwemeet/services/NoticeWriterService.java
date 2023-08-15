package com.bsh.projectwemeet.services;

import com.bsh.projectwemeet.entities.NoticeWriterArticleEntity;
import com.bsh.projectwemeet.entities.NoticeWriterImagesEntity;
import com.bsh.projectwemeet.entities.UserEntity;
import com.bsh.projectwemeet.enums.PatchNoticeViewResult;
import com.bsh.projectwemeet.mappers.NoticeWriterMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;

@Service
public class NoticeWriterService {

    private final NoticeWriterMapper noticeWriterMapper;

    @Autowired
    public NoticeWriterService(NoticeWriterMapper noticeWriterMapper) {
        this.noticeWriterMapper = noticeWriterMapper;
    }

    public boolean putNoticeWriter(HttpServletRequest request, NoticeWriterArticleEntity noticeWriterArticle) {
        noticeWriterArticle.setCreatedAt(new Date())
                .setClientIp(request.getRemoteAddr())
                .setClientUa(request.getHeader("User-Agent"));
        return this.noticeWriterMapper.insertArticle(noticeWriterArticle) > 0
                ? true
                : false;
    }

    // 내용에 이미지 파일이 있을때
    public NoticeWriterArticleEntity readArticle(int index) {
        NoticeWriterArticleEntity article = this.noticeWriterMapper.selectArticleByIndex(index);
        return article;
    }

    //    ckeditor에 그림 넣기
    public NoticeWriterImagesEntity putImage(HttpServletRequest request, MultipartFile file) throws IOException {
        NoticeWriterImagesEntity image = new NoticeWriterImagesEntity()
                .setCreatedAt(new Date())
                .setClientIp(request.getRemoteAddr())
                .setClientUa(request.getHeader("User-Agent"))
                .setName(file.getOriginalFilename())
                .setSize((file.getSize()))
                .setContentType(file.getContentType())
                .setData(file.getBytes());
        this.noticeWriterMapper.insertImage(image);
        return image;

    }

    //    ckeditor에 넣은 그림을 표시
    public NoticeWriterImagesEntity getImage(int index) {
        return this.noticeWriterMapper.selectImage(index);
    }


    public boolean deleteNoticeView(int index, HttpSession session) {
        UserEntity loginUser = (UserEntity) session.getAttribute("user");
        if (loginUser.isAdmin() != true) {
            return false; //사용자가 관리자가 아니라면
        }
        return this.noticeWriterMapper.deleteArticleByIndex(index) > 0;
    }

    //    수정한다면 원래 있던 내용들을 그대로 표시하기
    public NoticeWriterArticleEntity getPatchIndexArticle(int index, HttpSession session) {
        UserEntity loginUser = (UserEntity) session.getAttribute("user");
        if (loginUser.isAdmin() != true) {
            return null; //사용자가 관리자가 아니라면
        }
        return this.noticeWriterMapper.selectArticleByPatchIndex(index);
    }

    public PatchNoticeViewResult UpdateArticle(NoticeWriterArticleEntity noticeWriterArticle, HttpSession session, HttpServletRequest request) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        if (user == null) {
            return PatchNoticeViewResult.FAILURE;
        }
        noticeWriterArticle.setClientIp(request.getRemoteAddr())
                .setClientUa(request.getHeader("User-Agent"))
                .setCreatedAt(new Date());
        return this.noticeWriterMapper.updateArticleContent(noticeWriterArticle) > 0
                ? PatchNoticeViewResult.SUCCESS
                : PatchNoticeViewResult.FAILURE;
    }


}
