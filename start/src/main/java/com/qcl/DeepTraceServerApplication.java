package com.qcl;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.retry.annotation.EnableRetry;

/**
 * 入口类
 */
@SpringBootApplication
@MapperScan({"com.qcl.dao"})
@EnableRetry
public class DeepTraceServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeepTraceServerApplication.class, args);
        System.out.println("deep trace server start success!");
    }

}
