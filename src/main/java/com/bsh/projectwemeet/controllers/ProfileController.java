package com.bsh.projectwemeet.controllers;

import com.bsh.projectwemeet.entities.*;
import com.bsh.projectwemeet.enums.*;
import com.bsh.projectwemeet.services.ProfileService;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
@RequestMapping(value = "profile")
public class ProfileController {
    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @RequestMapping(value = "/",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getProfile(@RequestParam(value = "nickname") String nickName) {
        ModelAndView modelAndView = new ModelAndView("home/profile");

        UserEntity user = this.profileService.getUserByNickName(nickName);
        ArticleEntity[] article = this.profileService.getCountCategoryByPage(nickName);
        ProfileEntity profile = this.profileService.getThumbnail(nickName);

        modelAndView.addObject("profile", user);
        modelAndView.addObject("article", article);
        modelAndView.addObject("content", profile);
        return modelAndView;
    }


    @RequestMapping(value = "article/image",
            method = RequestMethod.GET)
    public ResponseEntity<byte[]> getArticleImage(@RequestParam(value = "index") int index) {
        ArticleEntity article = this.profileService.getArticleImage(index);

        ResponseEntity<byte[]> response;
        if (article == null) {
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentLength(article.getThumbnail().length);
            headers.setContentType(MediaType.parseMediaType(article.getThumbnailMime()));
            response = new ResponseEntity<>(article.getThumbnail(), headers, HttpStatus.OK);
        }
        return response;
    }

    @RequestMapping(value = "Thumbnail",
            method = RequestMethod.GET)
    public ResponseEntity<byte[]> getThumbnail(@RequestParam(value = "nickname") String nickname) {
        ProfileEntity profile = this.profileService.getThumbnail(nickname);

        ResponseEntity<byte[]> response;
        if (profile == null) {
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentLength(profile.getProfileThumbnail().length);
            headers.setContentType(MediaType.parseMediaType(profile.getProfileThumbnailMime()));
            response = new ResponseEntity<>(profile.getProfileThumbnail(), headers, HttpStatus.OK);
        }
        return response;
    }

    @RequestMapping(value = "checkPassword",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getCheckPassword() {
        return new ModelAndView("home/profile");
    }

    @RequestMapping(value = "checkPassword",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postCheckPassword(HttpSession session, @RequestParam("checkPassword") String password) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        user.setPassword(password);
        LoginResult result = this.profileService.checkPassword(user);
        JSONObject responseObject = new JSONObject() {{
            put("result", result.name().toLowerCase());
        }};
        return responseObject.toString();
    }

    @RequestMapping(value = "/modifyPassword",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getModifyPassword() {
        return new ModelAndView("home/profile");
    }

    @RequestMapping(value = "/modifyPassword",
            method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String modifyPassword(HttpSession session, @RequestParam("infoPassword") String password) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        ModifyPasswordResult result = this.profileService.modifyPassword(password, user, session);

        JSONObject responseObject = new JSONObject() {{
            put("result", result.name().toLowerCase());
        }};
        return responseObject.toString();
    }

    @RequestMapping(value = "/resetContact",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getResetContact() {
        return new ModelAndView("home/profile");
    }

    @RequestMapping(value = "/resetContact",
            method = RequestMethod.PATCH,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchResetContact(HttpSession session, @RequestParam("infoContact") String contact) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        ModifyPasswordResult result = this.profileService.resetContact(contact, user, session);

        JSONObject responseObject = new JSONObject() {{
            put("result", result.name().toLowerCase());
        }};
        return responseObject.toString();
    }


    @RequestMapping(value = "/resetNickname",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getResetNickname() {
        return new ModelAndView("home/profile");
    }

    @RequestMapping(value = "/resetNickname",
            method = RequestMethod.PATCH,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchResetNickname(HttpSession session, @RequestParam("infoNickname") String nickname) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        ModifyPasswordResult result = this.profileService.resetNickname(nickname, user, session);

        JSONObject responseObject = new JSONObject() {{
            put("result", result.name().toLowerCase());
        }};
        return responseObject.toString();
    }

    @RequestMapping(value = "/resetAddress",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getResetAddress() {
        return new ModelAndView("home/profile");
    }


    @RequestMapping(value = "/resetAddress",
            method = RequestMethod.PATCH,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchResetAddress(HttpSession session, @RequestParam("infoAddressPostal") String postal,
                                    @RequestParam("infoAddressPrimary") String primary, @RequestParam("infoAddressSecondary") String secondary) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        ModifyPasswordResult result = this.profileService.resetAddress(postal, primary, secondary, user, session);
        JSONObject responseObject = new JSONObject() {{
            put("result", result.name().toLowerCase());
        }};
        return responseObject.toString();
    }

    //프로필 이미지 변경
    @RequestMapping(value = "/profileImage",
            method = RequestMethod.PATCH,
            produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String postIndex(HttpSession session,
                            ProfileEntity profile,
                            @RequestParam(value = "thumbnailMultipart") MultipartFile thumbnailMultipart) throws IOException {
        UserEntity user = (UserEntity) session.getAttribute("user");
        profile.setEmail(user.getEmail());
        profile.setProfileThumbnail(thumbnailMultipart.getBytes());
        profile.setProfileThumbnailMime(thumbnailMultipart.getContentType());
        boolean result = this.profileService.putProfile(profile);
        return String.valueOf(result);
    }

    @RequestMapping(value = "/resetContent",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getResetContent() {
        return new ModelAndView("home/profile");
    }

    @RequestMapping(value = "/resetContent",
            method = RequestMethod.PATCH,
            produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String patchResetContent(HttpSession session,
                                    ProfileEntity profile,
                                    @RequestParam("infoContent") String content) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        profile.setEmail(user.getEmail());
        profile.setIntroduceText(content);
        boolean result = this.profileService.resetContent(profile, content);
        return String.valueOf(result);
    }


    //인증번호 전송 코드
    @RequestMapping(value = "contactCodeRec",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getContactCodeRec(RegisterContactCodeEntity registerContactCode) {
        SendRegisterContactCodeResult result = this.profileService.sendContactCodeResult(registerContactCode);
        JSONObject responseObject = new JSONObject() {{
            put("result", result.name().toLowerCase());
        }};
        if (result == SendRegisterContactCodeResult.SUCCESS) {
            responseObject.put("salt", registerContactCode.getSalt());
        }
        return responseObject.toString();
    }

    //인증번호 6자리 확인 코드
    @RequestMapping(value = "contactCodeRec",
            method = RequestMethod.PATCH,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchContactCodeRec(RegisterContactCodeEntity registerContactCode) {
        VerifyRegisterContactCodeResult result = this.profileService.verifyRegisterContactCodeResult(registerContactCode);
        JSONObject responseObject = new JSONObject() {{
            put("result", result.name().toLowerCase());
        }};

        return responseObject.toString();
    }

    @RequestMapping(value = "/deleteThumbnail",
            method = RequestMethod.PATCH,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String deleteThumbnail(HttpSession session, ProfileEntity profile) {
        DeleteUserResult result = this.profileService.deleteThumbnailResult(session, profile);

        JSONObject responseObject = new JSONObject() {{
            put("result", result.name().toLowerCase());
        }};
        return responseObject.toString();
    }


    @RequestMapping(value = "/deleteUser",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String deleteUser(HttpSession session) {
        UserEntity loggedInUser = (UserEntity) session.getAttribute("user");
        session.setAttribute("user", loggedInUser);
        DeleteUserResult result = this.profileService.deleteUserResult(session);
        JSONObject responseObject = new JSONObject() {{
            put("result", result.name().toLowerCase());
        }};

        if (result == DeleteUserResult.SUCCESS) {
            session.invalidate();
        }
        return responseObject.toString();
    }
}
