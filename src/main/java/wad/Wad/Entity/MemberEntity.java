package wad.Wad.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity


@Table(name="Member")
@Getter
@Setter
public class MemberEntity {

    @Id
    @Column(name = "username", nullable = false, length = 20)
    private String username;

    @Column(name = "UserId", nullable = false, length = 20)
    private String userId;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", nullable = false, length = 20)
    private String email;

    @Column(name = "role", nullable = false, length = 20)
    private String role;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScPostEntity> posts = new ArrayList<>();




}


