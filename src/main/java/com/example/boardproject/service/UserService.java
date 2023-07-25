package com.example.boardproject.service;


import com.example.boardproject.dto.LoginDto;
import com.example.boardproject.dto.RegisterDto;
import com.example.boardproject.dto.UserDto;
import com.example.boardproject.entity.UserEntity;
import com.example.boardproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    // 회원가입 확인
    public RegisterEnum checkRegister(RegisterDto registerDto) {
        RegisterEnum registerEnum = RegisterEnum.PASS;
        if (userRepository.existsByUserId(registerDto.getUserId())) {
            registerEnum = RegisterEnum.ID_DUP;
        }

        return registerEnum;
    }

    // 회원가입
    // 화면에서 RegisterDto(아이디, 비밀번호) 를 입력받아 UserEntity로 변환하여 저장
    // LoginId 중복체크는 checkLoginId
    public void register(RegisterDto registerDto) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPwd = encoder.encode(registerDto.getUserPwd());
        registerDto.setUserPwd(encodedPwd);
        userRepository.save(registerDto.toEntity());
    }


    // login 확인
    // 올바른 로그인이면 PASS, 아이디나 비밀번호가 틀리면 해당하는 오류 enum return
    public LoginEnum checkLogin(UserDto userDto, LoginDto loginDto) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        LoginEnum loginEnum = LoginEnum.PASS;

        if (!StringUtils.hasText(loginDto.getUserId())) {
            loginEnum = LoginEnum.ID_NULL;
        } else if (!StringUtils.hasText(loginDto.getUserPwd())) {
            loginEnum = LoginEnum.PWD_NULL;
        } else if (userDto == null) {
            loginEnum = LoginEnum.ID_MISS;
        } else if (!encoder.matches(loginDto.getUserPwd(), userDto.getUserPwd())) {
            loginEnum = LoginEnum.PWD_MISS;
        }

        return loginEnum;
    }


    // 로그인
    // 로그인 Password가 일치하는지 확인
    // 일치하면 입력받은 UserDto, 일치하지 않으면 null return
    public UserDto getUser(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId);
        if (userEntity == null) {
            return null;
        }
        UserDto userDto = new UserDto(userEntity);
        return userDto;
    }


    public Map<String, String> registerHandler(Errors errors) {
        Map<String, String> result = new HashMap<>();

        for (FieldError error : errors.getFieldErrors()) {
            String keyName = String.format("valid_%s", error.getField());
            result.put(keyName, error.getDefaultMessage());
        }


        return result;
    }


    // seq로 유저를 찾아 dto에 저장하고 반환
    public UserDto findByUserSeq(Long userSeq) {
        UserDto userDto = new UserDto(userRepository.findByUserSeq(userSeq));
        return userDto;
    }


}
