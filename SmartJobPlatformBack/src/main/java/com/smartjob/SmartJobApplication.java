package com.smartjob;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.smartjob.mapper")
public class SmartJobApplication {
    public static void main(String[] args) {
        SpringApplication.run(SmartJobApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("  智慧招聘平台后端启动成功！");
        System.out.println("  访问地址: http://localhost:8080");
        System.out.println("========================================\n");
    }
}
