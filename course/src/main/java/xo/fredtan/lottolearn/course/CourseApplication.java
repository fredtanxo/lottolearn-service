package xo.fredtan.lottolearn.course;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import xo.fredtan.lottolearn.common.annotation.LotToLearnApplication;

@LotToLearnApplication
@SpringBootApplication
public class CourseApplication {
    public static void main(String[] args) {
        SpringApplication.run(CourseApplication.class, args);
    }
}
