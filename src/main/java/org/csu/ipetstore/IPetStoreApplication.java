package org.csu.ipetstore;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableAsync
@EnableTransactionManagement
@EnableSwagger2
@MapperScan("org.csu.ipetstore.mapper")
@SpringBootApplication
public class IPetStoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(IPetStoreApplication.class, args);
    }

}
