package com.bsh.projectwemeet.controllers;

import com.bsh.projectwemeet.entities.*;
import com.bsh.projectwemeet.enums.PatchNoticeViewResult;
import com.bsh.projectwemeet.services.EventService;
import org.json.JSONObject;
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
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @RequestMapping(value = "event",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getEvent(HttpSession session){
        ModelAndView modelAndView = new ModelAndView("home/Event/event");
        UserEntity user = this.eventService.CheckUser(session);
        EventEntity[] eventArticle = this.eventService.getCountArticle();
        modelAndView.addObject("user", user);
        modelAndView.addObject("eventArticle",eventArticle);
        return modelAndView;
    } //메인 홈 주소

    @RequestMapping(value = "eventWriter", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getNoticeWriter() {
        ModelAndView modelAndView = new ModelAndView("home/Event/event-Write");
        return modelAndView;
    }

    @RequestMapping(value = "eventWriter", method = RequestMethod.POST, produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public ModelAndView postNoticeWriter(HttpServletRequest request, EventEntity eventWrite) {
        boolean result = this.eventService.putEventWriter(request, eventWrite);
        ModelAndView modelAndView = new ModelAndView("home/Event/event-Write");
        if (result) {
//          성공적으로 작성되었다면 noticeView로 전환
            modelAndView.setViewName("redirect:/eventView?index=" + eventWrite.getIndex());
        } else {
            modelAndView.setViewName("home/Event/event-Write");
            modelAndView.addObject("result", result);
        }
        return modelAndView;
    }

    @RequestMapping(value = "eventView",
            method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getRead(@RequestParam(value = "index") int index, HttpSession session) {
        UserEntity loginUser = (UserEntity) session.getAttribute("user");
        ModelAndView modelAndView = new ModelAndView("home/Event/event-View");
        EventEntity article = this.eventService.readArticle(index);
        modelAndView.addObject("article", article);
        modelAndView.addObject("user", loginUser);
        return modelAndView;
    }

    @RequestMapping(value = "eventUploadImage",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
//    에디터 안 그림 다운에 관한것
//     네트워크 페이로드 값이 upload이기 때문에 param에 upload로 만든것이다. 왜냐 정해져있는것이기 때문이다.
    public String postUploadImage(HttpServletRequest request, @RequestParam(value = "upload") MultipartFile file) throws IOException {
        EventImagesEntity image = this.eventService.putImage(request, file);
        JSONObject responseObject = new JSONObject() {{
            put("url", "/eventDownloadImage?index=" + image.getIndex());
        }};
        return responseObject.toString();
    }

    //게시판 안에 이미지 삽입시 다운로드
//    ResponseEntity는 httpentity를 상속받는 결과 데이터와 HTTP 상태 코드를 직접 제어할 수 있는 클래스이다.
//     이코드는 반환값에 상태코드(성공,실패확인하기 위해)와 응답 메세지를 주고 싶을때 사용한다.
//     여기에는 사용자의 HttpRequest에 대한 응답 데이터가 포함된다. 즉 HttpStatus(상태), HttpHeaders(요청/응답), HttpBody(내용)를 포함한다.
//    <> 제네릭 표현인데 알아서 공부해야한다.
    @RequestMapping(value = "eventDownloadImage", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getDownloadImage(@RequestParam(value = "index") int index) {
        EventImagesEntity image = this.eventService.getImage(index);
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


    @RequestMapping(value = "deleteEvent", method = RequestMethod.DELETE, produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String deleteNoticeView(@RequestParam(value = "index") int index, HttpSession session) {
        boolean result = this.eventService.deleteNoticeView(index, session);
        return String.valueOf(result);
    }

    @RequestMapping(value = "eventView/patch",
            method = RequestMethod.GET)
    public ModelAndView patchNotice(@RequestParam(value = "index") int index, HttpSession session) {
        EventEntity article = eventService.getPatchIndexArticle(index, session);
        ModelAndView modelAndView = new ModelAndView("home/Event/event-Patch");
        modelAndView.addObject("article", article);

        return modelAndView;
    }

    @RequestMapping(value = "eventView/patch",
            method = RequestMethod.PATCH,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String PatchWrite(EventEntity event,
                             HttpSession session,
                             HttpServletRequest request) {

        PatchNoticeViewResult result = this.eventService.UpdateArticle(event, session, request);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();

    }



}
