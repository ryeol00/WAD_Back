package wad.Wad.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wad.Wad.DTO.ScPost.ScPostResponseDTO;
import wad.Wad.Service.ScPostService;

@RequestMapping(value = "/schedule")
@RequiredArgsConstructor
@RestController
public class GetController {

    private final ScPostService scPostService;
    // 전체 게시물 조회
    @GetMapping("/posts")
    public Object getPosts() {
        try {
            // 모든 사용자가 게시글 목록을 조회할 수 있음
            return scPostService.getPosts();
        } catch (Exception e) {
            // 예외 발생 시 에러 메시지를 콘솔에 출력
            e.printStackTrace();
            return "에러 발생";
        }
    }

    @GetMapping("/post/{scpostId}")
    public ResponseEntity<?> getPost(@PathVariable("scpostId") Long scpostId) {
        try {
            ScPostResponseDTO responseDTO = scPostService.getPost(scpostId);
            if (responseDTO == null) {
                // 게시물이 존재하지 않는 경우 404 반환
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시물이 존재하지 않습니다.");
            }
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            // 서버 오류 발생 시 500 반환
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("에러 발생");
        }
    }
}
