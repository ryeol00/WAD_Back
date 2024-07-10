package wad.Wad.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity

//@Table(name = "member", uniqueConstraints = {
//        @UniqueConstraint(name = "UK_username", columnNames = "username"),
//        @UniqueConstraint(name = "UK_email", columnNames = "email")
//})
@Table(name="member")
@Getter
@Setter
public class MemberEntity {

    @Id
    @Column(name = "UserId", nullable = false, length = 20)
    private String userId;

    @Column(name = "username", nullable = false, length = 20)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", nullable = false, length = 20)
    private String email;

    @Column(name = "role", nullable = false, length = 20)
    private String role;


}


