package xo.fredtan.lottolearn.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@EntityScan("xo.fredtan.lottolearn.domain")
@ComponentScan(basePackages = {"xo.fredtan.lottolearn.common"})
@ComponentScan(basePackages = {"xo.fredtan.lottolearn.api"})
@ComponentScan(basePackages = {"xo.fredtan.lottolearn.system"})
@SpringBootApplication
public class SystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(SystemApplication.class, args);
    }
}
