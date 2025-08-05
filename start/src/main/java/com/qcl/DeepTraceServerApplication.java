package com.qcl;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 入口类
 */
@SpringBootApplication
@MapperScan({"com.qcl.dao"})
public class DeepTraceServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeepTraceServerApplication.class, args);
        System.out.println("deep trace server start success!");
    }

}
