package wad.Wad.Entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import wad.Wad.DTO.ScPost.ScPostRequestDTO;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class ScPostEntity {

    // enum을 사용하여 컬럼을 특정 값 으로 정의 가능
    public enum Type {
        기획,
        디자인,
        프론트엔드,
        백엔드
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ScPostId", nullable = false, length = 20)
    private long scpostId;

    @Column(name = "likes", nullable = false, length = 20)
    private int likes = 0;

    @Column(name = "viewCount", nullable = false, length = 20)
    private int viewCount = 0;


    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private Type type;

    @Column(name = "title", nullable = false, length = 20)
    private String title;

    // JSON 문자열로 일자와 관련된 내용을 저장
    @Column(name = "dateRanges", columnDefinition = "TEXT")
    private String dateRanges;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime modifiedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", referencedColumnName = "username", nullable = false)
    private MemberEntity member;



    public ScPostEntity(ScPostRequestDTO requestDTO) {
        this.title = requestDTO.getTitle();
        this.type = requestDTO.getType();
        this.dateRanges = convertDateRangesToJson(requestDTO.getDateRanges());
    }

    private String convertDateRangesToJson(List<Map<String, Object>> dateRanges) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(dateRanges);
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert date ranges to JSON", e);
        }
    }



}
