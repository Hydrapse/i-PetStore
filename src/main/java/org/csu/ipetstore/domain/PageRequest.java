package org.csu.ipetstore.domain;

import lombok.Data;

/**
 * @author Hydra
 * @date 2020/6/16
 * @description: 分页查询请求封装类
 */

@Data
public class PageRequest {
    //当前页码
    private int pageNum = 1;

    //每页数量
    private int pageSize = 10;
}