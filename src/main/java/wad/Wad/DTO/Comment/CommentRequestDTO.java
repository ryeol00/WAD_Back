package wad.Wad.DTO.Comment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequestDTO {
    private Long boardPostId; // Board post ID to which the comment belongs
    private String content;   // Content of the comment
}
