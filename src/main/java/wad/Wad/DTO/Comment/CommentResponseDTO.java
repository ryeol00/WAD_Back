package wad.Wad.DTO.Comment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import wad.Wad.Entity.CommentEntity;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CommentResponseDTO {
    private Long commentId;
    private String content;
    private String username;
    private LocalDateTime createdAt;

    public CommentResponseDTO(CommentEntity entity) {
        this.commentId = entity.getCommentId();
        this.content = entity.getContent();
        this.username = entity.getMember().getUsername();
        this.createdAt = entity.getCreatedAt();
    }
}
