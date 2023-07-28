package com.example.boardproject.validator;

import com.example.boardproject.dto.LoginDto;
import com.example.boardproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@RequiredArgsConstructor
@Component
public class LoginUserIdValidator implements Validator {

    private final UserRepository userRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(LoginDto.class);
    }

    @Override
    public void validate(Object object, Errors errors) {
        LoginDto loginDto = (LoginDto) object;
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if (!StringUtils.hasText(loginDto.getUserId())) {
            errors.rejectValue("userId", "아이디 공백", "아이디를 입력해주세요");
        } else if (!StringUtils.hasText(loginDto.getUserPwd())) {
            errors.rejectValue("userPwd", "비밀번호 공백", "비밀번호를 입력해주세요");
        } else if (!userRepository.existsByUserId(loginDto.getUserId())) {
            errors.rejectValue("userId", "없는 아이디", "아이디를 확인해주세요");
        } else if (!encoder.matches(loginDto.getUserPwd(), userRepository.getUserPwdByUserId(loginDto.getUserId()))) {
            errors.rejectValue("userPwd", "틀린 비밀번호", "비밀번호를 확인해주세요");
        }
    }
}