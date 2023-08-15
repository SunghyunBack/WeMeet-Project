package com.bsh.projectwemeet.controllers;

import com.bsh.projectwemeet.entities.NoticeWriterArticleEntity;
import com.bsh.projectwemeet.entities.NoticeWriterImagesEntity;
import com.bsh.projectwemeet.entities.UserEntity;
import com.bsh.projectwemeet.enums.PatchNoticeViewResult;
import com.bsh.projectwemeet.services.NoticeWriterService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
@RequestMapping(value = "/")
public class NoticeWriterController {

    private final NoticeWriterService noticeWriterService;

    @Autowired
    public NoticeWriterController(NoticeWriterService noticeWriterService) {
        this.noticeWriterService = noticeWriterService;
    }


    @RequestMapping(value = "noticeWriter", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getNoticeWriter() {
        ModelAndView modelAndView = new ModelAndView("home/Notice/noticeWriter");
        return modelAndView;
    }

    //  공지사항 작성란 insert
    @RequestMapping(value = "noticeWriter", method = RequestMethod.POST, produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public ModelAndView postNoticeWriter(HttpServletRequest request, NoticeWriterArticleEntity noticeWriterArticle) {
        boolean result = this.noticeWriterService.putNoticeWriter(request, noticeWriterArticle);
        ModelAndView modelAndView = new ModelAndView("home/Notice/noticeWriter");
        if (result) {
//          성공적으로 작성되었다면 noticeView로 전환
            modelAndView.setViewName("redirect:/noticeView?index=" + noticeWriterArticle.getIndex());
        } else {
            modelAndView.setViewName("home/NoticenoticeWriter");
            modelAndView.addObject("result", result);
        }
        return modelAndView;
    }

    // 성공적인 작성후 보낼 noticeView
    @RequestMapping(value = "noticeView",
            method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getRead(@RequestParam(value = "index") int index, HttpSession session) {
        UserEntity loginUser = (UserEntity) session.getAttribute("user");
        ModelAndView modelAndView = new ModelAndView("home/Notice/noticeView");
        NoticeWriterArticleEntity article = this.noticeWriterService.readArticle(index);
        modelAndView.addObject("article", article);
        modelAndView.addObject("user", loginUser);
        return modelAndView;
    }


    @RequestMapping(value = "uploadImage",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
//    에디터 안 그림 다운에 관한것
//     네트워크 페이로드 값이 upload이기 때문에 param에 upload로 만든것이다. 왜냐 정해져있는것이기 때문이다.
    public String postUploadImage(HttpServletRequest request, @RequestParam(value = "upload") MultipartFile file) throws IOException {
        NoticeWriterImagesEntity image = this.noticeWriterService.putImage(request, file);
        JSONObject responseObject = new JSONObject() {{
            put("url", "/downloadImage?index=" + image.getIndex());
        }};
        return responseObject.toString();
    }

    //게시판 안에 이미지 삽입시 다운로드
//    ResponseEntity는 httpentity를 상속받는 결과 데이터와 HTTP 상태 코드를 직접 제어할 수 있는 클래스이다.
//     이코드는 반환값에 상태코드(성공,실패확인하기 위해)와 응답 메세지를 주고 싶을때 사용한다.
//     여기에는 사용자의 HttpRequest에 대한 응답 데이터가 포함된다. 즉 HttpStatus(상태), HttpHeaders(요청/응답), HttpBody(내용)를 포함한다.
//    <> 제네릭 표현인데 알아서 공부해야한다.
    @RequestMapping(value = "downloadImage", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getDownloadImage(@RequestParam(value = "index") int index) {
        NoticeWriterImagesEntity image = this.noticeWriterService.getImage(index);
        ResponseEntity<byte[]> response;
        if (image == null) {
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            HttpHeaders headers = new HttpHeaders();
//            일부러 파일 길이를 늘려서 몇%남았는지 보여주기 위해 작성한 코드다. 아주작은 크기의 파일은 늘려서 할경우 보일때 아다리가 안 맞을수 있다.
            headers.setContentLength(image.getSize());
//            문자열을 미디어 타입으로 뱉어낸다.
            headers.setContentType(MediaType.parseMediaType(image.getContentType()));
            response = new ResponseEntity<>(image.getData(), headers, HttpStatus.OK);
        }
        return response;
    }


    @RequestMapping(value = "delete", method = RequestMethod.DELETE, produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String deleteNoticeView(@RequestParam(value = "index") int index, HttpSession session) {
        boolean result = this.noticeWriterService.deleteNoticeView(index, session);
        return String.valueOf(result);
    }

    @RequestMapping(value = "noticeView/patch",
            method = RequestMethod.GET)
    public ModelAndView patchNotice(@RequestParam(value = "index") int index, HttpSession session) {
        NoticeWriterArticleEntity article = noticeWriterService.getPatchIndexArticle(index, session);
        ModelAndView modelAndView = new ModelAndView("home/Notice/patchNoticeWriter");
        modelAndView.addObject("article", article);

        return modelAndView;
    }

    @RequestMapping(value = "noticeView/patch",
            method = RequestMethod.PATCH,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String PatchWrite(NoticeWriterArticleEntity noticeWriterArticle,
                             HttpSession session,
                             HttpServletRequest request) {

        PatchNoticeViewResult result = this.noticeWriterService.UpdateArticle(noticeWriterArticle, session, request);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();

    }


}
