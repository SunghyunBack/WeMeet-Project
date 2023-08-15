package com.bsh.projectwemeet.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping(value="/")
public class LogoutController {

    @RequestMapping(value="logout", method = RequestMethod.GET)
    public ModelAndView getLogout(HttpSession session){
        ModelAndView modelAndView = new ModelAndView("redirect:/");
        session.invalidate();
        return modelAndView;
    }

}
