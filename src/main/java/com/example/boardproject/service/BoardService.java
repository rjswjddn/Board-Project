package com.example.boardproject.service;

import com.example.boardproject.ConstantClass.Constants;
import com.example.boardproject.ConstantClass.ValidationConstants;
import com.example.boardproject.dto.*;
import com.example.boardproject.entity.*;
import com.example.boardproject.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class BoardService {
    @Value("D:/projects/image/")
    private String uploadDirectory;

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final BoardImageRepository boardImageRepository;
    private final BoardLikeRepository boardLikeRepository;
    private final BoardCommentRepository boardCommentRepository;
    private final BoardReplyRepository boardReplyRepository;

    //공지글
    public List<BoardResponseDto> getNoticeBoardsWithUsers() {
        return boardRepository.findNoticeBoardsWithUsers();
    }

    //일반글 페이징 조회
    public Page<BoardResponseDto> getAllNormalBoardsWithUsers(Pageable pageable) {
        return boardRepository.findNormalBoardsWithUsers(pageable);
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

        return searchResult;
    }

    // 이미지 저장 메소드
    public void saveImage(MultipartFile file, BoardEntity boardEntity ) throws IOException{
        if (file.getSize() > 20 * 1024 * 1024) {
            throw new IOException("파일 크기는 최대 20MB까지 허용됩니다.");
        }

        // 파일 확장자 확인 (이미지 파일 여부 체크)
        String fileExtension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1).toLowerCase();
        boolean isAllowedExtension = Arrays.stream(Constants.ALLOWED_EXTENSIONS).anyMatch(extension -> extension.equals(fileExtension));
        if (!isAllowedExtension) {

            throw new IOException("이미지 파일만 업로드 가능합니다.");
        }

        // 이미지 파일 저장 경로 설정
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename().replace("//", "");
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

    //게시물 등록
    @Transactional
    public void registerBoard(BoardRequestDto boardRequestDto, MultipartFile file) throws IOException {
        BoardEntity boardEntity = boardRequestDto.toEntity();
        boardRepository.save(boardEntity);

        // 이미지 파일 업로드 처리
        if (!file.isEmpty()) {
            // 파일 크기 확인
            saveImage(file, boardEntity);
        }

    }


    public boolean isValidBoardTitle(String boardTitle) {
        return StringUtils.hasText(boardTitle) && boardTitle.length() <= ValidationConstants.MAX_BOARD_TITLE_LENGTH;
    }

    public boolean isValidContent(String boardContent) {
        return StringUtils.hasText(boardContent) && boardContent.length() <= ValidationConstants.MAX_BOARD_CONTENT_LENGTH;
    }

    // 유효성 검사
    public void validateBoardRequest(BoardRequestDto boardRequestDto, BindingResult bindingResult) throws IOException {
        if (!isValidBoardTitle(boardRequestDto.getBoardTitle())) {
            bindingResult.rejectValue("boardTitle", "empty.boardTitle", ValidationConstants.BOARD_TITLE_ERROR_MESSAGE);
            throw new IOException(ValidationConstants.BOARD_TITLE_ERROR_MESSAGE);
        }
        if (!isValidContent(boardRequestDto.getBoardContent())) {
            bindingResult.rejectValue("boardContent", "invalid.boardContent", ValidationConstants.BOARD_CONTENT_ERROR_MESSAGE);
            throw new IOException(ValidationConstants.BOARD_CONTENT_ERROR_MESSAGE);
        }
    }


    // Seq로 게시물 찾고 dto로 변환하여 return
    public BoardResponseDto findByBoardSeq(Long boardSeq) {
        BoardResponseDto boardResponseDto = new BoardResponseDto(boardRepository.findByBoardSeq(boardSeq));
        return boardResponseDto;

    }



    // Seq로 게시물을 찾아 board_status를 0으로 변경
    @Transactional
    public void deleteBoard(Long boardSeq) {
        boardRepository.deleteByBoardSeq(boardSeq);
        boardLikeRepository.deleteAllByBoardSeq(boardSeq);
        boardImageRepository.deleteByBoardSeq(boardSeq);
    }

    // 게시물 찾아 수정하고 저장
    @Transactional
    public void updateBoard(Long boardSeq, BoardRequestDto boardRequestDto, MultipartFile file) throws IOException {
        BoardEntity boardEntity = boardRepository.findByBoardSeq(boardSeq);
        boardEntity.setBoardType(boardRequestDto.getBoardType());
        boardEntity.setBoardTitle(boardRequestDto.getBoardTitle());
        boardEntity.setBoardContent(boardRequestDto.getBoardContent());
        boardEntity.setImageYn(boardRequestDto.getImageYn());

        // 이미지 파일 업로드 처리
        if (boardEntity.isImageYn()&&!file.isEmpty()) {
            // 새로운 이미지를 등록 했을 때
            boardImageRepository.deleteByBoardSeq(boardSeq);
            saveImage(file, boardEntity);
            // imageYn이 그대로이고 file이 비어있으면 기존 이미지 유지
        } else {
            boardImageRepository.deleteByBoardSeq(boardSeq);
            if (!file.isEmpty()) {
                saveImage(file, boardEntity);
            }
        }


        boardRepository.save(boardEntity);
    }

    public String getImagePathByBoardSeq(Long boardSeq) {
        return boardImageRepository.getImagePathByBoardSeq(boardSeq);
    }


    //좋아요 기능
    @Transactional
    public String toggleLike(Long boardSeq, Long userSeq) throws IOException {
        BoardEntity boardEntity = boardRepository.findByBoardSeq(boardSeq);
        if (boardEntity == null) {
            // DB에 해당 boardSeq가 존재하지 않는 경우
            throw new IOException("존재하지 않는 게시물입니다.");
        }

        boolean alreadyLiked = boardLikeRepository.existsByBoardSeqAndUserSeq(boardSeq, userSeq);
        if (alreadyLiked) {
            boardLikeRepository.deleteByBoardSeqAndUserSeq(boardSeq, userSeq);
            boardEntity.setLikeCnt(boardEntity.getLikeCnt() - 1);
        } else {
            BoardLikeEntity boardLikeEntity = new BoardLikeEntity();
            boardLikeEntity.setBoardSeq(boardSeq);
            boardLikeEntity.setUserSeq(userSeq);
            boardLikeRepository.save(boardLikeEntity);
            boardEntity.setLikeCnt(boardEntity.getLikeCnt() + 1);
        }

        // 좋아요 추가 또는 취소 후 boardEntity의 like_cnt 값에 반영
        boardRepository.save(boardEntity);

        return alreadyLiked ? "disliked" : "liked";
    }


    // 사용자가 해당 게시글에 좋아요를 눌렀는지 확인하는 메서드
    public boolean isLiked(Long boardSeq, Long userSeq) {
        return boardLikeRepository.existsByBoardSeqAndUserSeq(boardSeq, userSeq);
    }

    // 게시글별 좋아요 갯수 가져오기
    public int getLikeCount(Long boardSeq) {
        return boardLikeRepository.countByBoardSeq(boardSeq);
    }

    // 조회수
    public void updateViewCnt(Long boardSeq) {
        boardRepository.updateViewCntByBoardSeq(boardSeq);
    }



    // 댓글 작성
    @Transactional
    public void registerBoardComment(BoardCommentRequestDto commentDto) {
        BoardCommentEntity commentEntity = commentDto.toEntity();
        boardCommentRepository.save(commentEntity);
        updateCommentCount(commentDto.getBoardSeq());
    }

    // 게시글 별 댓글 가져오기
    public List<BoardCommentEntity> getCommentsByBoardSeq(Long boardSeq) {
        return boardCommentRepository.findByBoardSeq(boardSeq);
    }

    public List<BoardCommentResponseDto> getCommentsByBoardSeqWithUserId(Long boardSeq) {
        // boardSeq로 댓글 리스트로 가져오기
        // boardSeq로 대댓글 리스트로 가져오기 (user id가 필요한 경우 여기서 추가해서 붙이기)
        // 대댓글 리스트의 숫자만큼 반복돌면서 댓글 리스트에 리스트를 붙이기
        List<BoardCommentResponseDto> boardCommentResponseDtoList = boardCommentRepository.findBoardCommentsWithUserId(boardSeq);
        List<BoardReplyResponseDto> boardReplyResponseDtoList = boardReplyRepository.findBoardReplyWithUserId(boardSeq);

        for (BoardReplyResponseDto boardReplyResponseDto : boardReplyResponseDtoList) {
            Long commentSeq = boardReplyResponseDto.getCommentSeq();
            for (BoardCommentResponseDto boardCommentResponseDto : boardCommentResponseDtoList) {
                if (Objects.equals(boardCommentResponseDto.getCommentSeq(), commentSeq)) {
                    boardCommentResponseDto.getBoardReplyResponseDtoList().add(boardReplyResponseDto);
                    break;
                }
            }
        }

        log.info(boardCommentResponseDtoList.toString());

        return boardCommentResponseDtoList;
    }


    // 게시글 별 댓글 수 가져오기 (추후엔 대댓글과도 합쳐서 count해야 함)
    public void updateCommentCount(Long boardSeq) {
        List<BoardCommentEntity> commentEntityList = boardCommentRepository.findByBoardSeq(boardSeq);
        int commentCount = commentEntityList.size();

//        int commentCount = boardCommentRepository.countByBoardSeq(boardSeq);
        BoardEntity boardEntity = boardRepository.findByBoardSeq(boardSeq);

        if (boardEntity != null) {
            boardEntity.setCommentCnt(commentCount);
            boardRepository.save(boardEntity);
        }
    }

    @Transactional
    public void updateComment(Long commentSeq, String commentContent){
        BoardCommentEntity boardCommentEntity = boardCommentRepository.findByCommentSeq(commentSeq);
        boardCommentEntity.setCommentContent(commentContent);
        boardCommentRepository.save(boardCommentEntity);
    }

    @Transactional
    public void deleteComment(Long commentSeq){

        boardCommentRepository.deleteCommentByCommentSeq(commentSeq);

    }


    // 대댓글 작성
    @Transactional
    public void registerBoardReply(BoardReplyRequestDto replyDto) {
        BoardReplyEntity replyEntity = replyDto.toEntity();
        boardReplyRepository.save(replyEntity);

    }











}
