package org.csu.ipetstore.util;

import com.google.common.collect.ImmutableMap;
import org.csu.ipetstore.mapper.ItemMapper;
import org.csu.ipetstore.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Hydra
 * @date 2020/6/18
 * @description: id生成器
 */

@Component
public class IDSequenceUtil {

    @Autowired
    ProductMapper productMapper;

    @Autowired
    ItemMapper itemMapper;

    private static Map<String, String> PRODUCT_PREFIX = ImmutableMap.of(
            "BIRDS", "AV-CB-",
            "FISH", "FI-FW-",
            "CATS", "FL-DLH-",
            "DOGS", "K9-RT-",
            "REPTILES", "RP-LI-");

    private static String ITEM_PREFIX = "EST-";

    /**
     * 功能描述: <br>
     * <生成全新的ProductId>
     * @return 若categoryId传入错误则返回null
     */
    public String constructProductId(String categoryId){
        String prefix = PRODUCT_PREFIX.get(categoryId);
        if(prefix == null){
            return null;
        }
        int index = productMapper.countProductByCategoryId(categoryId) + 1;
        String serial = index < 10 ? "0"+index : String.valueOf(index);
        return prefix + serial;
    }

    public String constructItemId(){
        int index = itemMapper.countItemNum() + 1;
        String serial = index < 10 ? "0"+index : String.valueOf(index);
        return ITEM_PREFIX + serial;
    }
}
