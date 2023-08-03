package com.example.boardproject.dto;

import com.example.boardproject.entity.UserEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserDto {

    private Long userSeq;

    private String userId;

    private String userPwd;

    private int userStatus;

    private boolean userAdmin;

    private LocalDateTime userRegDate;

    public UserDto(UserEntity userEntity){
        this.userSeq = userEntity.getUserSeq();
        this.userId = userEntity.getUserId();
        this.userPwd = userEntity.getUserPwd();
        this.userStatus = userEntity.getUserStatus();
        this.userAdmin = userEntity.isUserAdmin();
        this.userRegDate = userEntity.getUserRegDate();
    }
}
