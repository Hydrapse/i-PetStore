package org.csu.ipetstore.service.impl;

import org.csu.ipetstore.domain.Account;
import org.csu.ipetstore.mapper.AccountMapper;
import org.csu.ipetstore.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountMapper accountMapper;

    @Override
    public boolean isUsernameExist(String username) {
        if (username.isEmpty() || username == null) {
            return false;
        }
        return accountMapper.getAccountByUsername(username) != null;
    }

    @Override
    public Account getAccount(Account account) {
        return accountMapper.getAccountByUsernameAndPassword(account);
    }

    @Override
    public boolean insertAccount(Account account) {
        //插入数据
        accountMapper.insertAccount(account);
        accountMapper.insertProfile(account);
        accountMapper.insertSignon(account);
        return true;
    }

    @Override
    public void updateAccount(Account account) {
        accountMapper.updateAccount(account);
        accountMapper.updateProfile(account);

        if(account.getPassword() != null && account.getPassword().length()>0){
            accountMapper.updateSignon(account);
        }
    }

    @Override
    public String getBannerName(String category) {
        if(category == null || category.isEmpty()){
            //logger.warn("Favorite Category 名称不为空");
            return "";
        }
        String rtn = "";
        rtn = accountMapper.getBannerName(category);
        return rtn;

//        try {
//            rtn = accountMapper.getBannerName(category);
//        } catch (SQLException e) {
//            //logger.error("GetBannerName Failed");
//            e.printStackTrace();
//        }

    }

    private boolean isVacant(String str) {
        return str.isEmpty() || str == null;
    }
}
