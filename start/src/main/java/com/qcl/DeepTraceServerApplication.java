package com.qcl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;

/**
 * 入口类
 */
@SpringBootApplication
public class DeepTraceServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeepTraceServerApplication.class, args);
        System.out.println("deep trace server start success!");
    }

}
