package wad.Wad.Repository;

import wad.Wad.Entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, String> {
    Boolean existsByUserId(String useId);
    Boolean existsByusername(String username);
    Boolean existsByEmail(String email);

    Optional<MemberEntity> findByUsername(String username);
}
