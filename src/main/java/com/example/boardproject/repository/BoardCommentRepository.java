package com.example.boardproject.repository;

import com.example.boardproject.entity.BoardCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardCommentRepository extends JpaRepository<BoardCommentEntity, Long> {
    int countByBoardSeq(Long boardSeq);
    List<BoardCommentEntity> findByBoardSeq(Long boardSeq);








}

