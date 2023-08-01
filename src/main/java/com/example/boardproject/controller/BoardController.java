package com.example.boardproject.controller;

import com.example.boardproject.dto.BoardRequestDto;
import com.example.boardproject.dto.BoardResponseDto;
import com.example.boardproject.dto.UserDto;
import com.example.boardproject.entity.BoardType;
import com.example.boardproject.service.BoardService;
import com.example.boardproject.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.*;

@Controller
@RequiredArgsConstructor
@Slf4j
public class BoardController {
    private final BoardService boardService;
    private final UserService userService;

    //게시판 페이지
    @GetMapping("/board")
    public ModelAndView board(@RequestParam(name = "page", defaultValue = "0") int page,
                              HttpSession httpSession) {

        // 로그인한 사용자의 아이디 가져오기
        String userId = (String) httpSession.getAttribute("userId");
        if (userId == null) {
            return new ModelAndView("redirect:/login");
        }

//        boolean isAdmin = userService.isAdminUser(userId);
        boolean isAdmin = (boolean) httpSession.getAttribute("admin");
        Pageable pageable = PageRequest.of(page, 10);
        Page<BoardResponseDto> normalBoards = boardService.getAllNormalBoardsWithUsers(pageable);

        List<BoardResponseDto> allBoards = new ArrayList<>();
        if (page == 0) {
            List<BoardResponseDto> noticeBoards = boardService.getNoticeBoardsWithUsers();
            allBoards.addAll(noticeBoards);
        }
        allBoards.addAll(normalBoards.getContent());

        ModelAndView modelAndView = new ModelAndView("board_list");
        modelAndView.addObject("Boards", allBoards);
        modelAndView.addObject("currentPage", page);
        modelAndView.addObject("totalPages", normalBoards.getTotalPages());
        modelAndView.addObject("isSearch", false);
        modelAndView.addObject("isAdmin", isAdmin);

        return modelAndView;
    }


    //검색 기능
    @GetMapping("/search")
    public ModelAndView search(@RequestParam(name = "searchType", defaultValue = "boardTitle") String searchType,
                         @RequestParam(name = "keyword") String keyword,
                         @RequestParam(name = "page", defaultValue = "0") int page) {
        int pageSize = 10;
        Page<BoardResponseDto> searchResult = boardService.searchBoards(searchType, keyword, page, pageSize);

        ModelAndView modelAndView = new ModelAndView("board_list");
        modelAndView.addObject("Boards", searchResult.getContent());
        modelAndView.addObject("currentPage", page);
        modelAndView.addObject("searchType", searchType);
        modelAndView.addObject("keyword", keyword);
        modelAndView.addObject("totalPages", searchResult.getTotalPages());
        modelAndView.addObject("isSearch", true);

        return modelAndView;
    }


    //게시물 작성 페이지
    @GetMapping("board/register")
    public ModelAndView registerBoardView(HttpSession httpSession) {

        // 로그인한 사용자의 아이디 가져오기
        String userId = (String) httpSession.getAttribute("userId");
        if (userId == null) {
            return new ModelAndView("redirect:/login");
        }

        // 세션에서 관리자 여부 가져오기
        boolean isAdmin = (boolean) httpSession.getAttribute("admin");

        BoardRequestDto boardRequestDto = new BoardRequestDto();
        boardRequestDto.setUserId(userId);

        ModelAndView modelAndView = new ModelAndView("board_register");
        modelAndView.addObject("boardRequestDto", boardRequestDto);
        modelAndView.addObject("isAdmin", isAdmin);
        modelAndView.addObject("userId", userId);

        return modelAndView;
    }

