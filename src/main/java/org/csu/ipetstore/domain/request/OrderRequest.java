package org.csu.ipetstore.domain.request;

import lombok.Data;

/**
 * @author Hydra
 * @date 2020/6/19
 * @description:
 */

@Data
public class OrderRequest {

    private String username;

    private String startDate = "";

    private String endDate;

    private String status;
}
