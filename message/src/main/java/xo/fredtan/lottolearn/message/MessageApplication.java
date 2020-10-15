package xo.fredtan.lottolearn.message;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import xo.fredtan.lottolearn.common.annotation.LotToLearnApplication;

@LotToLearnApplication
@SpringBootApplication
public class MessageApplication {
    public static void main(String[] args) {
        SpringApplication.run(MessageApplication.class, args);
    }
}
