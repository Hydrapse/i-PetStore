package org.csu.ipetstore.result;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Hydra
 * @date 2020/6/16
 * @description 统一API数据返回格式,仅用于实验二前后端分离卖家管理系统
 */
@Data //提供getter setter equals、hashCode、canEqual、toString 方法
@NoArgsConstructor //自动生成无参构造函数
public final class CommonResult implements Serializable {

    private static final long serialVersionUID = 874200365941306385L;

    private Integer code;

    private String msg;

    private Object data;

    public static CommonResult success() {
        CommonResult result = new CommonResult();
        result.setCode(200);
        return result;
    }

    public static CommonResult success(Object data) {
        CommonResult result = new CommonResult();
        result.setCode(ResultCode.SUCCESS);
        result.setData(data);
        return result;
    }

    public static CommonResult failure(int code, String msg) {
        CommonResult result = new CommonResult();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

    public static CommonResult failure(int code, String msg, Object data) {
        CommonResult result = new CommonResult();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    //参数无效
    public static CommonResult failure(String message) {
        CommonResult result = new CommonResult();
        result.setCode(ResultCode.PARAM_IS_INVALID);
        result.setMsg(message);
        return result;
    }

}

