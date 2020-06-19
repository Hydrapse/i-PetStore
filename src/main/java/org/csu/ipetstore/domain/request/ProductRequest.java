package org.csu.ipetstore.domain.request;

import lombok.Data;

/**
 * @author Hydra
 * @date 2020/6/19
 * @description: product查询请求封装类
 */
@Data
public class ProductRequest {
    private String name;

    private String[] categoryIdList;
}
