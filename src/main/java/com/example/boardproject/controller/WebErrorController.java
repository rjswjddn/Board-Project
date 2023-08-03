package com.example.boardproject.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@Slf4j
public class WebErrorController implements ErrorController {

    @GetMapping("/error")
    public ModelAndView handleError(HttpServletRequest httpServletRequest, ModelAndView mv){

        mv.setViewName("/alert");
        mv.addObject("url", "/board");

        Object status = httpServletRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if(status != null){
            int statusCode = Integer.valueOf(status.toString());
            log.info("에러발생 {}", statusCode);
            if(statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                mv.addObject("message", status.toString());
                return mv;
            } else {
                if(httpServletRequest.getHeader("Referer") != null){
                    log.info("이전페이지 이동 {}", httpServletRequest.getHeader("Referer"));
                    mv.addObject("url", httpServletRequest.getHeader("Referer"));
                }
                mv.addObject("message", status.toString());
            }
        }

        return mv;
    }
}
