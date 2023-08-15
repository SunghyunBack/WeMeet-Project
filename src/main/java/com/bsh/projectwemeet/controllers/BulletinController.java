package com.bsh.projectwemeet.controllers;

import com.bsh.projectwemeet.entities.BulletinEntity;
import com.bsh.projectwemeet.entities.UserEntity;
import com.bsh.projectwemeet.services.BulletinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value="/")
public class BulletinController {

    private final BulletinService bulletinService;
    @Autowired
    public BulletinController(BulletinService bulletinService){
        this.bulletinService = bulletinService;
    }


    @RequestMapping(value="bulletin", method = RequestMethod.GET)
    public ModelAndView getBulletin(){
        ModelAndView modelAndView = new ModelAndView("home/bulletin");
        return modelAndView;
    }

    @RequestMapping(value="bulletin", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postComment(HttpServletRequest request, BulletinEntity bulletinEntity, @SessionAttribute(value="user") UserEntity user){
        bulletinEntity.setEmail(user.getEmail());
        boolean result = this.bulletinService.putComment(request, bulletinEntity);
        return String.valueOf(result);
    }


}
