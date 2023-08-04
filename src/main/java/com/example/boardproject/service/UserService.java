package com.example.boardproject.service;


import com.example.boardproject.dto.RegisterDto;
import com.example.boardproject.dto.UserDto;
import com.example.boardproject.entity.UserEntity;
import com.example.boardproject.repository.UserRepository;
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

    public Long findUserSeqByUserId(String userId) {
        return userRepository.findUserSeqByUserId(userId);
    }


    // seq로 유저를 찾아 dto에 저장하고 반환
    public UserDto findByUserSeq(Long userSeq) {
        UserDto userDto = new UserDto(userRepository.findByUserSeq(userSeq));
        return userDto;
    }

    public boolean getUserAdminByUserId(String userId) {
        return userRepository.getUserAdminByUserId(userId);
    }

//    public boolean isAdminUser(String userId) {
//        int userAdmin = getUserAdminByUserId(userId);
//        return userAdmin == 1;
//    }


}
