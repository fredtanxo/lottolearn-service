package xo.fredtan.lottolearn.common.annotation;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 标识目标类为LotToLearn服务的启动类，便于添加自动扫描
 * {@link xo.fredtan.lottolearn.common}和{@link xo.fredtan.lottolearn.api}两个包，
 * 同时添加自动扫描{@link xo.fredtan.lottolearn.domain}下的Spring Data实体类，
 * 并启动Dubbo自动扫描和自动配置
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@EnableDubbo
@EntityScan // 实体类
@ComponentScan(basePackages = {"xo.fredtan.lottolearn.common"}) // common包
@ComponentScan(basePackages = {"xo.fredtan.lottolearn.api"}) // api包
public @interface LotToLearnApplication {
    @AliasFor(annotation = EntityScan.class, attribute = "basePackages")
    String[] value() default "xo.fredtan.lottolearn.domain";
}
