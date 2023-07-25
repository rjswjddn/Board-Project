package com.example.boardproject.repository;

import com.example.boardproject.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByUserId(String userId);
    UserEntity findByUserId(String userId);
    @Query("SELECT u.userSeq FROM UserEntity u WHERE u.userId = :userId")
    Long findUserSeqByUserId(@Param("userId") String userId);

    @Query("SELECT u.userAdmin FROM UserEntity u WHERE u.userId = ?1")
    int getUserAdminByUserId(String userId);






}
