package org.csu.ipetstore;

import org.csu.ipetstore.domain.Account;
import org.csu.ipetstore.service.AccountService;
import org.csu.ipetstore.util.MailUtil;
import org.csu.ipetstore.util.SHAUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sun.security.provider.SHA;

import java.util.Arrays;
import java.util.Map;

@SpringBootTest
class IPetStoreApplicationTests {

    @Autowired
    AccountService accountService;

    @Autowired
    MailUtil mailUtil;

    @Test
    void contextLoads() {
        System.out.println(String.join("|", Arrays.asList(
                "history-cart-true-current-empty", "222")));
    }

    @Test
    void testMail(){
        mailUtil.sendMail("1781750070@qq.com","just a try","can you receive my e-mail ?");
    }

    @Test
    void testAccount(){
        Account account = new Account();
        account.setUsername("cyj");
        account.setPassword("cyj");
        account = accountService.getAccount(account);
        account.setUsername("chenyijun");
        accountService.insertAccount(account);
    }

    @Test
    void testSHA(){
        Account account = new Account();
        account.setUsername("j2ee");
        account.setPassword("j2ee");
        account = accountService.getAccount(account);
        account.setUsername("czllll");
        Map<String,String> resultMap = SHAUtil.getResult("czlczlczl");
        account.setSalt(resultMap.get("salt"));
        account.setPassword(resultMap.get("password"));
        accountService.insertAccount(account);
    }


}
