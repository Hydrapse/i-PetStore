package org.csu.ipetstore.mapper;

import org.csu.ipetstore.domain.Item;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ItemMapper {

    void updateInventoryQuantity(Map<String, Object> param);

    int getInventoryQuantity(String itemId);

    List<Item> getItemListByProduct(String productId);

    Item getItem(String itemId);

    List<Item> getItemListByProductName(String productName);

    boolean deleteItem(String itemId);

    boolean setInventoryQuantity(Map<String, Object> param);

    List<String> getItemIdListByProductId(String productId);

    int countItemNum();

    boolean insertItem(Item item);

    boolean insertInventory(Map<String, Object> param);

}
