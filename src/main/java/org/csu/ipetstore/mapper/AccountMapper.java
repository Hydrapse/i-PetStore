package org.csu.ipetstore.mapper;

import org.csu.ipetstore.domain.Account;
import org.csu.ipetstore.domain.request.UserRequest;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountMapper {

    Account getAccountByUsername(String username);

    Account getAccountByUsernameAndPassword(Account account);

    void insertAccount(Account account);

    void insertProfile(Account account);

    void insertSignon(Account account);

    void updateAccount(Account account);

    void updateProfile(Account account);

    void updateSignon(Account account);

    String getBannerName(String category);

    List<Account> getUsersByUserRequest(UserRequest userKey);
}
