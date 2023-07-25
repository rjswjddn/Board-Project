package com.example.boardproject.repository;

import com.example.boardproject.dto.BoardResponseDto;
import com.example.boardproject.entity.BoardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<BoardEntity, Long> {

    @Query("SELECT new com.example.boardproject.dto.BoardResponseDto(b.boardSeq, b.boardTitle, b.boardContent, b.boardType, b.commentCnt, b.viewCnt, b.likeCnt, b.imageYn, b.boardStatus, b.boardCreatedDate, b.boardUpdatedDate, b.userSeq, u.userId) " +
            "FROM BoardEntity b INNER JOIN UserEntity u ON b.userSeq = u.userSeq " +
            "WHERE b.boardType = 'N' " +
            "ORDER BY b.boardSeq DESC")
    List<BoardResponseDto> findNoticeBoardsWithUsers();

    @Query("SELECT new com.example.boardproject.dto.BoardResponseDto(b.boardSeq, b.boardTitle, b.boardContent, b.boardType, b.commentCnt, b.viewCnt, b.likeCnt, b.imageYn, b.boardStatus, b.boardCreatedDate, b.boardUpdatedDate, b.userSeq, u.userId) " +
            "FROM BoardEntity b INNER JOIN UserEntity u ON b.userSeq = u.userSeq " +
            "WHERE b.boardType != 'N' " +
            "ORDER BY b.boardSeq DESC")
    Page<BoardResponseDto> findNormalBoardsWithUsers(Pageable pageable);


    @Query("SELECT new com.example.boardproject.dto.BoardResponseDto(b.boardSeq, b.boardTitle, b.boardContent, b.boardType, b.commentCnt, b.viewCnt, b.likeCnt, b.imageYn, b.boardStatus, b.boardCreatedDate, b.boardUpdatedDate, b.userSeq, u.userId) " +
            "FROM BoardEntity b INNER JOIN UserEntity u ON b.userSeq = u.userSeq " +
            "WHERE u.userSeq = :userSeq " +
            "ORDER BY b.boardSeq DESC")
    Page<BoardResponseDto> findBoardByUserSeqWithUserIdOrderByBoardSeqDesc(@Param("userSeq") Long userSeq, Pageable pageable);

    @Query("SELECT new com.example.boardproject.dto.BoardResponseDto(b.boardSeq, b.boardTitle, b.boardContent, b.boardType, b.commentCnt, b.viewCnt, b.likeCnt, b.imageYn, b.boardStatus, b.boardCreatedDate, b.boardUpdatedDate, b.userSeq, u.userId) " +
            "FROM BoardEntity b INNER JOIN UserEntity u ON b.userSeq = u.userSeq " +
            "WHERE lower(b.boardContent) like %:keyword% " +
            "ORDER BY b.boardSeq DESC")
    Page<BoardResponseDto> findByBoardContentContainingIgnoreCaseOrderByBoardSeqDesc(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT new com.example.boardproject.dto.BoardResponseDto(b.boardSeq, b.boardTitle, b.boardContent, b.boardType, b.commentCnt, b.viewCnt, b.likeCnt, b.imageYn, b.boardStatus, b.boardCreatedDate, b.boardUpdatedDate, b.userSeq, u.userId) " +
            "FROM BoardEntity b INNER JOIN UserEntity u ON b.userSeq = u.userSeq " +
            "WHERE b.boardTitle LIKE %:titleKeyword% " +
            "ORDER BY b.boardSeq DESC")
    Page<BoardResponseDto> findByBoardTitleContainingIgnoreCaseOrderByBoardSeqDesc(@Param("titleKeyword") String titleKeyword, Pageable pageable);


    BoardEntity findByBoardSeq(Long boardSeq);

    @Query(value = "UPDATE board SET board_status = 0", nativeQuery = true)
    void deleteByBoardSeq(Long boardSeq);

}

