package wad.Wad.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wad.Wad.Entity.ScPostEntity;

import java.util.List;

@Repository
public interface ScPostRepository extends JpaRepository<ScPostEntity, Long> {
    List<ScPostEntity> findAllByOrderByModifiedAtDesc();


}
