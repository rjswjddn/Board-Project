package com.example.boardproject.repository;

import com.example.boardproject.dto.BoardReplyResponseDto;
import com.example.boardproject.entity.BoardReplyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardReplyRepository extends JpaRepository<BoardReplyEntity, Long> {
    BoardReplyEntity findByReplySeq(Long commentSeq);

    @Query("SELECT new com.example.boardproject.dto.BoardReplyResponseDto(b.replySeq, b.replyContent, b.replyCreatedDate, b.replyUpdatedDate, b.replyStatus, b.commentSeq, b.boardSeq, u.userId) " +
            "FROM BoardReplyEntity b INNER JOIN UserEntity u ON b.userSeq = u.userSeq " +
            "WHERE b.replyStatus = false AND b.boardSeq = :boardSeq " +
            "ORDER BY b.boardSeq DESC ")
    List<BoardReplyResponseDto> findBoardReplyWithUserId(@Param("boardSeq") Long boardSeq);

    @Query(value="SELECT * FROM board_reply WHERE reply_status = false and board_seq = :boardSeq" , nativeQuery = true)
    List<BoardReplyEntity> findByBoardSeq(@Param("boardSeq") Long boardSeq);

    @Query(value = "UPDATE board_reply SET reply_status = true WHERE board_seq = :boardSeq", nativeQuery = true)
    void deleteByBoardSeq(Long boardSeq);

    @Query(value = "SELECT board_reply.user_seq FROM board_reply WHERE reply_seq = :replySeq", nativeQuery = true)
    Long findUserSeqByReplySeq(@Param("replySeq") Long replySeq);

    @Query(value = "UPDATE board_reply SET reply_status = true WHERE reply_seq = :replySeq", nativeQuery = true)
    void deleteReplyByReplySeq(@Param("replySeq") Long replySeq);


}
