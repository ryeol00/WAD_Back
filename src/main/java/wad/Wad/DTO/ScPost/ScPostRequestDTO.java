package wad.Wad.DTO.ScPost;

import lombok.Getter;
import lombok.Setter;
import wad.Wad.Entity.ScPostEntity;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Setter
@Getter
public class ScPostRequestDTO {
    private String title;
    //private String content;
    private ScPostEntity.Type type;
    private List<Map<String, Object>> dateRanges;  // JSON 배열을 받아올 필드로 수정

    //private String startDate;
    //private String endDate;



}
