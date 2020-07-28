package xo.fredtan.lottolearn.course;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@EnableDubbo
@EntityScan("xo.fredtan.lottolearn.domain")
@ComponentScan(basePackages = {"xo.fredtan.lottolearn.common"})
@ComponentScan(basePackages = {"xo.fredtan.lottolearn.api"})
@ComponentScan(basePackages = {"xo.fredtan.lottolearn.course"})
@SpringBootApplication
public class CourseApplication {
    public static void main(String[] args) {
        SpringApplication.run(CourseApplication.class, args);
    }
}
