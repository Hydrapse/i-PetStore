package org.csu.ipetstore.service.impl;

import org.csu.ipetstore.domain.Category;
import org.csu.ipetstore.domain.Item;
import org.csu.ipetstore.domain.Product;
import org.csu.ipetstore.mapper.CategoryMapper;
import org.csu.ipetstore.mapper.ItemMapper;
import org.csu.ipetstore.mapper.ProductMapper;
import org.csu.ipetstore.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class CatalogServiceImpl implements CatalogService {

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ItemMapper itemMapper;

    @Override
    public List<Category> getCategoryList() {
        return categoryMapper.getCategoryList();
    }

    @Override
    public Category getCategory(String categoryId) {
        return categoryMapper.getCategory(categoryId);
    }

    @Override
    public Product getProduct(String productId) {
        return productMapper.getProduct(productId);
    }

    @Override
    public List<Product> getProductListByCategory(String categoryId) {
        return productMapper.getProductListByCategory(categoryId);
    }

    @Override
    public List<Product> searchProductList(String keyword) {
        List<Product> pd1 = productMapper.searchProductList("%" + keyword.toLowerCase() + "%");
        System.out.println(pd1.isEmpty());
        List<Product> pd2 = productMapper.searchCategoryProductList("%" + keyword.toLowerCase() + "%");
        System.out.println(pd2.isEmpty());
        for(Product p2 : pd2){
            boolean flag = true;
            for(Product p1 : pd1){
                if(p2.getProductId().equals(p1.getProductId())){
                    flag = false;
                    break;
                }
            }
            if(flag){
                pd1.add(p2);
            }
        }
        return pd1;
    }

    @Override
    public List<Item> getItemListByProductId(String productId) {
        return itemMapper.getItemListByProductId(productId);
    }

    @Override
    public Item getItem(String itemId) {
        return itemMapper.getItem(itemId);
    }

    @Override
    public int getItemQuantityById(String itemId) {
        return itemMapper.getInventoryQuantity(itemId);
    }

    @Override
    public String[] getAutoCompleteArray(String keyword) {
        String[] rtn = new String[1];

        //塞入是否匹配种类名称
        String pattern = ".*" + keyword.toLowerCase() + ".*";
        if (Pattern.matches(pattern, "dog")){
            rtn[0] = "DOGS";
        }else if (Pattern.matches(pattern, "cat")){
            rtn[0] = "CATS";
        }else if (Pattern.matches(pattern, "bird")){
            rtn[0] = "BIRDS";
        }else if (Pattern.matches(pattern, "fish")){
            rtn[0] = "FISH";
        }else if (Pattern.matches(pattern, "reptile")){
            rtn[0] = "REPTILES";
        }

//        try {
            List<String> nameList = productMapper.searchProductNameList("%" + keyword.toLowerCase() + "%");
            if(nameList.isEmpty())
                return rtn;
            int nlSize = nameList.size();

            String[] nameArray;
            if(rtn[0] == null){ //不匹配种类名的情况
                nameArray = new String[nlSize];
                for(int i=0; i< nlSize; i++){
                    nameArray[i] = nameList.get(i);
                }
            }else{//匹配种类名的情况
                nameArray = new String[nlSize+1];
                nameArray[0] = rtn[0];
                for(int i=0; i< nlSize; i++){
                    nameArray[i+1] = nameList.get(i);
                }
            }

            //logger.debug(JSON.toJSONString(nameArray));
            return nameArray;

//        }catch (SQLException e){
//            //logger.error("AutoComplete Keyword: '" + keyword + "' throws Exception");
//            e.printStackTrace();
//        }

        //return rtn;
    }
}
