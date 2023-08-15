package com.bsh.projectwemeet.controllers;

import com.bsh.projectwemeet.entities.RecoverContactCodeEntity;
import com.bsh.projectwemeet.entities.RecoverEmailCodeEntity;
import com.bsh.projectwemeet.entities.UserEntity;
import com.bsh.projectwemeet.enums.*;
import com.bsh.projectwemeet.services.CheckService;
import com.bsh.projectwemeet.services.RecoverAccountService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.mail.MessagingException;

@Controller
@RequestMapping(value = "/recoverAccount")
public class RecoverAccountController {

    private final RecoverAccountService recoverAccountService;
    private final CheckService checkService;


    @Autowired
    public RecoverAccountController(RecoverAccountService recoverAccountService, CheckService checkService) {
        this.recoverAccountService = recoverAccountService;
        this.checkService = checkService;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView getRecoverAccount() {
        ModelAndView modelAndView = new ModelAndView("home/recoverAccount");
        return modelAndView;
    }

    // 연락처를 이용해 인증번호를 보내기 위한 코드
    @RequestMapping(value = "contactCodeRec",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getContactCodeRec(RecoverContactCodeEntity recoverContactCode) {
//        JS객체문법으로 구조화된 데이터를 표현하기 위한 문자 기반 표준 포맷이다.
//        JSON은 문자열 형태로 존재한다. 그래서 네트워크를 통해 전송할때 유용하다.
//        put을 이용해 result키에 SendRecoverContactCodeResult의 타입의 값을 문자열을 가진다.
        SendRecoverContactCodeResult result = this.recoverAccountService.sendRecoverContactCode(recoverContactCode);
        JSONObject responseObject = new JSONObject() {{
            put("result", result.name().toLowerCase());
        }};
//        조건식에 부합하면 service에서 적용한 salt값을  salt의 키에 대한 값으로 가진다.
        if (result == SendRecoverContactCodeResult.SUCCESS) {
            responseObject.put("salt", recoverContactCode.getSalt());
        }
        return responseObject.toString();
    }

    // 인증번호 6자리 확인+ get으로 요청하기 위한 코드
    @RequestMapping(value = "contactCodeRec", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchContactCodeRec(RecoverContactCodeEntity recoverContactCode) {

        VeryfiRecoverContactCodeResult result = this.recoverAccountService.verifyRecoverContactCodeResult(recoverContactCode);
        JSONObject responseObject = new JSONObject() {{
            put("result", result.name().toLowerCase());
        }};
//        여기서 데이터 베이스의 contact와 이름의 값이 값으니 UserEntity안의 email과 name을 꺼낸다는 의미이다.
        if (result == VeryfiRecoverContactCodeResult.SUCCESS) {
            UserEntity user = this.recoverAccountService.getUserByContactName(recoverContactCode.getContact(), recoverContactCode.getName());
            responseObject.put("email", user.getEmail());
            responseObject.put("name", user.getName());
        }
        return responseObject.toString();
    }

    //    위의 코드와 똑같지만 요청 방식이 다르다. 위의 코드느 인증번호를 확인하기 위한 코드이고 아래의 코드는 위에서 비교하고 보낸값들을 get으로 요청하기 위한 코드이다.
    @RequestMapping(value = "/confirmEmail", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView getConfirmEmail(RecoverContactCodeEntity recoverContactCode) {
        ModelAndView modelAndView = new ModelAndView("home/confirmEmail");
        VeryfiRecoverContactCodeResult result = this.recoverAccountService.verifyRecoverContactCodeResult(recoverContactCode);
        JSONObject responseObject = new JSONObject() {{
            put("result", result.name().toLowerCase());
        }};

        if (result == VeryfiRecoverContactCodeResult.SUCCESS) {
            UserEntity user = this.recoverAccountService.getUserByContactName(recoverContactCode.getContact(), recoverContactCode.getName());
            modelAndView.addObject("email", user.getEmail());
            modelAndView.addObject("name", user.getName());
        }
        modelAndView.addObject("recoverContactCode", recoverContactCode);
        return modelAndView;
    }


    /*-----------------------------------------------------------------------------------------------------------------------------------*/

    @RequestMapping(value = "recoverPassword",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postRecoverPassword(RecoverEmailCodeEntity recoverEmailCode) throws MessagingException, MessagingException {
        SendRecoverEmailCodeResult result = this.checkService.sendRecoverEmailCode(recoverEmailCode);
        JSONObject responseObject = new JSONObject() {{
            put("result", result.name().toLowerCase());
        }};
        if (result == SendRecoverEmailCodeResult.SUCCESS) {
            responseObject.put("redirect", String.format("http://wemeet.sunghyunback.com/recoverAccount/recoverPassword?email=%s&salt=%s",
                    recoverEmailCode.getEmail(),
                    recoverEmailCode.getSalt()));
        }
        return responseObject.toString();
    } //링크보내기

    @RequestMapping(value = "recoverPassword",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getRecoverPassword(RecoverEmailCodeEntity recoverEmailCode) {
        VerifyRecoverEmailCodeResult result = this.checkService.verifyRecoverEmailCode(recoverEmailCode);
        ModelAndView modelAndView = new ModelAndView("_recoverEmail");
        modelAndView.addObject("result", result.name().toLowerCase());
        modelAndView.addObject("recoverEmailCode", recoverEmailCode);
        return modelAndView;
    } //위의 링크에 대해서 읽은 다음 결과값에 따라 _recoverEmail을 반환한다.

    @RequestMapping(value = "emailCodeRec",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postEmailCodeRec(RecoverEmailCodeEntity recoverEmailCode) {
        VerifyRecoverEmailCodeResult result = this.checkService.recoverEmailCodeResult(recoverEmailCode);
        JSONObject responseObject = new JSONObject() {{
            put("result", result.name().toLowerCase());
        }};
        return responseObject.toString();
    } // 인증을 판단해서 결과 돌려주는 컨트롤러

    @RequestMapping(value = "recoverPassword",
            method = RequestMethod.PATCH,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchRecoverPassword(RecoverEmailCodeEntity recoverEmailCode, UserEntity user) {
        RecoverPasswordResult result = this.checkService.recoverPassword(recoverEmailCode, user);
        JSONObject responseObject = new JSONObject() {{
            put("result", result.name().toLowerCase());
        }};
        return responseObject.toString();
    } //PATCH(리소스의 일부를 수정한다) 비밀번호 변경 컨트롤러

    //HTTP 프로토콜 종류
    //GET: 리소스의 조회를 요청하는 메소드입니다. 서버로부터 정보를 요청하고 응답으로 해당 리소스를 받아옵니다.
    // 데이터의 변경이나 부수적인 효과가 없는 "읽기 전용" 작업에 사용됩니다.

    //POST: 리소스의 생성 또는 데이터의 제출을 요청하는 메소드입니다. 서버에 새로운 리소스를 생성하거나
    // 데이터를 제출할 때 사용됩니다. 주로 폼 데이터나 파일 업로드 등에 사용됩니다. 서버는 이 요청에 대한 처리 결과를 응답으로 전송합니다.

    //PUT: 리소스의 전체적인 수정을 요청하는 메소드입니다. 클라이언트가 요청한 리소스의 전체 내용을 요청 본문에 담아 서버에 보내고, 해당 리소스를 요청된 내용으로 교체합니다. 즉, 리소스의 대체(갱신) 작업을 수행합니다.

    //PATCH: 리소스의 일부 수정을 요청하는 메소드입니다. PUT과 달리 리소스의 일부만 변경하고자 할 때 사용됩니다. 요청 본문에 변경할 필드와 값을 전송하고, 서버는 해당 필드만 수정합니다.

    //DELETE: 리소스의 삭제를 요청하는 메소드입니다. 서버에 특정 리소스의 삭제를 요청하고, 해당 리소스를 삭제합니다.

}
