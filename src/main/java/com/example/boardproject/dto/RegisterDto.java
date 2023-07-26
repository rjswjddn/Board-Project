package com.example.boardproject.dto;

import com.example.boardproject.entity.UserEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RegisterDto {

    @Pattern(regexp = "^[a-z0-9]{4,20}$", message = "영어 소문자와 숫자로 4~20자를 입력해주세요.")
    private String userId;

    @NotBlank(message = "비밀번호를 입력 해주세요.")
    private String userPwd;

    public UserEntity toEntity(){
        return UserEntity.builder()
                .userId(this.userId)
                .userPwd(this.userPwd)
                .userStatus(1)
                .userAdmin(0)
                .build();
    }
}
