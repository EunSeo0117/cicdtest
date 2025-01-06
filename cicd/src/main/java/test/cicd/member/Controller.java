package test.cicd.member;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {
  @GetMapping("test/api")
    public String test() {
        return "this is test.";
    }

}
