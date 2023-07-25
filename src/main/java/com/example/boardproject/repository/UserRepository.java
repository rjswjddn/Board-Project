package com.example.boardproject.repository;

import com.example.boardproject.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByUserId(String userId);
    UserEntity findByUserId(String userId);

    UserEntity findByUserSeq(Long userSeq);
}
