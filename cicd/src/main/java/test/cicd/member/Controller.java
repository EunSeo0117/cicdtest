package test.cicd.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    @Autowired
    private MemberRepository memberRepository;

    @GetMapping("test/api")
    public String test() {
        return "this is test.";
    }

    @PostMapping("test/rds")
    public String testRds() {
        Member member = new Member(1L, "test", "1234");
        memberRepository.save(member);
        return "ok";
    }

}
