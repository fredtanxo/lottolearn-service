package xo.fredtan.lottolearn.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import xo.fredtan.lottolearn.common.annotation.LotToLearnApplication;

@LotToLearnApplication
@SpringBootApplication
public class UserApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}
