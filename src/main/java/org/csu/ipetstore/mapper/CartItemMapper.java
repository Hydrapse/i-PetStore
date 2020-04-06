package org.csu.ipetstore.mapper;

import org.apache.ibatis.annotations.Param;
import org.csu.ipetstore.domain.Account;
import org.csu.ipetstore.domain.CartItem;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.List;

@Repository
public interface CartItemMapper {
    void insertCartItemList(@Param("cartItemList") List<CartItem> cartItemList, @Param("username") String username) throws SQLException;

    void deleteCartItemList(Account account) throws SQLException;

    List<CartItem> getCartItemList(Account account)throws SQLException;
}
