package org.csu.ipetstore.service;

import org.csu.ipetstore.domain.Category;
import org.csu.ipetstore.domain.Item;
import org.csu.ipetstore.domain.Product;

import java.util.List;

public interface CatalogService {
    public List<Category> getCategoryList();

    public Category getCategory(String categoryId);

    public Product getProduct(String productId);

    public List<Product> getProductListByCategory(String categoryId);

    // TODO enable using more than one keyword
    public List<Product> searchProductList(String keyword);

    public List<Item> getItemListByProductId(String productId);

    public Item getItem(String itemId);

    public int getItemQuantityById(String itemId);

    public String[] getAutoCompleteArray(String keyword);
}
