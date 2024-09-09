package wad.Wad.Controller;

import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wad.Wad.DTO.BoardPost.BoardRequestDTO;
import wad.Wad.DTO.BoardPost.BoardResponseDTO;
import wad.Wad.DTO.BoardPost.DeleteResponseDTO;
import wad.Wad.Service.BoardService;
import wad.Wad.jwt.JWTUtil;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/board")  // 경로를 명확히 구분
public class BoardController {

    private final BoardService boardService;
    private final JWTUtil jwtUtil;

    @GetMapping("/posts")
    public ResponseEntity<List<BoardResponseDTO>> getPosts() {
        try {
            List<BoardResponseDTO> responseDTOs = boardService.getPosts();
            return ResponseEntity.ok(responseDTOs);
        } catch (Exception e) {
            log.error("Error retrieving board posts", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @GetMapping("/post/{boardPostId}")
    public ResponseEntity<?> getPost(@PathVariable("boardPostId") Long boardPostId) {
        try {
            BoardResponseDTO responseDTO = boardService.getPost(boardPostId);
            if (responseDTO == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found");
            }
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            log.error("Error retrieving board post with ID: {}", boardPostId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving board post");
        }
    }

    @PostMapping("/save")
    public ResponseEntity<?> createPost(
            @RequestBody BoardRequestDTO requestDTO,
            @RequestHeader(value = "Authorization", required = false) String accessToken) {

        System.out.println("ACCESS-TOKEN  = " + accessToken);

        if (accessToken == null || !accessToken.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No access token");
        }

        String token = accessToken.substring(7);  // "Bearer " 이후의 토큰 부분만 추출

        try {
            // Access 토큰이 만료되었는지 확인
            if (jwtUtil.isExpired(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access token is expired.");
            }

            // 게시글 생성 서비스 호출
            BoardResponseDTO responseDTO = boardService.createPost(requestDTO, token);
            return ResponseEntity.ok(responseDTO);

        } catch (IllegalArgumentException e) {
            log.warn("Unauthorized access attempt: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error creating board post", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating board post");
        }
    }



    @PutMapping("/post/{boardPostId}")
    public ResponseEntity<?> updatePost(
            @PathVariable Long boardPostId,
            @RequestBody BoardRequestDTO requestDTO,
            @CookieValue(value = "refresh", required = false) String refreshToken) {

        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No refresh token");
        }

        try {
            BoardResponseDTO responseDTO = boardService.updatePost(boardPostId, requestDTO, refreshToken);
            return ResponseEntity.ok(responseDTO);
        } catch (IllegalArgumentException e) {
            log.warn("Unauthorized access attempt for board post ID: {}", boardPostId, e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token or user");
        } catch (Exception e) {
            log.error("Error updating board post with ID: {}", boardPostId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating board post");
        }
    }

    @DeleteMapping("/post/{boardPostId}")
    public ResponseEntity<DeleteResponseDTO> deletePost(
            @PathVariable Long boardPostId,
            @CookieValue(value = "refresh", required = false) String refreshToken) {

        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new DeleteResponseDTO(false));
        }

        try {
            DeleteResponseDTO responseDTO = boardService.deletePost(boardPostId, refreshToken);
            return ResponseEntity.ok(responseDTO);
        } catch (IllegalArgumentException e) {
            log.warn("Unauthorized deletion attempt for board post ID: {}", boardPostId, e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new DeleteResponseDTO(false));
        } catch (Exception e) {
            log.error("Error deleting board post with ID: {}", boardPostId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new DeleteResponseDTO(false));
        }
    }
}
