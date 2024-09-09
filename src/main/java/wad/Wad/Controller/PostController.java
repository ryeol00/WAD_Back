package wad.Wad.Controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import wad.Wad.DTO.CustomUserDetails;
import wad.Wad.DTO.ScPost.DeleteResponseDTO;
import wad.Wad.DTO.ScPost.ScPostRequestDTO;
import wad.Wad.DTO.ScPost.ScPostResponseDTO;
import wad.Wad.Entity.MemberEntity;
import wad.Wad.Service.ScPostService;
import wad.Wad.jwt.JWTUtil;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/schedule")
public class PostController {
    private final ScPostService scPostService;
    private final JWTUtil jwtUtil;

    @PostMapping("/save")
    public ResponseEntity<?> createPost(
            @RequestBody ScPostRequestDTO requestDTO,
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

            // 일정 게시글 생성 서비스 호출
            ScPostResponseDTO responseDTO = scPostService.createPost(requestDTO, token);
            return ResponseEntity.ok(responseDTO);

        } catch (IllegalArgumentException e) {
            log.warn("Unauthorized access attempt: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error creating schedule post", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating schedule post");
        }
    }


    @PutMapping("/post/{scpostId}")
    public ResponseEntity<?> updatePost(
            @PathVariable Long scpostId, // 실제 게시물 아이디를 삭제 및 수정의 경로로 사용
            @RequestBody ScPostRequestDTO requestDTO,
            @CookieValue(value = "refresh", required = false) String refreshToken) {

        // 리프레시 토큰이 없으면 401 Unauthorized 반환
        if (refreshToken == null) {
            return ResponseEntity.status(401).body("No refresh token");
        }

        try {
            // 게시물 수정 로직 - scpostId, requestDTO, refreshToken을 서비스로 전달
            ScPostResponseDTO responseDTO = scPostService.updatePost(scpostId, requestDTO, refreshToken);
            return ResponseEntity.ok(responseDTO);

        } catch (IllegalArgumentException e) {
            // 유저가 존재하지 않거나 리프레시 토큰이 유효하지 않으면 401 반환
            return ResponseEntity.status(401).body(e.getMessage());
        } catch (Exception e) {
            // 기타 예외 발생 시 500 서버 에러 반환
            return ResponseEntity.status(500).body("Server error: " + e.getMessage());
        }
    }




    @DeleteMapping("/post/{scpostId}")
    public ResponseEntity<DeleteResponseDTO> deletePost(
            @PathVariable Long scpostId,
            @CookieValue(value = "refresh", required = false) String refreshToken) {

        // 리프레시 토큰이 없으면 401 Unauthorized 반환
        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new DeleteResponseDTO(false));
        }

        try {
            // 게시물 삭제 로직 - scpostId와 refreshToken을 서비스로 전달
            DeleteResponseDTO responseDTO = scPostService.deletePost(scpostId, refreshToken);
            return ResponseEntity.ok(responseDTO);

        } catch (IllegalArgumentException e) {
            // 유저가 존재하지 않거나 리프레시 토큰이 유효하지 않으면 401 반환
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new DeleteResponseDTO(false));
        } catch (Exception e) {
            // 기타 예외 발생 시 500 서버 에러 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new DeleteResponseDTO(false));
        }
    }






}
