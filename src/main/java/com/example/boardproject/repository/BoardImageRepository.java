package com.example.boardproject.repository;

import com.example.boardproject.entity.BoardImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardImageRepository extends JpaRepository<BoardImageEntity, Long> {

}
