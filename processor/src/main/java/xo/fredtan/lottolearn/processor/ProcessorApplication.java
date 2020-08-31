package xo.fredtan.lottolearn.processor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@EntityScan("xo.fredtan.lottolearn.domain")
@ComponentScan(basePackages = {"xo.fredtan.lottolearn.common"})
@ComponentScan(basePackages = {"xo.fredtan.lottolearn.api"})
@ComponentScan(basePackages = {"xo.fredtan.lottolearn.processor"})
@SpringBootApplication
public class ProcessorApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProcessorApplication.class, args);
    }
}
