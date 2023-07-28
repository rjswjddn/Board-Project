package com.example.boardproject.controller;

import com.example.boardproject.dto.LoginDto;
import com.example.boardproject.dto.RegisterDto;
import com.example.boardproject.dto.UserDto;
import com.example.boardproject.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;


@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    // 회원가입 페이지
    @GetMapping("/register")
    public String registerPage(Model model, HttpServletRequest httpServletRequest) {
        if (httpServletRequest.getSession(false) != null) {
            return "redirect:/board";
        }
        model.addAttribute("registerDto", new RegisterDto());
        return "register";
    }

    @PostMapping("/register")
    public String register(Model model, @Valid @ModelAttribute("registerDto") RegisterDto registerDto, Errors errors) {

        String message = "";
        String url = "";

        log.info("registerDto = {}", registerDto);
        if (errors.hasErrors()) {
            Map<String, String> result = userService.registerHandler(errors);
            for (String key : result.keySet()) {
                model.addAttribute(key, result.get(key));
            }
            return "register";
        }

        switch (userService.checkRegister(registerDto)) {
            case PASS:
                userService.register(registerDto);
                message = "회원가입에 성공했습니다.";
                url = "/login";
                break;

            case ID_DUP:
                message = "같은 아이디가 존재합니다.";
                url = "/register";
                break;
        }

        model.addAttribute("message", message);
        model.addAttribute("url", url);
        return "alert";
    }

    // 로그인
    // 로그인 상태면 index 로 이동
    @GetMapping("/login")
    public String loginPage(Model model, HttpServletRequest httpServletRequest) {
        if (httpServletRequest.getSession(false) != null) {
            return "redirect:/board";
        }
        model.addAttribute("loginDto", new LoginDto());
        return "/login";
    }


    // 로그인
    // 로그인에 실패하면 실패 사유를 alert 창으로 나타냄
    // 성공하면 index 로 이동함
    @PostMapping("/login")
    public String login(@ModelAttribute LoginDto loginDto, HttpServletRequest httpServletRequest, Model model) {

        UserDto userDto = userService.getUser(loginDto.getUserId());

        String message = "";
        String url = "";

        switch (userService.checkLogin(userDto, loginDto)) {
            case PASS:
                // Session 이 없으면 생성
                HttpSession session = httpServletRequest.getSession(true);
                // 세션에 userId를 넣어줌
                session.setAttribute("userId", userDto.getUserId());
                session.setAttribute("admin", userDto.getUserAdmin());
                return "redirect:/board";

            case ID_NULL:
                message = "아이디를 입력해주세요.";
                url = "/login";
                break;

            case PWD_NULL:
                message = "비밀번호를 입력해주세요.";
                url = "/login";
                break;

            case ID_MISS:
                message = "존재하지 않는 아이디입니다.";
                url = "/login";
                break;

            case PWD_MISS:
                message = "비밀번호를 확인 해주세요.";
                url = "/login";
                break;
        }

        model.addAttribute("message", message);
        model.addAttribute("url", url);
        return "alert";
    }


    // 로그아웃
    // session 을 제거하고 login 으로 이동
    @GetMapping("/logout")
    public String logout(Model model, HttpServletRequest httpServletRequest) {
        HttpSession session = httpServletRequest.getSession(false);
        session.invalidate();
        model.addAttribute("message", "로그아웃 되었습니다.");
        model.addAttribute("url", "/login");
        return "alert";

    }
}
