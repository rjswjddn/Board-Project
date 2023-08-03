package com.example.boardproject.repository;

import com.example.boardproject.entity.BoardImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardImageRepository extends JpaRepository<BoardImageEntity, Long> {
    @Query(value = "SELECT board_image.image_path FROM board_image WHERE board_seq=:boardSeq", nativeQuery = true)
    String getImagePathByBoardSeq(@Param("boardSeq") Long boardSeq);

    BoardImageEntity findByBoardSeq(Long boardSeq);
    void deleteByBoardSeq(Long boardSeq);

}
