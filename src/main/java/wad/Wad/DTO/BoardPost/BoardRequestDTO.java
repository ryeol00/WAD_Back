package wad.Wad.DTO.BoardPost;

import lombok.Getter;
import lombok.Setter;
import wad.Wad.Entity.BoardEntity;

@Setter
@Getter
public class BoardRequestDTO {
    private String title;

    private BoardEntity.Type type;
    private String content;


}
