package xo.fredtan.lottolearn.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import xo.fredtan.lottolearn.common.annotation.LotToLearnApplication;

@LotToLearnApplication
@SpringBootApplication
public class SystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(SystemApplication.class, args);
    }
}
