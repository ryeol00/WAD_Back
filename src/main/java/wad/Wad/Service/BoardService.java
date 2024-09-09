package wad.Wad.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wad.Wad.DTO.BoardPost.BoardRequestDTO;
import wad.Wad.DTO.BoardPost.BoardResponseDTO;
import wad.Wad.DTO.BoardPost.DeleteResponseDTO;
import wad.Wad.Entity.BoardEntity;
import wad.Wad.Entity.MemberEntity;
import wad.Wad.Entity.RefreshEntity;
import wad.Wad.Repository.BoardRepository;
import wad.Wad.Repository.MemberRepository;
import wad.Wad.Repository.RefreshRepository;
import wad.Wad.jwt.JWTUtil;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final RefreshRepository refreshRepository;
    private final MemberRepository memberRepository;
    private final JWTUtil jwtUtil;


    @Transactional(readOnly = true)
    public List<BoardResponseDTO> getPosts() {
        return boardRepository.findAllByOrderByModifiedAtDesc().stream()
                .map(BoardResponseDTO::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public BoardResponseDTO getPost(Long boardPostId) {
        return boardRepository.findById(boardPostId)
                .map(BoardResponseDTO::new)
                .orElse(null);
    }

    @Transactional
    public BoardResponseDTO createPost(BoardRequestDTO requestDTO, String accessToken) {
        // Access Token에서 username 추출 및 검증
        if (jwtUtil.isExpired(accessToken)) {
            throw new IllegalArgumentException("Access token is expired.");
        }

        String username = jwtUtil.getUsername(accessToken); // JWT에서 username 추출

        // MemberEntity 조회
        MemberEntity memberEntity = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        // BoardEntity 생성 및 설정
        BoardEntity boardEntity = new BoardEntity();
        boardEntity.setTitle(requestDTO.getTitle());
        boardEntity.setContent(requestDTO.getContent());
        boardEntity.setCreatedAt(LocalDateTime.now());
        boardEntity.setModifiedAt(LocalDateTime.now());
        boardEntity.setType(requestDTO.getType());
        boardEntity.setMember(memberEntity); // MemberEntity 설정

        // 엔티티 저장
        boardRepository.save(boardEntity);

        // 응답 DTO 생성 및 반환
        return new BoardResponseDTO(boardEntity);
    }

    @Transactional
    public BoardResponseDTO updatePost(Long boardPostId, BoardRequestDTO requestDTO, String refreshToken) {
        // RefreshEntity lookup
        RefreshEntity refreshEntity = refreshRepository.findByRefresh(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token."));

        // Get username from RefreshEntity
        String currentUsername = refreshEntity.getUsername();

        // BoardEntity lookup
        BoardEntity boardEntity = boardRepository.findById(boardPostId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found."));

        // Check if current user is the author
        if (!boardEntity.getMember().getUsername().equals(currentUsername)){
            throw new IllegalArgumentException("No permission to update this post.");
        }

        // Update entity
        boardEntity.setTitle(requestDTO.getTitle());
        boardEntity.setContent(requestDTO.getContent());
        boardEntity.setModifiedAt(LocalDateTime.now());
        boardEntity.setType(requestDTO.getType());

        // Save updated entity
        boardRepository.save(boardEntity);

        return new BoardResponseDTO(boardEntity);
    }

    @Transactional
    public DeleteResponseDTO deletePost(Long boardPostId, String refreshToken) {
        // RefreshEntity lookup
        RefreshEntity refreshEntity = refreshRepository.findByRefresh(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token."));

        // Get username from RefreshEntity
        String currentUsername = refreshEntity.getUsername();

        // BoardEntity lookup
        BoardEntity boardEntity = boardRepository.findById(boardPostId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found."));

        // Check if current user is the author
        if (!boardEntity.getMember().getUsername().equals(currentUsername)){
            throw new IllegalArgumentException("No permission to delete this post.");
        }

        // Delete entity
        boardRepository.delete(boardEntity);
        return new DeleteResponseDTO(true);
    }
}
