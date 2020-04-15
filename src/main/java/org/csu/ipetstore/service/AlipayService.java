package org.csu.ipetstore.service;

import org.csu.ipetstore.domain.Order;

/**
 * Created by Enzo Cotter on 2020/4/15.
 */
public interface AlipayService {

    //返回交易跳转表格
    String tradePagePayForm(Order order);
}
