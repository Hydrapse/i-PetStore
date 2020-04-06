package org.csu.ipetstore.service;

import org.csu.ipetstore.domain.Account;

public interface AccountService {

    // 根据用户名取得用户，注册的时候用到
    public boolean isUsernameExist(String username);

    // 根据用户名和密码取得用户，登录的时候用到
    public Account getAccount(Account account);

    // 插入一个新用户，注册的时候用到
    public boolean insertAccount(Account account);

    // 更新一个用户，修改用户用到
    public void updateAccount(Account account);

    String getBannerName(String category);

}
