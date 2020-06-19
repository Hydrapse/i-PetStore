package org.csu.ipetstore.mapper;

import org.csu.ipetstore.domain.Product;
import org.csu.ipetstore.domain.request.ProductRequest;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductMapper {

    // 根据大类categoryId 来查询属于该类的所有Product
    List<Product> getProductListByCategory(String categoryId);

    // 根据小类 productId 来查询该product对象
    Product getProduct(String productId);

    // 根据关键字 keywords 查询所有符合条件的Product
    List<Product> searchProductList(String keywords);

    //为了智能化查询，同时检索Category匹配字段
    List<Product> searchCategoryProductList(String keywords);

    //查询自动补全
    List<String> searchProductNameList(String keywords);

    //根据限定条件 分页查询商品
    List<Product> getProductsByProductRequest(ProductRequest productKey);

    //更新Product
    void updateProduct(Product product);

    //删除Product
    boolean deleteProduct(String productId);

    //插入Product
    boolean insertProduct(Product product);

    //获取某Category对应Product数量
    int countProductByCategoryId(String categoryId);
}
