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

    //공지 게시물 + userId
    @Query("SELECT new com.example.boardproject.dto.BoardResponseDto(b.boardSeq, b.boardTitle, b.boardContent, b.boardType, b.commentCnt, b.viewCnt, b.likeCnt, b.imageYn, b.boardStatus, b.boardCreatedDate, b.boardUpdatedDate, b.userSeq, u.userId) " +
            "FROM BoardEntity b INNER JOIN UserEntity u ON b.userSeq = u.userSeq " +
            "WHERE b.boardType = 'N' AND b.boardStatus = false " +
            "ORDER BY b.boardSeq DESC " +
            "LIMIT 5")
    List<BoardResponseDto> findNoticeBoardsWithUsers();

    //공지 아닌 게시물 + userId
    @Query("SELECT new com.example.boardproject.dto.BoardResponseDto(b.boardSeq, b.boardTitle, b.boardContent, b.boardType, b.commentCnt, b.viewCnt, b.likeCnt, b.imageYn, b.boardStatus, b.boardCreatedDate, b.boardUpdatedDate, b.userSeq, u.userId) " +
            "FROM BoardEntity b INNER JOIN UserEntity u ON b.userSeq = u.userSeq " +
            "WHERE b.boardType != 'N' AND b.boardStatus = false " +
            "ORDER BY b.boardSeq DESC")
    Page<BoardResponseDto> findNormalBoardsWithUsers(Pageable pageable);


    //(board tbl)userSeq로 (user tbl)userId 가져와서 userId로 검색
    @Query("SELECT new com.example.boardproject.dto.BoardResponseDto(b.boardSeq, b.boardTitle, b.boardContent, b.boardType, b.commentCnt, b.viewCnt, b.likeCnt, b.imageYn, b.boardStatus, b.boardCreatedDate, b.boardUpdatedDate, b.userSeq, u.userId) " +
            "FROM BoardEntity b INNER JOIN UserEntity u ON b.userSeq = u.userSeq " +
            "WHERE u.userSeq = :userSeq " +
            "ORDER BY b.boardSeq DESC")
    Page<BoardResponseDto> findBoardByUserSeqWithUserIdOrderByBoardSeqDesc(@Param("userSeq") Long userSeq, Pageable pageable);

    //boardContent로 검색
    @Query("SELECT new com.example.boardproject.dto.BoardResponseDto(b.boardSeq, b.boardTitle, b.boardContent, b.boardType, b.commentCnt, b.viewCnt, b.likeCnt, b.imageYn, b.boardStatus, b.boardCreatedDate, b.boardUpdatedDate, b.userSeq, u.userId) " +
            "FROM BoardEntity b INNER JOIN UserEntity u ON b.userSeq = u.userSeq " +
            "WHERE b.boardType != 'S' AND b.boardStatus = false AND lower(b.boardContent) like %:keyword% " +
            "ORDER BY b.boardSeq DESC")
    Page<BoardResponseDto> findByBoardContentContainingIgnoreCaseOrderByBoardSeqDesc(@Param("keyword") String keyword, Pageable pageable);

    //boardTitle로 검색
    @Query("SELECT new com.example.boardproject.dto.BoardResponseDto(b.boardSeq, b.boardTitle, b.boardContent, b.boardType, b.commentCnt, b.viewCnt, b.likeCnt, b.imageYn, b.boardStatus, b.boardCreatedDate, b.boardUpdatedDate, b.userSeq, u.userId) " +
            "FROM BoardEntity b INNER JOIN UserEntity u ON b.userSeq = u.userSeq " +
            "WHERE b.boardType != 'S' AND b.boardStatus = false AND b.boardTitle LIKE %:titleKeyword% " +
            "ORDER BY b.boardSeq DESC")
    Page<BoardResponseDto> findByBoardTitleContainingIgnoreCaseOrderByBoardSeqDesc(@Param("titleKeyword") String titleKeyword, Pageable pageable);


    BoardEntity findByBoardSeq(Long boardSeq);

    @Query(value = "UPDATE board SET board_status = true WHERE board_seq = :boardSeq", nativeQuery = true)
    void deleteByBoardSeq(@Param("boardSeq") Long boardSeq);

    @Query(value ="UPDATE board SET view_cnt = view_cnt + 1 WHERE board_seq = :boardSeq", nativeQuery = true)
    void updateViewCntByBoardSeq(@Param("boardSeq") Long boardSeq);

}

