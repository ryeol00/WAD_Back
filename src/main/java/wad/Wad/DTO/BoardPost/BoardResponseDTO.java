package wad.Wad.DTO.BoardPost;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import wad.Wad.Entity.BoardEntity;

import java.time.LocalDateTime;


@Setter
@Getter
@NoArgsConstructor
public class BoardResponseDTO {
    private Long boardpostid;
    private String title;
    private BoardEntity.Type type;
    private String content;
    private String username;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;



    public BoardResponseDTO(BoardEntity entity) {
        this.boardpostid = entity.getBoardPostId();
        this.title = entity.getTitle();
        this.type = entity.getType();
        this.content= entity.getContent();
        this.username = entity.getMember().getUsername();
        this.createdAt = entity.getCreatedAt();
        this.modifiedAt = entity.getModifiedAt();


    }

}
