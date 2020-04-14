package org.csu.ipetstore.service.impl;

import org.csu.ipetstore.domain.Account;
import org.csu.ipetstore.mapper.AccountMapper;
import org.csu.ipetstore.service.AccountService;
import org.csu.ipetstore.util.SHAUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

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
        //获得原本的盐
        String salt = accountMapper.getAccountByUsername(account.getUsername()).getSalt();

        //重新获取pwd和salt
        Map<String,String> newMap = SHAUtil.getResult(account.getPassword());

        String pwd = SHAUtil.getResult(account.getPassword(),salt);
        account.setPassword(pwd);
        Account resultAccount = accountMapper.getAccountByUsernameAndPassword(account);

        //每登录一次则随机更换一次salt
        if(resultAccount != null) {
            account.setPassword(newMap.get("password"));
            account.setSalt(newMap.get("salt"));
            accountMapper.updateSignon(account);
        }

        return resultAccount;
    }

    @Override
    public boolean insertAccount(Account account) {
        //插入数据
        accountMapper.insertAccount(account);
        accountMapper.insertProfile(account);

        Map<String,String> resultMap = SHAUtil.getResult(account.getPassword());
        account.setPassword(resultMap.get("password"));
        account.setSalt(resultMap.get("salt"));
        accountMapper.insertSignon(account);
        return true;
    }

    @Override
    public void updateAccount(Account account) {
        accountMapper.updateAccount(account);
        accountMapper.updateProfile(account);

        if(account.getPassword() != null && account.getPassword().length()>0){
            Map<String,String> resultMap = SHAUtil.getResult(account.getPassword());
            account.setPassword(resultMap.get("password"));
            account.setSalt(resultMap.get("salt"));
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
