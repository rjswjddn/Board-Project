package com.example.boardproject.repository;

import com.example.boardproject.entity.BoardLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardLikeRepository extends JpaRepository<BoardLikeEntity, Long> {
    boolean existsByBoardSeqAndUserSeq(Long boardSeq, Long userSeq);
    void deleteByBoardSeqAndUserSeq(Long boardSeq, Long userSeq);
    int countByBoardSeq(Long boardSeq);
    @Query(value = "DELETE FROM board_like WHERE board_seq = :boardSeq", nativeQuery = true)
    void deleteAllByBoardSeq(@Param("boardSeq") Long boardSeq);



}
