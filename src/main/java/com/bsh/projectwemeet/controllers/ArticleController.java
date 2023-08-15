package com.bsh.projectwemeet.controllers;

import com.bsh.projectwemeet.entities.*;
import com.bsh.projectwemeet.enums.*;
import com.bsh.projectwemeet.models.PagingModel;
import com.bsh.projectwemeet.enums.CreateCommentResult;
import com.bsh.projectwemeet.enums.DeleteCommentResult;
import com.bsh.projectwemeet.services.ArticleService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.ibatis.annotations.Param;
import com.bsh.projectwemeet.services.ReviewService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping(value = "/")
public class ArticleController {

    private final ArticleService articleService;
    private final ReviewService reviewService;

    @Autowired
    public ArticleController(ArticleService articleService, ReviewService reviewService) {
        this.articleService = articleService;
        this.reviewService = reviewService;
    }


    @RequestMapping(value = "article",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getArticle(@RequestParam(value = "p", defaultValue = "1", required = false) int requestPage,
                                   @RequestParam(value = "category", required = false) String category,
                                   @RequestParam(value = "c", defaultValue = "", required = false) String searchCriterion,
                                   @RequestParam(value = "q", defaultValue = "", required = false) String searchQuery) {

        ModelAndView modelAndView = new ModelAndView("home/article");

        int searchResultCount = this.articleService.getCountCategory(searchCriterion, searchQuery, category);


        PagingModel pagingCategory = new PagingModel(
                ArticleService.PAGE_COUNT, //메모서비스의 읽기 전용 변수 접근
                this.articleService.getCountCategory(searchCriterion, searchQuery, category),
                requestPage); //객체화

        ArticleEntity[] articleCategory = this.articleService.getCountCategoryByPage(pagingCategory, searchCriterion, searchQuery, category);
        //페이징하면서 카테고리 관련 게시물 나타내기

        modelAndView.addObject("articleCategory", articleCategory);
        modelAndView.addObject("pagingCategory", pagingCategory);
        modelAndView.addObject("category", category);
        modelAndView.addObject("searchCriterion", searchCriterion);
        modelAndView.addObject("searchQuery", searchQuery);
        modelAndView.addObject("searchResultCount",searchResultCount);
        modelAndView.addObject("requestPage",requestPage);
        return modelAndView;

    } //각 게시판 카테고리 별 //

    @RequestMapping(value = "article/read",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getRead(@RequestParam(value = "index") int index,
                                HttpSession session,
                                boolean flag,String email) {
        ModelAndView modelAndView = new ModelAndView("home/bulletin");

        ArticleEntity article = this.articleService.readArticle(index);
        ArticleEntity[] articles = this.articleService.getMiniArticle();
        UserEntity user = this.articleService.userEmail(session);
        UserEntity articleUser = this.articleService.IntroduceUser(index);
        ProfileEntity profileUser = this.articleService.IntroduceText(index);


        LikeReportEntity LikeResult = this.articleService.selectLike(index,session,flag);
        LikeReportEntity ReportResult = this.articleService.selectReport(index,session,flag);
        SelectParticipantsResult ParticipantsResult = this.articleService.selectParticipants(index,session);
        ArticleEntity[] articleLimitPeople = this.articleService.selectArticleByLimitPeople(index);
        // articleService를 통해 index에 해당하는 게시글을 가져옵니다.
        ProfileEntity profile = this.articleService.profileBulletin(index);
        //게시판 인덱스를 통해 게시판의 작성자가 프로필 테이블에 사진이 있다면 가져오고
        ProfileEntity[] participantsArray = this.articleService.selectParticipantsProfile(index);
        //참가자의 참여부를 따지기
        ProfileEntity[] profiles = this.articleService.ParticipateProfile(index, email);
        ParticipantsEntity profile1 = this.articleService.profileAs(index);



        // ModelAndView에 "article"이라는 이름으로 가져온 게시글을 추가합니다.
        modelAndView.addObject("article", article);
        modelAndView.addObject("articles", articles);
        modelAndView.addObject("user", user);
        modelAndView.addObject("articleUser", articleUser);
        modelAndView.addObject("profileUser", profileUser);
        modelAndView.addObject("LikeResult",LikeResult);
        modelAndView.addObject("ReportResult",ReportResult);
        modelAndView.addObject("ParticipantsResult",ParticipantsResult.name().toLowerCase());
        modelAndView.addObject("articleLimitPeople",articleLimitPeople);
        modelAndView.addObject("profile",profile);
        modelAndView.addObject("participantsArray",participantsArray);
        modelAndView.addObject("profiles",profiles);
        modelAndView.addObject("profile1",profile1);
        return modelAndView;
    }//bulletin 게시판 나타내기


    @RequestMapping(value = "article/image", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getThumbnail(@RequestParam(value = "index") int index) {

        ArticleEntity article = this.articleService.readArticle(index);

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

    @RequestMapping(value = "article/profile/owner", method = RequestMethod.GET)
    public ResponseEntity<byte[]> articleOwnerProfile(@RequestParam(value = "index")int index){
        ProfileEntity profile = this.articleService.profileBulletin(index);

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

    @RequestMapping(value = "article/profile/participant1", method = RequestMethod.GET)
    public ResponseEntity<byte[]> ParticpantProfile1(@RequestParam(value = "index")int index){
        ProfileEntity profile = this.articleService.profile1(index);

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

    @RequestMapping(value = "article/profile/participant2", method = RequestMethod.GET)
    public ResponseEntity<byte[]> ParticpantProfile2(@RequestParam(value = "index")int index){
        ProfileEntity profile = this.articleService.profile2(index);

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


    @RequestMapping(value = "article/profiles", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getProfileThumbnail(@RequestParam(value = "index") int index) {

        ArticleEntity article = this.articleService.readProfile(index);

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



//    @RequestMapping(value = "article/participant/profiles", method = RequestMethod.GET)
//    public ResponseEntity<byte[]> getParticipantProfileThumbnail(@RequestParam(value = "index") int index) {
//
//        ArticleEntity article = this.articleService.readParticipant(index);
//        System.out.println("1");
//
//        ResponseEntity<byte[]> response;
//        if (article == null) {
//            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
//            System.out.println("2");
//
//        } else {
//            System.out.println("3");
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentLength(article.getThumbnail().length);
//            headers.setContentType(MediaType.parseMediaType(article.getThumbnailMime()));
//            response = new ResponseEntity<>(article.getThumbnail(), headers, HttpStatus.OK);
//        }
//        return response;
//    }
//
//    @RequestMapping(value = "article/participant/profiless", method = RequestMethod.GET)
//    public ResponseEntity<byte[]> getParticipantProfileThumbnailTwo(@RequestParam(value = "index") int index) {
//
//        ArticleEntity article = this.articleService.readParticipantTwo(index);
//        System.out.println("4");
//        ResponseEntity<byte[]> response;
//        if (article == null) {
//            System.out.println("5");
//            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        } else {
//            System.out.println("6");
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentLength(article.getThumbnail().length);
//            headers.setContentType(MediaType.parseMediaType(article.getThumbnailMime()));
//            response = new ResponseEntity<>(article.getThumbnail(), headers, HttpStatus.OK);
//        }
//        return response;
//    }


    @RequestMapping(value = "article/category/image",
            method = RequestMethod.GET)
    public ResponseEntity<byte[]> getCategoryThumbnail(@RequestParam(value = "index") int index) {

        ArticleEntity article = this.articleService.readArticle(index);

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



    @RequestMapping(value = "article/read/image", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getThumbnai(@RequestParam(value = "index") int index) {

        ArticleEntity article = this.articleService.readArticle(index);

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

    //인덱스 번호로 사진가져와서 인덱스에 해당하는 게시판에 사진 나타내기

    @RequestMapping(value = "article/read",
            method = RequestMethod.DELETE)
    @ResponseBody //주소도 같고 메서드도 같으면 충돌이 일어난다.
    public String deleteIndex(@RequestParam(value = "index") int index, ArticleEntity article, HttpSession session) {
        boolean result = this.articleService.deleteByIndex(index, article, session);
        return String.valueOf(result);
    }
    //게시판 삭제

    @RequestMapping(value = "article/patch",
            method = RequestMethod.GET)
    public ModelAndView getWrite(@RequestParam(value = "index") int index, HttpSession session) {
        ArticleEntity article = articleService.getPatchIndexArticle(index,session);
        ModelAndView modelAndView = new ModelAndView("home/patchWrite");
        modelAndView.addObject("article", article);
        return modelAndView;
    }
    //게시판 수정 폼 받아오기

    @RequestMapping(value = "article/patch",
            method = RequestMethod.PATCH,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String PatchWrite(@RequestParam(value = "dayStr") String dayStr,
                             @RequestParam(value = "timeStr") String timeStr,
                             @RequestParam(value = "limit") String limit,
                             @RequestParam(value = "thumbnailMultipart", required = false) MultipartFile thumbnailMultipart,
                             ArticleEntity article,
                             HttpSession session) throws IOException, ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date day = sdf.parse(dayStr);
        article.setAppointmentStartDate(day);

        SimpleDateFormat tsdf = new SimpleDateFormat("HH:mm");
        Date time = tsdf.parse(timeStr);
        article.setAppointmentStartTime(time);

        int limitPeople = Integer.parseInt(limit);
        article.setLimitPeople(limitPeople);


        if (thumbnailMultipart != null && !thumbnailMultipart.isEmpty()) {
            article.setThumbnail(thumbnailMultipart.getBytes())
                    .setThumbnailMime(thumbnailMultipart.getContentType());
        }


        PatchArticleResult result = this.articleService.UpdateArticle(article, session);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();

    }


    @RequestMapping(value = "article/Participate",
            method = RequestMethod.POST)
    @ResponseBody //주소도 같고 메서드도 같으면 충돌이 일어난다.
    public String postParticipate(@RequestParam(value = "index") int index, ParticipantsEntity participants, HttpSession session) {
        boolean result = this.articleService.InsertParticipate(index, participants, session);
        return String.valueOf(result);
    } //인원참가시 1증가하는 POST 방식 컨트롤러 (중복 체크 불가)

    @RequestMapping(value = "article/Participate",
            method = RequestMethod.GET)
    public ModelAndView getArticle(@RequestParam(value = "index") int index, ParticipantsEntity participants, HttpSession session) {
        boolean result = this.articleService.checkParticipationStatus(index, participants, session);
        ModelAndView modelAndView = new ModelAndView("home/bulletin");
        modelAndView.addObject("result",result);
        return modelAndView;
    } //인원의 참가여부 select로 표현


    @RequestMapping(value = "article/Participate",
            method = RequestMethod.PATCH,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody //있어야지 JSON 사용가능
    public String patchParticipate(@RequestParam(value = "index") int index, ParticipantsEntity participants, HttpSession session) {
        SelectParticipantsResult result = this.articleService.SelectParticipants(index, participants, session);
        JSONObject responseObject = new JSONObject() {{
            put("result", result.name().toLowerCase());
        }};
        return responseObject.toString();
    } //인원이 참가 했을시 취소 가능하게

    @RequestMapping(value = "article/like",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postLike(@RequestParam(value = "index") int index, LikeReportEntity likeEntity, HttpSession session,boolean flag) {
        InsertLikeAndReportResult result = this.articleService.InsertLike(index,likeEntity,session,flag);
        JSONObject responseObject = new JSONObject() {{
            put("result", result.name().toLowerCase());
        }};
        return responseObject.toString();
    }
    //좋아요를 누르면 좋아요가 오르고 새로고침됨 - 중복시 좋아요 못함 - 게시판 작성자와 로그인 사용자 이메일 같으면 실패

    @RequestMapping(value = "article/report",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postReport(@RequestParam(value = "index") int index, LikeReportEntity likeEntity, HttpSession session,boolean flag) {
        InsertLikeAndReportResult result = this.articleService.InsertReport(index,likeEntity,session,flag);
        JSONObject responseObject = new JSONObject() {{
            put("result", result.name().toLowerCase());
        }};
        return responseObject.toString();
    }
    //를 누르면 좋아요가 오르고 새로고침됨 - 중복시 좋아요 못함 - 게시판 작성자와 로그인 사용자 이메일 같으면 실패

    @RequestMapping(value = "article/like",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String deleteLike(@RequestParam(value = "index") int index,HttpSession session,boolean flag) {
        DeleteLikeReportResult result = this.articleService.deleteLike(index,session,flag);
        JSONObject responseObject = new JSONObject() {{
            put("result", result.name().toLowerCase());
        }};
        return responseObject.toString();
    }
    //좋아요 취소하는 컨트롤러

    @RequestMapping(value = "article/report",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String deleteReport(@RequestParam(value = "index") int index,HttpSession session,boolean flag) {
        DeleteLikeReportResult result = this.articleService.deleteReport(index,session,flag);
        JSONObject responseObject = new JSONObject() {{
            put("result", result.name().toLowerCase());
        }};
        return responseObject.toString();
    }
    //신고를 취소하는 컨트롤러

    @RequestMapping(value = "article/profile", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getMainProfile(HttpSession session) {

        ProfileEntity profile = this.articleService.profileArticle(session);

        ResponseEntity<byte[]> response;
        if (profile == null){
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }else{
            HttpHeaders headers = new HttpHeaders();
            headers.setContentLength(profile.getProfileThumbnail().length);
            headers.setContentType(MediaType.parseMediaType(profile.getProfileThumbnailMime()));
            response = new ResponseEntity<>(profile.getProfileThumbnail(),headers,HttpStatus.OK);
        }
        return response;
    }
    //게시판주인의 프로필 사진

    @RequestMapping(value = "article/read/profile", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getProfile(@RequestParam(value = "index") int index) {

        ProfileEntity profile = this.articleService.profileBulletin(index);

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
    //게시판주인의 프로필 사진

    @RequestMapping(value = "article/Participate/profile", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getParticipants(@RequestParam(value = "index") int index,
                                                  @RequestParam(value = "email") String email) throws IOException {

        ProfileEntity[] profiles = this.articleService.ParticipateProfile(index, email);
        ResponseEntity<byte[]> response = null;

        for (ProfileEntity profile : profiles) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentLength(profile.getProfileThumbnail().length);
            headers.setContentType(MediaType.parseMediaType(profile.getProfileThumbnailMime()));
            response = new ResponseEntity<>(profile.getProfileThumbnail(), headers, HttpStatus.OK);
        }
        return response;
    }
    @RequestMapping(value = "article/Participate/profiles", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<byte[]> getDifferent(@RequestParam(value = "index") int index){
        ProfileEntity[] profiles = this.articleService.ParticipateProfiles(index);
        ResponseEntity<byte[]> response = null;

        for (ProfileEntity profile : profiles) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentLength(profile.getProfileThumbnail().length);
            headers.setContentType(MediaType.parseMediaType(profile.getProfileThumbnailMime()));
            response = new ResponseEntity<>(profile.getProfileThumbnail(), headers, HttpStatus.OK);
        }
        return response;
    }



//    댓글

    @RequestMapping(value = "comment",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getComment(@RequestParam(value = "articleIndex")int articleIndex){
        ArticleEntity article = this.articleService.getArticleByIndex(articleIndex);
        CommentEntity[] comments = this.articleService.getCommentsOf(articleIndex);
        JSONArray responseArray = new JSONArray();
        for (CommentEntity comment : comments) {
            JSONObject commentObject = new JSONObject(comment);
            commentObject.put("same", article.getEmail().equals(comment.getEmail()));
            responseArray.put(commentObject);
        }
        return responseArray.toString();
    }


    @RequestMapping(value = "comment", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postComment(HttpServletRequest request, CommentEntity comment, HttpSession session, @RequestParam("articleEmail") String articleEmail, @RequestParam("nickname") String nickname) throws ParseException {


        CreateCommentResult result = articleService.putComment(request, comment, session, articleEmail, nickname);

        JSONObject responseObject = new JSONObject() {{
            put("result", result.name().toLowerCase());
        }};

        return responseObject.toString();
    }


    @RequestMapping(value = "comment",
            method = RequestMethod.DELETE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String deleteComment(@RequestParam(value = "index") int commentIndex, HttpSession session) {
        CommentEntity comment = new CommentEntity();
        comment.setIndex(commentIndex);

        DeleteCommentResult result = this.articleService.deleteComment(comment, session);
        JSONObject responseObject = new JSONObject() {{
            put("result", result.name().toLowerCase());
        }};
        return responseObject.toString();
    }


//    완료 후 다음으로 보내기

    @RequestMapping(value="article/review", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getUpdateCategory(@RequestParam(value="index")int index, String category){
        ModelAndView modelAndView = new ModelAndView("home/review");
        ArticleEntity article = this.articleService.getUpdateCategoryByIndex(index);
        ReviewEntity[] reviewEntities = this.reviewService.getAll(index);
        Double reviewAvgStar = reviewService.avgStar(index);
        UserEntity articleUser = this.articleService.IntroduceUser(index);
        ProfileEntity profileUser = this.articleService.IntroduceText(index);

        modelAndView.addObject("avgStar", reviewAvgStar);
        modelAndView.addObject("article", article);
        modelAndView.addObject("profileUser", profileUser);
        modelAndView.addObject("articleUser", articleUser);
        modelAndView.addObject("reviews", reviewEntities);
        return modelAndView;
    }



    @RequestMapping(value="article/review", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String updateCategory(int index, HttpSession session){
        UpdateCategoryResult result = this.articleService.updateCategory(index, session);
        JSONObject responseObject = new JSONObject() {{
            put("result", result.name().toLowerCase());
        }};
        return responseObject.toString();
    }




}