    //게시물 작성 기능
    @PostMapping("/board/register")
    public String registerBoard(@Valid @ModelAttribute("boardRequestDto") BoardRequestDto boardRequestDto,
                                @RequestParam("boardTypeInput") String boardType,
                                @RequestParam("image") MultipartFile file,
                                BindingResult bindingResult,
                                HttpServletRequest httpServletRequest,
                                RedirectAttributes redirectAttributes) {
        try {
            boardService.validateBoardRequest(boardRequestDto, bindingResult);
            if (bindingResult.hasErrors()) {
                for (FieldError error : bindingResult.getFieldErrors()) {
                    log.error("Validation error in field '{}' with value '{}'. Error message: {}",
                            error.getField(), error.getRejectedValue(), error.getDefaultMessage());
                }
                return "redirect:/board/register";
            }

            // 작성 페이지에서 세션에 저장된 로그인한 사용자의 아이디를 가져와서 boardRequestDto에 설정
            HttpSession session = httpServletRequest.getSession(false);
            if (session != null) {
                String userId = (String) session.getAttribute("userId");
                Long userSeq = userService.findUserSeqByUserId(userId); // userId로 userSeq를 찾아옴
                boardRequestDto.setUserId(userId);
                boardRequestDto.setUserSeq(userSeq);
            }

            log.info("게시글 작성 요청: {}", boardRequestDto);
            boardRequestDto.setBoardType(boardType);

            // 요청받은 boardType이 "N"인 경우 관리자 여부를 확인
            if (BoardType.N.name().equals(boardType) && session != null) {
                boolean isAdmin = (boolean) session.getAttribute("admin");
                if (!isAdmin) {
                    String userId = (String) session.getAttribute("userId");
                    log.error("관리자가 아닌 사용자 (" + userId + ")가 공지글을 작성하려고 시도했습니다.");
                    redirectAttributes.addFlashAttribute("errorMessage", "관리자만 공지글을 작성할 수 있습니다.");
                    return "redirect:/board/register";
                }
            }
            boardService.registerBoard(boardRequestDto, file);

        } catch (IOException e) {
            log.error("Error occurred during board registration: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/board/register";
        }
        return "redirect:/board";
    }




    @GetMapping("board/{boardSeq}")
    public String viewBoard(@PathVariable("boardSeq") Long boardSeq, Model model, HttpServletRequest httpServletRequest) {
        BoardResponseDto boardResponseDto = boardService.findByBoardSeq(boardSeq);

        // 게시물 등록 유저 정보
        UserDto boardUserDto = userService.findByUserSeq(boardResponseDto.getUserSeq());
        String boardUserId = boardUserDto.getUserId();

        // 현재 로그인 중인 유저 정보
        HttpSession session = httpServletRequest.getSession();
        String loginId = (String) session.getAttribute("userId");

        // 현재 로그인 중인 유저 권한
        boolean isAdmin = (boolean) session.getAttribute("admin");

        // 게시물 작성자와 로그인 유저가 같거나 관리자 유저이면 true 다르면 false
        boolean boardAuth = loginId.equals(boardUserId) || isAdmin;

        if (boardResponseDto.getBoardTypeEnum().equals(BoardType.S) && !boardAuth) {
            model.addAttribute("message", "접근 권한이 없습니다.");
            model.addAttribute("url", "/board");
            return "/alert";
        }

        if (boardResponseDto.isImageYn()) {
            String imagePath = boardService.getImagePathByBoardSeq(boardResponseDto.getBoardSeq());
            log.info("imagePath = {}", imagePath.substring(imagePath.lastIndexOf("/")));
            model.addAttribute("imagePath", imagePath.substring(imagePath.lastIndexOf("/")));
        }

        boardService.updateViewCnt(boardResponseDto.getBoardSeq());
        model.addAttribute("boardResponseDto", boardResponseDto);
        model.addAttribute("boardUserId", boardUserId);
        model.addAttribute("boardAuth", boardAuth);

        // 게시글 별 좋아요 갯수 가져오기
        int likeCount = boardService.getLikeCount(boardSeq);
        model.addAttribute("likeCount", likeCount);

        // userSeq로 좋아요 여부 확인
        boolean liked = false;
        Long userSeq = (Long) session.getAttribute("userSeq");
        if (userSeq != null) {
            liked = boardService.isLiked(boardSeq, userSeq);
        }
        model.addAttribute("liked", liked);

        return "board_view";
    }

    // 게시물 삭제
    @DeleteMapping("board/{boardSeq}")
    public String deleteBoard(Model model, @PathVariable("boardSeq") Long boardSeq) {
        log.info("delete board   boardSeq: {}", boardSeq);
        boardService.deleteBoard(boardSeq);

        model.addAttribute("message", "게시글이 삭제 되었습니다.");
        model.addAttribute("url", "/board");
        return "alert";
    }

    // 게시물 업데이트 페이지 이동
    @GetMapping("board/edit/{boardSeq}")
    public String updateBoardPage(Model model, @PathVariable("boardSeq") Long boardSeq, HttpServletRequest httpServletRequest) {


        BoardResponseDto boardResponseDto = boardService.findByBoardSeq(boardSeq);
        UserDto userDto = userService.findByUserSeq(boardResponseDto.getUserSeq());
        String boardUserId = userDto.getUserId();

        HttpSession session = httpServletRequest.getSession();
        // 현재 로그인 중인 유저 권한
        boolean isAdmin = (boolean) session.getAttribute("admin");

        if (boardResponseDto.isImageYn()) {
            String imagePath = boardService.getImagePathByBoardSeq(boardResponseDto.getBoardSeq()).substring(25);
            log.info("imagePath = {}", imagePath);
            model.addAttribute("imagePath", imagePath);
        }

        model.addAttribute("boardResponseDto", boardResponseDto);
        model.addAttribute("boardUserId", boardUserId);
        model.addAttribute("isAdmin", isAdmin);
        return "board_update";
    }

    // 게시물 업데이트 적용
    @PutMapping("board/{boardSeq}")
    public String updateBoard(@PathVariable("boardSeq") Long boardSeq,
                              @ModelAttribute("boardResponseDto") BoardResponseDto boardResponseDto,
                              @RequestParam("image") MultipartFile file,
                              HttpServletRequest httpServletRequest,
                              RedirectAttributes redirectAttributes,
                              BindingResult bindingResult) {

        BoardRequestDto boardRequestDto = new BoardRequestDto(boardResponseDto);
        HttpSession session = httpServletRequest.getSession();
        boolean isAdmin = (boolean) session.getAttribute("admin");
        try {
            boardService.validateBoardRequest(boardRequestDto, bindingResult);
            if (bindingResult.hasErrors()) {
                for (FieldError error : bindingResult.getFieldErrors()) {
                    log.error("Validation error in field '{}' with value '{}'. Error message: {}",
                            error.getField(), error.getRejectedValue(), error.getDefaultMessage());
                }
                return "redirect:/board";
            }


            log.info("게시글 작성 요청: {}", boardRequestDto);


            // 요청받은 boardType이 "N"인 경우 관리자 여부를 확인
            if (BoardType.N.name().equals(boardRequestDto.getBoardType()) && isAdmin) {
                redirectAttributes.addFlashAttribute("errorMessage", "관리자만 공지글을 작성할 수 있습니다.");
                return "redirect:/board/" + boardSeq;
            }
            boardService.updateBoard(boardSeq, boardRequestDto, file);

        } catch (IOException e) {
            log.error("Error occurred during board registration: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/board/register";
        }





        return "redirect:/board/" + boardSeq;
    }




    // 게시물 좋아요
    @PostMapping("board/{boardSeq}")
    @ResponseBody
    public Map<String, Object> toggleLike(@PathVariable Long boardSeq, HttpSession httpSession) {
        Long userSeq = (Long) httpSession.getAttribute("userSeq");
        String result = boardService.toggleLike(boardSeq, userSeq);
        int likeCount = boardService.getLikeCount(boardSeq);

        Map<String, Object> response = new HashMap<>();
        response.put("result", result);
        response.put("likeCount", likeCount);
        return response;
    }




}
