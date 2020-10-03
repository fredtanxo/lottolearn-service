package xo.fredtan.lottolearn.message;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {"xo.fredtan.lottolearn.common"})
@ComponentScan(basePackages = {"xo.fredtan.lottolearn.api"})
@ComponentScan(basePackages = {"xo.fredtan.lottolearn.message"})
@SpringBootApplication
public class MessageApplication {
    public static void main(String[] args) {
        SpringApplication.run(MessageApplication.class, args);
    }
}
