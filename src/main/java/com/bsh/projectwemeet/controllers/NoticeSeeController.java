package com.bsh.projectwemeet.controllers;

import com.bsh.projectwemeet.entities.NoticeWriterArticleEntity;
import com.bsh.projectwemeet.entities.UserEntity;
import com.bsh.projectwemeet.services.NoticeSeeService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping(value = "/")
public class NoticeSeeController {

    private final NoticeSeeService noticeSeeService;

    public NoticeSeeController(NoticeSeeService noticeSeeService) {
        this.noticeSeeService = noticeSeeService;
    }

    //    공지사항 메인 홈 주소
    @RequestMapping(value = "notice",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getNotice(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("home/Notice/notice");
        UserEntity user = this.noticeSeeService.CheckUser(session);
        NoticeWriterArticleEntity[] articleEntities = this.noticeSeeService.getCountArticle();
        modelAndView.addObject("user", user);
        modelAndView.addObject("article", articleEntities);
        return modelAndView;
    }
}
