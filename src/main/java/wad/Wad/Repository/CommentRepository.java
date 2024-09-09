package wad.Wad.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wad.Wad.Entity.CommentEntity;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    // Method to find comments by board post ID and sort them by creation date
    List<CommentEntity> findByBoard_BoardPostIdOrderByCreatedAtAsc(Long boardPostId);
}
