package com.example.boardproject.repository;

import com.example.boardproject.entity.BoardCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardCommentRepository extends JpaRepository<BoardCommentEntity, Long> {

    @Query(value = "UPDATE board_comment SET comment_status = true WHERE comment_seq = :commentSeq", nativeQuery = true)
    void deleteCommentByCommentSeq(@Param("commentSeq") Long commentSeq);

    BoardCommentEntity findByCommentSeq(Long commentSeq);

}
