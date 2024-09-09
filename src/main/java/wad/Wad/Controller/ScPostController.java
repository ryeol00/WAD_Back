//package wad.Wad.Controller;
//
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//import wad.Wad.DTO.ScPost.ScPostRequestDTO;
//import wad.Wad.DTO.ScPost.ScPostResponseDTO;
//import wad.Wad.Service.ScPostService;
//
//import java.util.List;
//
//@RequestMapping(value = "/schedule")
//@RestController
//@RequiredArgsConstructor
//public class ScPostController {
//    private final ScPostService scPostService;
//
//    @GetMapping("/posts")
//    public List<ScPostResponseDTO> getPosts() {
//        return scPostService.getPosts();
//    // 오류 수정 : 객체인 ScPostService가 아닌 객체 인스턴스인 scPostService를 통해 getPosts()메서드를 사용해야함
//    }
//
//    @PostMapping("/save")
//    public ScPostResponseDTO createPost(@RequestBody ScPostRequestDTO requestDTO) {
//        return scPostService.createPost(requestDTO);
//    }
//    // POST 방식 “/save” → createPost (게시글을 작성한다.)
//    // 게시글 내용이 담긴 BoardRequestsDto 를 Client로부터 받는다.
//    // 작성된 게시글을 BoardResponseDto 에 담아 Client로 보낸다.
//
//
//
//}
