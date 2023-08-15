package com.bsh.projectwemeet.controllers;

import com.bsh.projectwemeet.entities.FaqEntity;
import com.bsh.projectwemeet.entities.UserEntity;
import com.bsh.projectwemeet.enums.PatchNoticeViewResult;
import com.bsh.projectwemeet.services.FaqService;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


@Controller
@RequestMapping(value = "/")
public class FaqController {

    private final FaqService faqService;

    public FaqController(FaqService faqService) {
        this.faqService = faqService;
    }

    @RequestMapping(value = "faq",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getEvent(HttpSession session){
        ModelAndView modelAndView = new ModelAndView("home/Faq/faq");
        UserEntity user = this.faqService.CheckUser(session);
        FaqEntity[] faqArticle = this.faqService.getCountArticle();
        modelAndView.addObject("user", user);
        modelAndView.addObject("faq",faqArticle);
        return modelAndView;
    } //메인 홈 주소

    @RequestMapping(value = "faqWriter", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getNoticeWriter() {
        ModelAndView modelAndView = new ModelAndView("home/Faq/faqWrite");
        return modelAndView;
    }

    @RequestMapping(value = "faqWriter", method = RequestMethod.POST, produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public ModelAndView postNoticeWriter(HttpServletRequest request, FaqEntity faq) {
        boolean result = this.faqService.putEventWriter(request, faq);
        ModelAndView modelAndView = new ModelAndView("home/Faq/faqWrite");
        if (result) {
//          성공적으로 작성되었다면 noticeView로 전환
            modelAndView.setViewName("redirect:/faqView?index=" + faq.getIndex());
        } else {
            modelAndView.setViewName("home/Faq/faqWrite");
            modelAndView.addObject("result", result);
        }
        return modelAndView;
    }

    @RequestMapping(value = "faqView",
            method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getRead(@RequestParam(value = "index") int index, HttpSession session) {
        UserEntity loginUser = (UserEntity) session.getAttribute("user");
        ModelAndView modelAndView = new ModelAndView("home/Faq/faqView");
        FaqEntity article = this.faqService.readArticle(index);
        modelAndView.addObject("article", article);
        modelAndView.addObject("user", loginUser);
        return modelAndView;
    }

    @RequestMapping(value = "deleteFaq", method = RequestMethod.DELETE, produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String deleteNoticeView(@RequestParam(value = "index") int index, HttpSession session) {
        boolean result = this.faqService.deleteNoticeView(index, session);
        return String.valueOf(result);
    }

    @RequestMapping(value = "faqView/patch",
            method = RequestMethod.GET)
    public ModelAndView patchNotice(@RequestParam(value = "index") int index, HttpSession session) {
        FaqEntity article = faqService.getPatchIndexArticle(index, session);
        ModelAndView modelAndView = new ModelAndView("home/Faq/faqPatch");
        modelAndView.addObject("article", article);

        return modelAndView;
    }

    @RequestMapping(value = "faqView/patch",
            method = RequestMethod.PATCH,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String PatchWrite(FaqEntity faq,
                             HttpSession session,
                             HttpServletRequest request) {

        PatchNoticeViewResult result = this.faqService.UpdateArticle(faq, session, request);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();

    }
}
