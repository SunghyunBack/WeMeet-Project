package com.bsh.projectwemeet.controllers;

import com.bsh.projectwemeet.entities.UserEntity;
import com.bsh.projectwemeet.enums.LoginResult;
import com.bsh.projectwemeet.services.LoginService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping(value = "/")
public class LoginController {

    private final LoginService loginService;

    @Autowired
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @RequestMapping(value = "login",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getLogin() {
        ModelAndView modelAndView = new ModelAndView("home/login");
        return modelAndView;
    } // 로그인 주소


    @RequestMapping(value = "login", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postLogin(HttpSession session, UserEntity user) {
        LoginResult result = this.loginService.login(user);
        if (result == LoginResult.SUCCESS) {
            session.setAttribute("user", user);
        } //로그인 성공시 세션에 사용자 정보를 저장합니다.
        // 세션은 로그인된 사용자를 유지하기 위해 사용되는 데이터 저장 공간입니다.

        JSONObject responseObject = new JSONObject() {{
            put("result", result.name().toLowerCase());
        }};
        //JSON 객체에 "result"라는 키와 로그인 결과(result)의 소문자 문자열 값을 추가합니다.
        // result.name()은 result 변수의 열거형 상수 이름을 가져오고, toLowerCase() 메서드를 사용하여 소문자로 변환합니다.
        // PUT은 서버에 데이터를 전송해 해당리소스 즉 Login result의 상수를 가져와서 소문자로 대체 합니다.
        // 업데이트의 의미는 기존의 값을 새로운 값으로 대체하거나 갱신하는 것을 의미합니다.

        return responseObject.toString();
        //JSON객체를 문자열로 반환하여 저장합니다.
    }


}
