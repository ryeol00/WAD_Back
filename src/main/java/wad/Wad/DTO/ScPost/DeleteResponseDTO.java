package wad.Wad.DTO.ScPost;

import lombok.Getter;

@Getter
public class DeleteResponseDTO {
    private boolean success;

    public DeleteResponseDTO(boolean success) {
        this.success = success;
        // 게시글 삭제 성공 여부 생성자
    }

}
