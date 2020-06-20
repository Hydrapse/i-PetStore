package org.csu.ipetstore.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.csu.ipetstore.domain.Item;
import org.csu.ipetstore.domain.request.OrderRequest;
import org.csu.ipetstore.domain.request.PageRequest;
import org.csu.ipetstore.domain.request.ProductRequest;
import org.csu.ipetstore.domain.request.UserRequest;
import org.csu.ipetstore.domain.result.PageResult;
import org.csu.ipetstore.domain.Product;
import org.csu.ipetstore.domain.result.CommonResult;
import org.csu.ipetstore.domain.result.ResultCode;
import org.csu.ipetstore.service.CatalogService;
import org.csu.ipetstore.service.ManagerService;
import org.csu.ipetstore.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Hydra
 * @date 2020/6/15
 * @description 实验二后台管理系统 API接口
 */
@Api(tags = "后台管理系统API接口")
@RestController
@RequestMapping("/manager")
public class ManagerController {
    private static final Logger logger = LoggerFactory.getLogger(ManagerController.class);

    @Autowired
    ManagerService managerService;

    @Autowired
    CatalogService catalogService;

    @Autowired
    OrderService orderService;

    @ApiOperation("测试入口")
    @GetMapping("/hello")
    @ResponseStatus(HttpStatus.OK)
    public CommonResult hello(){
        return CommonResult.success();
    }

    @ApiOperation("根据限定条件获取product列表")
    @GetMapping("/products")
    public CommonResult productList(ProductRequest prodKey, PageRequest pageRequest){
        //初始校验pageRequest
        pageRequest.initialValidate(1, 6);

        logger.info("查询请求: " + prodKey);
        logger.info("分页数据: " + pageRequest);

        PageResult pageResult = managerService.findProductPage(prodKey, pageRequest);

        if(pageResult.getPageSize() == 0) {
            return CommonResult.failure(ResultCode.PRODUCT_NOT_FOUND, "不存在匹配商品");
        }
        return CommonResult.success(pageResult);
    }

    @ApiOperation("查看product详情, 返回对应product及itemList")
    @GetMapping("/products/{productId}")
    public CommonResult viewProduct(@PathVariable("productId")String productId){
        //如果字符串为空直接报错
        if(StringUtils.isEmpty(productId)){
            return CommonResult.failure("productId为空");
        }

        Product product = catalogService.getProduct(productId);
        List<Item> itemList = catalogService.getItemListByProductId(productId);

        if(product == null){
            return CommonResult.failure(ResultCode.PRODUCT_NOT_FOUND, "找不到对应product");
        }
        //如果查询结果为空则返回资源找不到
        if(itemList==null || itemList.size() == 0){
            return CommonResult.failure(ResultCode.ITEM_NOT_EXIST,
                    "该product没有对应的item, 需要跳转至item创建页面");
        }

        Map map = new HashMap();
        map.put("product", product);
        map.put("itemList", itemList);
        return CommonResult.success(map);
    }

    @ApiOperation("修改product信息, 如果不上传图片默认不修改图片")
    @PutMapping("/products/{productId}")
    public CommonResult updateProduct(@PathVariable("productId")String productId,
                                      Product product,
                                      MultipartFile img){
        //如果字符串为空直接报错
        if(StringUtils.isEmpty(productId)){
            return CommonResult.failure("原productId为空");
        }
        else if(StringUtils.isEmpty(product.getName())){
            return CommonResult.failure("productName未填写");
        }
        //检验productId是否正确
        Product preProd = catalogService.getProduct(productId);
        if(preProd == null){
            return CommonResult.failure(ResultCode.PRODUCT_NOT_FOUND, "找不到对应product");
        }

        //更新商品
        product.setImgUrl(preProd.getImgUrl()); //先将原imgUrl传入
        managerService.setProductImage(product, img); //若img不为空则插入
        Product rtnProd = managerService.updateProduct(product);

        return CommonResult.success(rtnProd);
    }

