package org.csu.ipetstore.controller;

import com.alibaba.fastjson.JSON;
import org.csu.ipetstore.domain.Category;
import org.csu.ipetstore.domain.Item;
import org.csu.ipetstore.domain.Product;
import org.csu.ipetstore.service.CatalogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author Hydra
 * @date 2020/4/2
 */

@Controller
public class CatalogController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CatalogService catalogService;

    @GetMapping("/category")
    public String viewCategory(@RequestParam("categoryId") String categoryId, Model model){
        Category category = catalogService.getCategory(categoryId);
        List<Product> productList = catalogService.getProductListByCategory(categoryId);

        model.addAttribute("category", category);
        model.addAttribute("productList", productList);

        logger.info("查看商品种类 "+ categoryId);
        return "catalog/category";
    }

    @GetMapping("/product/{productId}")
    public String viewProduct(@PathVariable("productId")String productId, Model model){
        Product product = catalogService.getProduct(productId);
        List<Item> itemList = catalogService.getItemListByProduct(productId);
        logger.info("查看产品 "+ product.getCategoryId() + "/" + productId);

        model.addAttribute("product", product);
        model.addAttribute("itemList", itemList);

        return "catalog/product";
    }

    //查询页面
    @GetMapping("/products")
    public String searchProduct(@RequestParam("keyword")String keyword, Model model){
        if(StringUtils.isEmpty(keyword)){
            return "redirect:/main.html";
        }

        List<Product> productList = catalogService.searchProductList("%" + keyword.toLowerCase() + "%");
        model.addAttribute("productList", productList);
        logger.info("根据关键字 '"+ keyword +"' 搜索相关产品");

        return "catalog/searchProduct";
    }


    @GetMapping("/item/{itemId}")
    public String viewItem(@PathVariable("itemId")String itemId, Model model){
        Item item = catalogService.getItem(itemId);
        Product product = item.getProduct();
        logger.info("查看具体项 " + product.getCategoryId() + "/" + product.getProductId() + "/" + itemId);

        model.addAttribute("item", item);
        model.addAttribute("product", product);

        return "catalog/item";
    }

    @ResponseBody
    @GetMapping("/searchAutoComplete")
    public String searchAutoComplete(@RequestParam("keyword") String keyword){
        if(StringUtils.isEmpty(keyword)){
            logger.debug("字符串为空");
            return "false";
        }

        String[] nameArray = catalogService.getAutoCompleteArray(keyword);
        if (nameArray.length == 0 || nameArray[0] == null || nameArray[0].isEmpty()){
            logger.debug("Cannot find product by '" + keyword + "'");
            return "false";
        }
        logger.debug(JSON.toJSONString(nameArray));

        return JSON.toJSONString(nameArray);
    }

}
