package com.example.boardproject.controller;

import com.example.boardproject.dto.LoginDto;
import com.example.boardproject.dto.RegisterDto;
import com.example.boardproject.dto.UserDto;
import com.example.boardproject.repository.UserRepository;
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


@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
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
    public String registerPage(Model model, HttpServletRequest httpServletRequest) {
        if (httpServletRequest.getSession(false) != null) {
            return "redirect:/board";
        }
        model.addAttribute("registerDto", new RegisterDto());
        return "register";
    }

    @PostMapping("/register")
    public String register(Model model, @Valid @ModelAttribute("registerDto") RegisterDto registerDto, Errors errors) {

        // 유효성 검사
        if (errors.hasErrors()) {
            log.info("{}", "회원가입 검증");
            model.addAttribute("registerDto", registerDto);
            return "/register";
        }

        userService.register(registerDto);
        model.addAttribute("message", "회원가입에 성공했습니다.");
        model.addAttribute("url", "/login");
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
    public String login(Model model, @Valid @ModelAttribute("loginDto") LoginDto loginDto, Errors errors, HttpServletRequest httpServletRequest) {

        log.info("로그인 컨트롤러");
        // 로그인 유효성 검사
        if (errors.hasErrors()) {
            log.info("{}", "아이디검증");
            model.addAttribute("message", errors.getFieldError().getDefaultMessage());
            model.addAttribute("url", "/login");
            return "alert";
        }

        UserDto userDto = userService.getUser(loginDto.getUserId());

        // Session 생성
        HttpSession session = httpServletRequest.getSession(true);

        // 세션에 userId를 넣어줌
        session.setAttribute("userId", userDto.getUserId());
        session.setAttribute("admin", userDto.getUserAdmin());
        session.setAttribute("userSeq", userDto.getUserSeq());

        return "redirect:/board";

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
