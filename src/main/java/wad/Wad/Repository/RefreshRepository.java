package wad.Wad.Repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import wad.Wad.Entity.RefreshEntity;

import java.util.Optional;

public interface RefreshRepository extends JpaRepository<RefreshEntity, Long> {

    Boolean existsByRefresh(String refresh);

    @Transactional
    void deleteByRefresh(String refresh);

    Optional<RefreshEntity> findByRefresh(String refresh);
}
