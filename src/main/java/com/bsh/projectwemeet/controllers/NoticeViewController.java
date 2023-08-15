package com.bsh.projectwemeet.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value="/")
public class NoticeViewController {

    private final NoticeViewService noticeViewService;
    @Autowired
    public NoticeViewController(NoticeViewService noticeViewService){
        this.noticeViewService = noticeViewService;
    }

}
