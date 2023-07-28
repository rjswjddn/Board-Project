package com.example.boardproject.service;

import com.example.boardproject.dto.BoardRequestDto;
import com.example.boardproject.dto.BoardResponseDto;
import com.example.boardproject.entity.BoardEntity;
import com.example.boardproject.entity.BoardImageEntity;
import com.example.boardproject.entity.UserEntity;
import com.example.boardproject.repository.BoardImageRepository;
import com.example.boardproject.repository.BoardRepository;
import com.example.boardproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class BoardService {
    @Value("${upload.directory}")
    private String uploadDirectory;

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final BoardImageRepository boardImageRepository;

    //공지글
    public List<BoardResponseDto> getNoticeBoardsWithUsers() {
        List<BoardResponseDto> noticeBoards = boardRepository.findNoticeBoardsWithUsers();
        setNewBoards(noticeBoards);
        return noticeBoards;
    }

    //일반글 페이징 조회
    public Page<BoardResponseDto> getAllNormalBoardsWithUsers(Pageable pageable) {
        Page<BoardResponseDto> normalBoards = boardRepository.findNormalBoardsWithUsers(pageable);
        setNewBoards(normalBoards.getContent());
        return normalBoards;
    }

    //신규 등록 게시물 표시
    private void setNewBoards(List<BoardResponseDto> boards) {
        LocalDateTime currentTime = LocalDateTime.now();
        for (BoardResponseDto boardResponseDto : boards) {
            LocalDateTime boardCreatedDate = boardResponseDto.getBoardCreatedDate();
            if (boardCreatedDate != null) {
                Duration duration = Duration.between(boardCreatedDate, currentTime);
                if (duration.toHours() < 24) {
                    boardResponseDto.setIsNew(true);
                }
            }
        }
    }

    //검색
    public Page<BoardResponseDto> searchByTitle(String title, Pageable pageable) {
        return boardRepository.findByBoardTitleContainingIgnoreCaseOrderByBoardSeqDesc(title, pageable);
    }

    public Page<BoardResponseDto> searchByContent(String content, Pageable pageable) {
        return boardRepository.findByBoardContentContainingIgnoreCaseOrderByBoardSeqDesc(content, pageable);
    }

    public Page<BoardResponseDto> searchByUserId(String userId, Pageable pageable) {
        UserEntity user = userRepository.findByUserId(userId);
        if (user != null) {
            return boardRepository.findBoardByUserSeqWithUserIdOrderByBoardSeqDesc(user.getUserSeq(), pageable);
        } else {
            return new PageImpl<>(Collections.emptyList());
        }
    }

    //검색 + 신규 등록 게시물 표시 뜨도록
    public Page<BoardResponseDto> searchBoards(String searchType, String keyword, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("boardSeq").descending());
        Page<BoardResponseDto> searchResult;

        if ("boardTitle".equals(searchType)) {
            searchResult = searchByTitle(keyword, pageable);
        } else if ("userSeq".equals(searchType)) {
            searchResult = searchByUserId(keyword, pageable);
        } else if ("boardContent".equals(searchType)) {
            searchResult = searchByContent(keyword, pageable);
        } else {
            searchResult = new PageImpl<>(Collections.emptyList());
        }

        setNewBoards(searchResult.getContent());
        return searchResult;
    }



    //게시물 등록
    @Transactional
    public void registerBoard(BoardRequestDto boardRequestDto, MultipartFile file) throws IOException {
        BoardEntity boardEntity = boardRequestDto.toEntity();
        log.info(boardEntity.toString());

        // 이미지 파일 업로드 처리
        if (!file.isEmpty()) {
            // 파일 크기 확인
            if (file.getSize() > 20 * 1024 * 1024) {
                throw new IOException("파일 크기는 최대 20MB까지 허용됩니다.");
            }

            // 파일 확장자 확인 (이미지 파일 여부 체크)
            String[] allowedExtensions = {"jpg", "jpeg", "png", "gif"};
            String fileExtension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1).toLowerCase();
            boolean isAllowedExtension = Arrays.stream(allowedExtensions).anyMatch(extension -> extension.equals(fileExtension));
            if (!isAllowedExtension) {
                throw new IOException("이미지 파일만 업로드 가능합니다.");
            }

            // 이미지 파일 저장 경로 설정
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            String imagePath = uploadDirectory + fileName;
            Path imageFilePath = Paths.get(imagePath);
            Files.createDirectories(imageFilePath.getParent());
            Files.write(imageFilePath, file.getBytes());

            // 이미지 정보 저장 (board_image)
            BoardImageEntity boardImage = new BoardImageEntity();
            boardImage.setBoardSeq(boardEntity.getBoardSeq());
            boardImage.setImagePath(imagePath);
            boardImage.setImageName(file.getOriginalFilename());
            boardImageRepository.save(boardImage);

            // board 테이블의 imageYn 속성을 true로 설정
            boardEntity.setImageYn(true);
        }
        boardRepository.save(boardEntity);
    }

    public void validateBoardRequest(BoardRequestDto boardRequestDto, BindingResult bindingResult) {
        if (boardRequestDto.getBoardTitle().isEmpty()) {
            bindingResult.rejectValue("boardTitle", "empty.boardTitle", "제목 미입력");
        }
        if (boardRequestDto.getBoardContent().isEmpty()) {
            bindingResult.rejectValue("boardContent", "empty.boardContent", "내용 미입력");
        }

    }

    // Seq로 게시물 찾고 dto로 변환하여 return
    public BoardResponseDto findByBoardSeq(Long boardSeq) {
        BoardResponseDto boardResponseDto = new BoardResponseDto(boardRepository.findByBoardSeq(boardSeq));
        return boardResponseDto;

    }
        

    // Seq로 게시물을 찾아 board_status를 0으로 변경
    public void deleteBoard(Long boardSeq) {
        boardRepository.deleteByBoardSeq(boardSeq);
    }

    // 게시물 찾아 수정하고 저장
    public void updateBoard(Long boardSeq, BoardRequestDto boardRequestDto) {
        BoardEntity boardEntity = boardRepository.findByBoardSeq(boardSeq);
        boardEntity.setBoardType(boardRequestDto.getBoardType());
        boardEntity.setBoardTitle(boardRequestDto.getBoardTitle());
        boardEntity.setBoardContent(boardRequestDto.getBoardContent());

        boardRepository.save(boardEntity);
    }


}
