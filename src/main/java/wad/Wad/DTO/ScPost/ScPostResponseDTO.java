package wad.Wad.DTO.ScPost;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import wad.Wad.Entity.MemberEntity;
import wad.Wad.Entity.ScPostEntity;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Setter
@Getter
@NoArgsConstructor
public class ScPostResponseDTO {

    private Long scpostid;
    private String title;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private ScPostEntity.Type type;
    private List<Map<String, Object>> dateRanges; // JSON 문자열을 파싱한 결과를 담는 필드
    private String username;

    @JsonIgnore
    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 파싱을 위한 ObjectMapper


    public ScPostResponseDTO(ScPostEntity entity) {
        this.scpostid = entity.getScpostId();
        this.title = entity.getTitle();
        this.createdAt = entity.getCreatedAt();
        this.modifiedAt = entity.getModifiedAt();
        this.type = entity.getType();
        this.username = entity.getMember().getUsername();
        this.dateRanges = parseDateRanges(entity.getDateRanges());


    }
    // JSON 문자열을 파싱하여 List<Map<String, Object>>로 변환하는 메서드
    private List<Map<String, Object>> parseDateRanges(String dateRangesJson) {
        try {
            return objectMapper.readValue(dateRangesJson, new TypeReference<List<Map<String, Object>>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse dateRanges JSON", e);
        }
    }

}
