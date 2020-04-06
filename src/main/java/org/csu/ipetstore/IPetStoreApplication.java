package org.csu.ipetstore;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("org.csu.ipetstore.mapper")
@SpringBootApplication
public class IPetStoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(IPetStoreApplication.class, args);
    }

}
