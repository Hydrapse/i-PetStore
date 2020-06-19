package org.csu.ipetstore.result;

/**
 * @author Hydra
 * @date 2020/6/16
 * @description: 状态码枚举类
 */

public final class ResultCode {

    //成功状态码
    public static final int SUCCESS = 1;

    // -------------------失败状态码----------------------
    // 参数错误
    public static final int PARAM_IS_NULL = 10001;// 参数为空
    public static final int PARAM_NOT_COMPLETE = 10002; // 参数不全
    public static final int PARAM_TYPE_ERROR = 1003; // 参数类型错误
    public static final int PARAM_IS_INVALID = 10004; // 参数无效

    // 用户错误
    public static final int USER_NOT_EXIST = 20001; // 用户不存在
    public static final int USER_NOT_LOGGED_IN = 20002; // 用户未登陆
    public static final int USER_ACCOUNT_ERROR = 20003; // 用户名或密码错误
    public static final int USER_ACCOUNT_FORBIDDEN = 20004; // 用户账户已被禁用
    public static final int USER_HAS_EXIST = 20005;// 用户已存在

    // 商品错误
    public static final int PRODUCT_NOT_FOUND = 30001; //没有对应product
    public static final int ITEM_NOT_EXIST = 30002;// product不存在item
    public static final int ITEM_NOT_FOUND = 30003;// 没有对应item
    public static final int INVENTORY_HAS_EXIST = 30004;// 库存已存在

    // 订单错误
    public static final int ORDER_NOT_FOUND = 40001; //没有对应product

    // 业务错误
    public static final int ITEM_UNDELIVERED_CANT_DELETE= 50001; // 若未发货订单中含有item, 则无法删除该item

    // 接口错误
    public static final int INTERFACE_INNER_INVOKE_ERROR = 60001; // 系统内部接口调用异常
    public static final int INTERFACE_OUTER_INVOKE_ERROR = 60002;// 系统外部接口调用异常
    public static final int INTERFACE_FORBIDDEN = 60003;// 接口禁止访问
    public static final int INTERFACE_ADDRESS_INVALID = 60004;// 接口地址无效
    public static final int INTERFACE_REQUEST_TIMEOUT = 60005;// 接口请求超时
    public static final int INTERFACE_EXCEED_LOAD = 60006;// 接口负载过高

    // 权限错误
    public static final int PERMISSION_NO_ACCESS = 70001;// 没有访问权限



}