    @ApiOperation("删除product, 并按照相同规则删除包含Item")
    @DeleteMapping("/products/{productId}")
    public CommonResult deleteProduct(@PathVariable("productId")String productId){
        Map map = managerService.deleteProduct(productId); //业务入口
        int code = (int)map.get("code"); //获取返回码
        map.remove("code"); //删除map中返回码, 避免干扰

        //判断返回码
        if(code == 1){
            return CommonResult.success();
        }
        return CommonResult.failure(code, "删除失败", map);
    }

    @ApiOperation(value = "删除Item, 并删除库存信息, 保留用户购物车信息",
                  notes = "判断未发货订单中是否含有被删除item, 若有 取消删除并返回orderList")
    @DeleteMapping("/products/items/{itemId}")
    public CommonResult deleteItem(@PathVariable("itemId")String itemId){
        List list = managerService.deleteItem(itemId); //业务入口

        //判断是否成功删除
        if(list.isEmpty()){
            return CommonResult.success();
        }
        return CommonResult.failure(ResultCode.ITEM_UNDELIVERED_CANT_DELETE, "未发货订单中含有item, 取消删除并返回orderList", list);
    }

    @ApiOperation("修改Item库存")
    @PatchMapping("/products/items/{itemId}")
    public CommonResult updateInventory(@PathVariable("itemId")String itemId, int qty){
        if(managerService.setInventoryQuantity(itemId, qty)){
            return CommonResult.success();
        }
        return CommonResult.failure(ResultCode.ITEM_NOT_FOUND, "库存更新失败, 库存表没有对应item");
    }

    @ApiOperation("新增product")
    @PostMapping("/products")
    public CommonResult addProduct(Product product, MultipartFile img){
        if(StringUtils.isEmpty(product.getName())){
            logger.warn("product插入时name为空");
            return CommonResult.failure("name为空");
        }

        //若img不为空则插入图片
        managerService.setProductImage(product, img);
        Product rtnProd = managerService.createProduct(product);
        if(rtnProd == null){
            return CommonResult.failure(ResultCode.PARAM_IS_INVALID, "传入值包含非法值");
        }
        return CommonResult.success(rtnProd);
    }

    @ApiOperation("新增item")
    @PostMapping("/products/items")
    public CommonResult addItem(Item item){
        if(StringUtils.isEmpty(item.getProductId())){
            logger.warn("item插入时productId为空");
            return CommonResult.failure(ResultCode.PARAM_IS_NULL, "productId为空");
        }

        //插入item
        Item rtnItem = managerService.createItem(item);
        if(item == null){
            return CommonResult.failure("item插入数据库失败");
        }
        if(item.getQuantity() == -1){
            return CommonResult.failure(ResultCode.INVENTORY_HAS_EXIST, "item插入成功, 库存插入失败");
        }
        return CommonResult.success(rtnItem);
    }

    @ApiOperation("根据条件分页查询订单")
    @GetMapping("/orders")
    public CommonResult orderList(OrderRequest orderRequest, PageRequest pageRequest){
        //初始校验pageRequest
        pageRequest.initialValidate(1, 10);

        logger.info("查询请求: " + orderRequest);
        logger.info("分页数据: " + pageRequest);

        //根据参数查询
        PageResult pageResult = orderService.findOrderPage(orderRequest, pageRequest);
        if(pageResult.getPageSize() == 0) {
            logger.info("不存在匹配订单");
            return CommonResult.failure(ResultCode.ORDER_NOT_FOUND, "不存在匹配订单");
        }
        return CommonResult.success(pageResult);
    }

    @ApiOperation("根据条件分页查询用户信息")
    @GetMapping("/users")
    public CommonResult userList(UserRequest userRequest, PageRequest pageRequest){
        //初始校验pageRequest
        pageRequest.initialValidate(1, 10);

        logger.info("查询请求: " + userRequest);
        logger.info("分页数据: " + pageRequest);

        //根据参数查询
        PageResult pageResult = managerService.findUserPage(userRequest, pageRequest);
        if(pageResult.getPageSize() == 0) {
            logger.info("不存在匹配用户");
            return CommonResult.failure(ResultCode.USER_NOT_EXIST, "不存在匹配用户");
        }
        return CommonResult.success(pageResult);
    }



}
