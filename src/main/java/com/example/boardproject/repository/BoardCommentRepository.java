package com.example.boardproject.repository;

import com.example.boardproject.entity.BoardCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardCommentRepository extends JpaRepository<BoardCommentEntity, Long> {
    int countByBoardSeq(Long boardSeq);

    @Query(value="SELECT * FROM board_comment WHERE comment_status = false and board_seq = :boardSeq" , nativeQuery = true)
    List<BoardCommentEntity> findByBoardSeq(@Param("boardSeq") Long boardSeq);

    @Query(value = "UPDATE board_comment SET comment_status = true WHERE comment_seq = :commentSeq", nativeQuery = true)
    void deleteCommentByCommentSeq(@Param("commentSeq") Long commentSeq);

    BoardCommentEntity findByCommentSeq(Long commentSeq);

    @Query(value = "SELECT board_comment.user_seq FROM board_comment WHERE comment_seq = :commentSeq", nativeQuery = true)
    Long findUserSeqByCommentSeq(@Param("commentSeq") Long commentSeq);

    @Query(value = "SELECT board_comment.comment_content FROM board_comment WHERE comment_seq = :commentSeq", nativeQuery = true)
    String findCommentContentByCommentSeq(@Param("commentSeq") Long commentSeq);
}
