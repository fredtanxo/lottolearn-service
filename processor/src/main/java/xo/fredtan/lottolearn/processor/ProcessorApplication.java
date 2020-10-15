package xo.fredtan.lottolearn.processor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import xo.fredtan.lottolearn.common.annotation.LotToLearnApplication;

@LotToLearnApplication
@SpringBootApplication
public class ProcessorApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProcessorApplication.class, args);
    }
}
