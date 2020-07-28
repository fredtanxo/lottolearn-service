package xo.fredtan.lottolearn.user;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@EnableDubbo
@EntityScan("xo.fredtan.lottolearn.domain")
@ComponentScan(basePackages = {"xo.fredtan.lottolearn.common"})
@ComponentScan(basePackages = {"xo.fredtan.lottolearn.api"})
@ComponentScan(basePackages = {"xo.fredtan.lottolearn.user"})
@SpringBootApplication
public class UserApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}
