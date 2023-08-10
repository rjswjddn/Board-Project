package com.example.boardproject.service;


import com.example.boardproject.dto.LoginDto;
import com.example.boardproject.dto.RegisterDto;
import com.example.boardproject.dto.UserDto;
import com.example.boardproject.entity.UserEntity;
import com.example.boardproject.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;


    // 회원가입
    // 화면에서 RegisterDto(아이디, 비밀번호) 를 입력받아 UserEntity로 변환하여 저장
    // LoginId 중복체크는 checkLoginId
    public void register(RegisterDto registerDto) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPwd = encoder.encode(registerDto.getUserPwd());
        registerDto.setUserPwd(encodedPwd);
        userRepository.save(registerDto.toEntity());
    }

    public void login (LoginDto loginDto, HttpServletRequest httpServletRequest){

        UserEntity userEntity = userRepository.findByUserId(loginDto.getUserId());

        // Session 생성
        HttpSession session = httpServletRequest.getSession(true);

        //세션 유지시간 설정
        session.setMaxInactiveInterval(3600);

        // 세션에 userId를 넣어줌
        session.setAttribute("userId", userEntity.getUserId());
        session.setAttribute("userSeq", userEntity.getUserSeq());
        session.setAttribute("admin", userEntity.isUserAdmin());
    }

    public void logout (HttpServletRequest httpServletRequest){
        HttpSession session = httpServletRequest.getSession(false);
        session.invalidate();
    }

    // seq로 유저를 찾아 dto에 저장하고 반환
    public UserDto findByUserSeq(Long userSeq) {
        UserDto userDto = new UserDto(userRepository.findByUserSeq(userSeq));
        return userDto;
    }
}
