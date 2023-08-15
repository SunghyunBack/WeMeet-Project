package com.bsh.projectwemeet.controllers;

import com.bsh.projectwemeet.entities.ArticleEntity;
import com.bsh.projectwemeet.entities.ProfileEntity;
import com.bsh.projectwemeet.models.PagingModel;
import com.bsh.projectwemeet.services.ArticleService;
import com.bsh.projectwemeet.services.CompleteService;
import com.bsh.projectwemeet.services.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Controller
@RequestMapping(value = "/")
public class CompleteCategoryController {

    private final CompleteService completeService;
    private final ReviewService reviewService;


    @Autowired
    public CompleteCategoryController(CompleteService completeService, ReviewService reviewService) {
        this.completeService = completeService;
        this.reviewService = reviewService;
    }

    @RequestMapping(value = "complete", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getComplete(@RequestParam(value = "p", defaultValue = "1", required = false) int requestPage,
                                    @RequestParam(value = "category", defaultValue = "", required = false) String category,
                                    @RequestParam(value = "c", defaultValue = "", required = false) String searchCriterion,
                                    @RequestParam(value = "q", defaultValue = "", required = false) String searchQuery) {

        int searchResultCount = this.completeService.getCountCategory(searchCriterion, searchQuery, category);

        ModelAndView modelAndView = new ModelAndView("home/completeCategory"); //index.html 연결

        PagingModel pagingCategory = new PagingModel(
                ArticleService.PAGE_COUNT, //메모서비스의 읽기 전용 변수 접근
                searchResultCount,
                requestPage); //객체화

        ArticleEntity[] articleCategory = this.completeService.getCountCategoryByPage(pagingCategory, searchCriterion, searchQuery, category);
        //페이징하면서 카테고리 관련 게시물 나타내기

        modelAndView.addObject("articleCategory", articleCategory);
        modelAndView.addObject("pagingCategory", pagingCategory);
        modelAndView.addObject("category", category);
        modelAndView.addObject("searchCriterion", searchCriterion);
        modelAndView.addObject("searchQuery", searchQuery);
        modelAndView.addObject("searchResultCount", searchResultCount);
        return modelAndView;

    }


    //        게시글 불러오기
    @RequestMapping(value = "complete/image",
            method = RequestMethod.GET)
    public ResponseEntity<byte[]> getThumbnail(@RequestParam(value = "index") int index) {

        ArticleEntity article = this.completeService.readArticle(index);
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


    @RequestMapping(value = "complete/category/image",
            method = RequestMethod.GET)
    public ResponseEntity<byte[]> getCategoryThumbnail(@RequestParam(value = "index") int index) {

        ArticleEntity article = this.completeService.readArticle(index);
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

    //       작성자 프로필 나타내기
    @RequestMapping(value = "complete/read/profile", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getProfile(@RequestParam(value = "index") int index) {

        ProfileEntity profile = this.completeService.profileBulletin(index);

        ResponseEntity<byte[]> response;
        if (profile == null) {
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            try {
                // 원본 이미지를 BufferedImage로 변환
                BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(profile.getProfileThumbnail()));

                // 새로운 크기로 이미지 조정
                int newWidth = 60;
                int newHeight = 60;
                Image resizedImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);

                // BufferedImage 생성
                BufferedImage outputImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);

                // Graphics2D를 사용하여 이미지 그리기
                Graphics2D graphics = outputImage.createGraphics();
                graphics.drawImage(resizedImage, 0, 0, null);
                graphics.dispose();

                // 조정된 이미지를 바이트 배열로 변환
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(outputImage, "jpg", baos);
                byte[] resizedImageBytes = baos.toByteArray();

                // HTTP 응답 헤더 설정
                HttpHeaders headers = new HttpHeaders();
                headers.setContentLength(resizedImageBytes.length);
                headers.setContentType(MediaType.IMAGE_JPEG);

                response = new ResponseEntity<>(resizedImageBytes, headers, HttpStatus.OK);

            } catch (Exception e) {
                e.printStackTrace();
                response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return response;
    }


    // 참가자1 프로필 나타내기
    @RequestMapping(value = "complete/profile/participant1", method = RequestMethod.GET)
    public ResponseEntity<byte[]> ParticpantProfile1(@RequestParam(value = "index") int index) {
        ProfileEntity profile = this.completeService.profile1(index);

        ResponseEntity<byte[]> response;
        if (profile == null) {
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            try {
                // 원본 이미지를 BufferedImage로 변환
                BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(profile.getProfileThumbnail()));

                // 새로운 크기로 이미지 조정
                int newWidth = 60;
                int newHeight = 60;
                Image resizedImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);

                // BufferedImage 생성
                BufferedImage outputImage = new BufferedImage(newWidth, newHeight, originalImage.getType());

                // Graphics2D를 사용하여 이미지 그리기
                Graphics2D graphics = outputImage.createGraphics();
                graphics.drawImage(resizedImage, 0, 0, null);
                graphics.dispose();

                // 조정된 이미지를 바이트 배열로 변환
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(outputImage, "png", baos);
                byte[] resizedImageBytes = baos.toByteArray();

                HttpHeaders headers = new HttpHeaders();
                headers.setContentLength(resizedImageBytes.length);
                headers.setContentType(MediaType.IMAGE_PNG);

                response = new ResponseEntity<>(resizedImageBytes, headers, HttpStatus.OK);

            } catch (Exception e) {
                e.printStackTrace();
                response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return response;
    }

    // 참가자2 프로필 나타내기
    @RequestMapping(value = "complete/profile/participant2", method = RequestMethod.GET)
    public ResponseEntity<byte[]> ParticpantProfile2(@RequestParam(value = "index") int index) {
        ProfileEntity profile = this.completeService.profile2(index);

        ResponseEntity<byte[]> response;
        if (profile == null) {
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            try {
                // 원본 이미지를 BufferedImage로 변환
                BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(profile.getProfileThumbnail()));

                // 새로운 크기로 이미지 조정
                int newWidth = 60;
                int newHeight = 60;
                Image resizedImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);

                // BufferedImage 생성
                BufferedImage outputImage = new BufferedImage(newWidth, newHeight, originalImage.getType());

                // Graphics2D를 사용하여 이미지 그리기
                Graphics2D graphics = outputImage.createGraphics();
                graphics.drawImage(resizedImage, 0, 0, null);
                graphics.dispose();

                // 조정된 이미지를 바이트 배열로 변환
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(outputImage, "png", baos);
                byte[] resizedImageBytes = baos.toByteArray();

                HttpHeaders headers = new HttpHeaders();
                headers.setContentLength(resizedImageBytes.length);
                headers.setContentType(MediaType.IMAGE_PNG);

                response = new ResponseEntity<>(resizedImageBytes, headers, HttpStatus.OK);

            } catch (Exception e) {
                e.printStackTrace();
                response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return response;
    }


}
