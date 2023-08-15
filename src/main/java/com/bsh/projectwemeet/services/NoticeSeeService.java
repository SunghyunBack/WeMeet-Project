package com.bsh.projectwemeet.services;

import com.bsh.projectwemeet.entities.NoticeWriterArticleEntity;
import com.bsh.projectwemeet.entities.UserEntity;
import com.bsh.projectwemeet.mappers.NoticeSeeMapper;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@Service
public class NoticeSeeService {

    private final NoticeSeeMapper noticeSeeMapper;

    public NoticeSeeService(NoticeSeeMapper noticeSeeMapper) {
        this.noticeSeeMapper = noticeSeeMapper;
    }

    // 로그인의 여부
    public UserEntity CheckUser(HttpSession session) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        if (user == null) {
            return null;
        }
        UserEntity check = noticeSeeMapper.selectCheckUser(user.getEmail());
        return check;
    }

    //    작성된 내용들 select
    public NoticeWriterArticleEntity[] getCountArticle() {
        return this.noticeSeeMapper.selectCountArticle();
    }
}
