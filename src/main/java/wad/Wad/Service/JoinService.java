package wad.Wad.Service;

import wad.Wad.DTO.JoinDTO;
import wad.Wad.Entity.MemberEntity;
import wad.Wad.Repository.MemberRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;



@Service
public class JoinService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public JoinService(MemberRepository memberRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.memberRepository = memberRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public boolean existsUser(String userId, String username, String email) {
        return memberRepository.existsByUserId(userId)
                || memberRepository.existsByUsername(username)
                || memberRepository.existsByEmail(email);
    }

    public void joinProcess(JoinDTO joinDTO) {
        String userId = joinDTO.getUserId();
        String password = joinDTO.getPassword();
        String username = joinDTO.getUsername();
        String email = joinDTO.getEmail();



        if (existsUser(userId, username, email)) {
            throw new IllegalStateException("이미 값이 존재합니다.");
        }


        MemberEntity data = new MemberEntity();
        data.setUserId(userId);
        data.setPassword(bCryptPasswordEncoder.encode(password));
        data.setUsername(username);
        data.setEmail(email);
        data.setRole("ROLE_ADMIN");

        memberRepository.save(data);
    }


}