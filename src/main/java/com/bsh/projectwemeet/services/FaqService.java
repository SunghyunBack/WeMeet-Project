package com.bsh.projectwemeet.services;

import com.bsh.projectwemeet.entities.FaqEntity;
import com.bsh.projectwemeet.entities.UserEntity;
import com.bsh.projectwemeet.enums.PatchNoticeViewResult;
import com.bsh.projectwemeet.mappers.FaqMapper;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.Date;

@Service
public class FaqService {

    private final FaqMapper faqMapper;

    public FaqService(FaqMapper faqMapper) {
        this.faqMapper = faqMapper;
    }

    // 로그인의 여부
    public UserEntity CheckUser(HttpSession session) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        if (user == null) {
            return null;
        }

        UserEntity check = faqMapper.selectCheckUser(user.getEmail());
        return check;
    }

    public boolean putEventWriter(HttpServletRequest request, FaqEntity faq) {
        faq.setCreatedAt(new Date())
                .setClientIp(request.getRemoteAddr())
                .setClientUa(request.getHeader("User-Agent"));
        return this.faqMapper.insertArticle(faq) > 0
                ? true
                : false;
    }

    public FaqEntity[] getCountArticle() {
        return this.faqMapper.selectCountArticle();
    }

    public FaqEntity readArticle(int index) {
        FaqEntity article = this.faqMapper.selectArticleByIndex(index);
        return article;
    }


    public boolean deleteNoticeView(int index, HttpSession session) {
        UserEntity loginUser = (UserEntity) session.getAttribute("user");
        if (loginUser.isAdmin() != true) {
            return false; //사용자가 관리자가 아니라면
        }
        return this.faqMapper.deleteArticleByIndex(index) > 0;
    }

    //    수정한다면 원래 있던 내용들을 그대로 표시하기
    public FaqEntity getPatchIndexArticle(int index, HttpSession session) {
        UserEntity loginUser = (UserEntity) session.getAttribute("user");
        if (loginUser.isAdmin() != true) {
            return null; //사용자가 관리자가 아니라면
        }
        return this.faqMapper.selectArticleByPatchIndex(index);
    }

    public PatchNoticeViewResult UpdateArticle(FaqEntity faq, HttpSession session, HttpServletRequest request) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        if (user == null) {
            return PatchNoticeViewResult.FAILURE;
        }
        faq.setClientIp(request.getRemoteAddr())
                .setClientUa(request.getHeader("User-Agent"))
                .setCreatedAt(new Date());
        return this.faqMapper.updateArticleContent(faq) > 0
                ? PatchNoticeViewResult.SUCCESS
                : PatchNoticeViewResult.FAILURE;
    }
}
