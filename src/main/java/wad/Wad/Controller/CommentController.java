package wad.Wad.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wad.Wad.DTO.Comment.CommentRequestDTO;
import wad.Wad.DTO.Comment.CommentResponseDTO;
import wad.Wad.Service.CommentService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/post/{boardPostId}")
    public ResponseEntity<List<CommentResponseDTO>> getCommentsByPost(@PathVariable("boardPostId") Long boardPostId) {
        try {
            List<CommentResponseDTO> responseDTOs = commentService.getComments(boardPostId);
            return ResponseEntity.ok(responseDTOs);
        } catch (Exception e) {
            log.error("Error retrieving comments for post ID: {}", boardPostId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/save")
    public ResponseEntity<?> createComment(
            @RequestBody CommentRequestDTO requestDTO,
            @CookieValue(value = "refresh", required = false) String refreshToken) {

        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No refresh token provided");
        }

        try {
            CommentResponseDTO responseDTO = commentService.addComment(requestDTO.getBoardPostId(), requestDTO, refreshToken);
            return ResponseEntity.ok(responseDTO);
        } catch (IllegalArgumentException e) {
            log.warn("Unauthorized access attempt", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token or board post");
        } catch (Exception e) {
            log.error("Error creating comment", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating comment");
        }
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<?> getComment(@PathVariable("commentId") Long commentId) {
        try {
            CommentResponseDTO responseDTO = commentService.getComment(commentId);
            if (responseDTO == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Comment not found");
            }
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            log.error("Error retrieving comment with ID: {}", commentId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving comment");
        }
    }
}
