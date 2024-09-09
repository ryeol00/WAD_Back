package wad.Wad.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wad.Wad.DTO.Comment.CommentRequestDTO;
import wad.Wad.DTO.Comment.CommentResponseDTO;
import wad.Wad.Entity.BoardEntity;
import wad.Wad.Entity.CommentEntity;
import wad.Wad.Entity.MemberEntity;
import wad.Wad.Entity.RefreshEntity;
import wad.Wad.Repository.BoardRepository;
import wad.Wad.Repository.CommentRepository;
import wad.Wad.Repository.MemberRepository;
import wad.Wad.Repository.RefreshRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final RefreshRepository refreshRepository;
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;

    @Transactional
    public CommentResponseDTO addComment(Long boardPostId, CommentRequestDTO requestDTO, String refreshToken) {
        RefreshEntity refreshEntity = refreshRepository.findByRefresh(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token."));

        String username = refreshEntity.getUsername();

        MemberEntity memberEntity = memberRepository.findById(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        BoardEntity boardEntity = boardRepository.findById(boardPostId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found."));

        CommentEntity commentEntity = new CommentEntity();
        commentEntity.setContent(requestDTO.getContent());
        commentEntity.setCreatedAt(LocalDateTime.now());
        commentEntity.setBoard(boardEntity);
        commentEntity.setMember(memberEntity);

        commentRepository.save(commentEntity);

        return new CommentResponseDTO(commentEntity);
    }

    @Transactional(readOnly = true)
    public List<CommentResponseDTO> getComments(Long boardPostId) {
        List<CommentEntity> comments = commentRepository.findByBoard_BoardPostIdOrderByCreatedAtAsc(boardPostId);
        return comments.stream()
                .map(CommentResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CommentResponseDTO getComment(Long commentId) {
        CommentEntity commentEntity = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found."));
        return new CommentResponseDTO(commentEntity);
    }
}
