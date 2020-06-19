package org.csu.ipetstore.domain.request;

import lombok.Data;

/**
 * @author Hydra
 * @date 2020/6/19
 * @description: account查询请求封装类
 */
@Data
public class UserRequest {
    private String username;

    private String[] cityList;
}
