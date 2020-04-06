package org.csu.ipetstore;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

@SpringBootTest
class IPetStoreApplicationTests {

    @Test
    void contextLoads() {
        System.out.println(String.join("|", Arrays.asList(
                "history-cart-true-current-empty", "222")));
    }

}
