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

    List<BoardCommentEntity> findByBoardSeq(Long boardSeq);

    @Query(value = "UPDATE board_comment SET comment_status = true WHERE comment_seq = :commentSeq", nativeQuery = true)
    void deleteCommentByCommentSeq(@Param("commentSeq") Long commentSeq);

    BoardCommentEntity findByCommentSeq(Long commentSeq);

//    @Query("SELECT new com.example.boardproject.dto.CommentReplyResponseDto(c.boardSeq, c.commentSeq, c.commentContent, c.commentCreatedDate, c.commentStatus, c.userSeq, r.replySeq, r.replyContent, r.replyCreatedDate) " +
//            "FROM BoardCommentEntity c " +
//            "LEFT JOIN BoardReplyEntity r ON c.commentSeq = r.commentSeq AND c.boardSeq = r.boardSeq " +
//            "WHERE c.boardSeq = :boardSeq AND c.commentStatus = false AND (r.replyStatus = false OR r.replyStatus IS NULL) " +
//            "ORDER BY c.commentCreatedDate ASC, r.replyCreatedDate ASC " +
//            "LIMIT 10")
//    List<CommentReplyResponseDto> findCommentsAndRepliesByBoardSeq(@Param("boardSeq") Long boardSeq);

}
