package com.example.boardproject.validator;

import com.example.boardproject.dto.RegisterDto;
import com.example.boardproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@RequiredArgsConstructor
@Component
@Slf4j
public class RegisterUserIdValidator implements Validator {

    private final UserRepository userRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(RegisterDto.class);
    }

    @Override
    public void validate(Object object, Errors errors) {
        RegisterDto registerDto = (RegisterDto) object;
        if (userRepository.existsByUserId(registerDto.getUserId())) {
            log.info("아이디 중복");
            errors.rejectValue("userId", "아이디 중복", "이미 사용중인 아이디 입니다.");
        }
    }
}
