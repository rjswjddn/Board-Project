package com.example.boardproject.repository;

import com.example.boardproject.entity.BoardReplyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardReplyRepository extends JpaRepository<BoardReplyEntity, Long> {
    List<BoardReplyEntity> findByCommentSeq(Long commentSeq);

}
