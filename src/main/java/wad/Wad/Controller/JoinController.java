package wad.Wad.Controller;

import org.springframework.http.ResponseEntity;
import wad.Wad.DTO.JoinDTO;
import wad.Wad.Service.JoinService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
public class JoinController {

    private final JoinService joinService;

    public JoinController(JoinService joinService) {

        this.joinService = joinService;
    }

    @PostMapping("/join")
    public ResponseEntity<String> joinProcess(JoinDTO joinDTO) {
        try {
            joinService.joinProcess(joinDTO);
            return ResponseEntity.ok("회원가입이 완료되었습니다.");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body("이미 존재하는 아이디입니다.");
        }
    }
}