package com.example.boardproject.controller;

import com.example.boardproject.dto.LoginDto;
import com.example.boardproject.dto.RegisterDto;
import com.example.boardproject.dto.UserDto;
import com.example.boardproject.service.UserService;
import com.example.boardproject.validator.LoginUserIdValidator;
import com.example.boardproject.validator.RegisterUserIdValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;


@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final RegisterUserIdValidator registerUserIdValidator;
    private final LoginUserIdValidator loginUserIdValidator;

    // 회원가입 유효성 검사
    @InitBinder("registerDto")
    public void registerValidator(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(registerUserIdValidator);
    }

    // 로그인 유효성 검사
    @InitBinder("loginDto")
    public void loginValidator(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(loginUserIdValidator);
    }


    // 회원가입 페이지
    @GetMapping("/register")
    public ModelAndView registerPage(HttpServletRequest httpServletRequest) {

        ModelAndView mv = new ModelAndView();

        if (httpServletRequest.getSession(false) != null) {
            mv.setViewName("redirect:/board");
        } else {
            mv.setViewName("register");
            mv.addObject("registerDto", new RegisterDto());
        }

        return mv;
    }

    @PostMapping("/register")
    public ModelAndView register(Model model, @Valid @ModelAttribute("registerDto") RegisterDto registerDto, Errors errors) {

        ModelAndView mv = new ModelAndView();

        // 유효성 검사
        if (errors.hasErrors()) {
            log.info("회원가입 에러 : {}", errors.getFieldErrors());
            mv.setViewName("/register");
            mv.addObject("registerDto", registerDto);

        } else {
            userService.register(registerDto);
            mv.setViewName("/alert");
            mv.addObject("message", "회원가입에 성공했습니다.");
            mv.addObject("url", "/login");
        }

        return mv;
    }

    // 로그인
    // 로그인 상태면 index 로 이동
    @GetMapping("/login")
    public ModelAndView loginPage(HttpServletRequest httpServletRequest) {

        ModelAndView mv = new ModelAndView();

        if (httpServletRequest.getSession(false) != null) {
            mv.setViewName("redirect:/board");
        } else {
            mv.setViewName("/login");
            mv.addObject("loginDto", new LoginDto());
        }

        return mv;
    }


    // 로그인
    // 로그인에 실패하면 실패 사유를 alert 창으로 나타냄
    // 성공하면 index 로 이동함
    @PostMapping("/login")
    public ModelAndView login( @Valid @ModelAttribute("loginDto") LoginDto loginDto, Errors errors, HttpServletRequest httpServletRequest) {

        ModelAndView mv = new ModelAndView();

        log.info("로그인 컨트롤러");
        // 로그인 유효성 검사
        if (errors.hasErrors()) {
            log.info("{}", "아이디검증");
            mv.setViewName("/alert");
            mv.addObject("message", errors.getFieldError().getDefaultMessage());
            mv.addObject("url", "/login");
            return mv;
        } else {
            UserDto userDto = userService.getUser(loginDto.getUserId());

            // Session 생성
            HttpSession session = httpServletRequest.getSession(true);

            //세션 유지시간 설정
            session.setMaxInactiveInterval(3600);

            // 세션에 userId를 넣어줌
            session.setAttribute("userId", userDto.getUserId());
            session.setAttribute("userSeq", userDto.getUserSeq());
            session.setAttribute("admin", userDto.isUserAdmin());

            mv.setViewName("redirect:/board");
        }

        return mv;

    }


    // 로그아웃
    // session 을 제거하고 login 으로 이동
    @GetMapping("/logout")
    public ModelAndView logout(HttpServletRequest httpServletRequest) {
        ModelAndView mv = new ModelAndView();
        HttpSession session = httpServletRequest.getSession(false);
        session.invalidate();
        mv.setViewName("alert");
        mv.addObject("message", "로그아웃 되었습니다.");
        mv.addObject("url", "/login");

        return mv;

    }
}
