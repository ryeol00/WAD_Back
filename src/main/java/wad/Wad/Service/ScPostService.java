package wad.Wad.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wad.Wad.DTO.ScPost.DeleteResponseDTO;
import wad.Wad.DTO.ScPost.ScPostRequestDTO;
import wad.Wad.DTO.ScPost.ScPostResponseDTO;
import wad.Wad.Entity.MemberEntity;
import wad.Wad.Entity.RefreshEntity;
import wad.Wad.Entity.ScPostEntity;
import wad.Wad.Repository.MemberRepository;
import wad.Wad.Repository.RefreshRepository;
import wad.Wad.Repository.ScPostRepository;
import wad.Wad.jwt.JWTUtil;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ScPostService {
    private final ScPostRepository scPostRepository;
    private final RefreshRepository refreshRepository;
    private final MemberRepository memberRepository;
    private final JWTUtil jwtUtil;
    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 파싱을 위한 ObjectMapper

    @Transactional(readOnly = true)
    public List<ScPostResponseDTO> getPosts() {
        return scPostRepository.findAllByOrderByModifiedAtDesc().stream().map(ScPostResponseDTO::new).toList();
    // 오류 수정 : 객체인 ScPostRepository가 아닌 객체의 인스턴스 scPostRepository를 통해 메서드를 사용해야함
    }

    @Transactional(readOnly = true)
    public ScPostResponseDTO getPost(Long scpostId) {
        return scPostRepository.findById(scpostId)
                .map(ScPostResponseDTO::new)
                .orElse(null); // 게시물이 없을 경우 null 반환
    }

    @Transactional
    public ScPostResponseDTO createPost(ScPostRequestDTO requestDTO, String accessToken) {
        // Access Token에서 username 추출 및 검증
        if (jwtUtil.isExpired(accessToken)) {
            throw new IllegalArgumentException("Access token is expired.");
        }

        String username = jwtUtil.getUsername(accessToken); // JWT에서 username 추출

        // MemberEntity 조회
        MemberEntity memberEntity = memberRepository.findById(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        // ScPostEntity 생성 및 설정
        ScPostEntity scPostEntity = new ScPostEntity();
        scPostEntity.setTitle(requestDTO.getTitle());
        scPostEntity.setCreatedAt(LocalDateTime.now());
        scPostEntity.setModifiedAt(LocalDateTime.now());
        scPostEntity.setType(requestDTO.getType());
        scPostEntity.setMember(memberEntity); // MemberEntity 설정

        // JSON 문자열로 날짜 범위 설정
        scPostEntity.setDateRanges(convertDateRangesToJson(requestDTO.getDateRanges()));

        // 엔티티 저장
        scPostRepository.save(scPostEntity);

        // 응답 DTO 생성 및 반환
        return new ScPostResponseDTO(scPostEntity);
    }


    private String convertDateRangesToJson(List<Map<String, Object>> dateRanges) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(dateRanges);
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert date ranges to JSON", e);
        }
    }







    @Transactional
    public ScPostResponseDTO updatePost(Long scpostId, ScPostRequestDTO requestDTO, String refreshToken) {
        // RefreshEntity를 refreshToken으로 조회
        RefreshEntity refreshEntity = refreshRepository.findByRefresh(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("유효한 토큰이 없습니다."));

        // RefreshEntity에서 username 가져오기
        String currentUsername = refreshEntity.getUsername();

        // ScPostEntity 조회
        ScPostEntity scPostEntity = scPostRepository.findById(scpostId)
                .orElseThrow(() -> new IllegalArgumentException("게시물이 존재하지 않습니다."));

        // 게시물 작성자와 현재 사용자의 username이 일치하는지 확인
        if (!scPostEntity.getMember().getUsername().equals(currentUsername)){
            throw new IllegalArgumentException("게시물을 수정할 권한이 없습니다.");
        }

        // 게시물 정보 수정
        scPostEntity.setTitle(requestDTO.getTitle());
        //scPostEntity.setContent(requestDTO.getContent());
        scPostEntity.setModifiedAt(LocalDateTime.now());
        //scPostEntity.setStartDate(requestDTO.getStartDate());
        //scPostEntity.setEndDate(requestDTO.getEndDate());
        scPostEntity.setType(requestDTO.getType());

        // JSON 문자열로 날짜 범위 설정
        scPostEntity.setDateRanges(convertDateRangesToJson(requestDTO.getDateRanges()));

        // 변경된 게시물 저장
        scPostRepository.save(scPostEntity);

        // 수정된 엔티티를 기반으로 Response DTO 생성
        return new ScPostResponseDTO(scPostEntity);

    }

    private String generateDateRangesJson(String dateRanges) {
        // dateRanges를 JSON 형식으로 변환하는 로직 (요청 DTO의 dateRanges를 JSON으로 변환)
        try {
            return objectMapper.writeValueAsString(dateRanges);
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert date ranges to JSON", e);
        }
    }

    @Transactional
    // requestDto의 정보는 삭제과정에 불필요하므로 제외(사용자의 삭제 권한 유무만 확인하면 됌)
    public DeleteResponseDTO deletePost(Long scpostId, String refreshToken) throws Exception {
        // RefreshEntity를 refreshToken으로 조회
       RefreshEntity refreshEntity = refreshRepository.findByRefresh(refreshToken)
                       .orElseThrow(() -> new IllegalArgumentException("유효한 토큰이 없습니다."));

       // RefreshEntity에서 username 가져오기
       String currentUsername = refreshEntity.getUsername();

       // ScPostEntity에서 게시물 존재 여부 조회
       ScPostEntity scPostEntity = scPostRepository.findById(scpostId)
                       .orElseThrow(() -> new IllegalArgumentException("게시물이 존재하지 않습니다."));

       // 게시물 작성자와 현재 사용자의 username이 일치하는지 확인
        if (!scPostEntity.getMember().getUsername().equals(currentUsername)){
            throw new IllegalArgumentException("게시물을 삭제할 권한이 없습니다.");
        }

        // 게시물 삭제
        scPostRepository.delete(scPostEntity);
        return new DeleteResponseDTO(true); // 삭제 성공
    }




}
