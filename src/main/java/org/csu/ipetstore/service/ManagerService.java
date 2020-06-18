package org.csu.ipetstore.service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.csu.ipetstore.domain.*;
import org.csu.ipetstore.mapper.ItemMapper;
import org.csu.ipetstore.mapper.OrderMapper;
import org.csu.ipetstore.mapper.ProductMapper;
import org.csu.ipetstore.result.ResultCode;
import org.csu.ipetstore.util.IDSequenceUtil;
import org.csu.ipetstore.util.OSSClientUtil;
import org.csu.ipetstore.util.PageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Hydra
 * @date 2020/6/16
 * @description: 管理系统业务层
 */

@Service
public class ManagerService {
    private static final Logger logger = LoggerFactory.getLogger(ManagerService.class);

    @Autowired
    ProductMapper productMapper;

    @Autowired
    ItemMapper itemMapper;

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    OSSClientUtil ossClientUtil;

    @Autowired
    IDSequenceUtil idSequenceUtil;

    //查找对应商品
    public PageResult findPage(PageRequest pageRequest, Product product) {
        int pageNum = pageRequest.getPageNum();
        int pageSize = pageRequest.getPageSize();

        //只有紧跟在PageHelper.startPage方法后的第一个Mybatis的查询（Select）方法会被分页。
        //将前台分页查询参数传入并拦截MyBtis执行实现分页效果
        PageHelper.startPage(pageNum, pageSize);
        List<Product> productList = productMapper.selectPage(product);

        return PageUtil.getPageResult(pageRequest, new PageInfo<>(productList));
    }

    public void setProductImage(Product product, MultipartFile img){
        //如果img存在, 修改图片
        if(img != null && !img.isEmpty()){
            logger.info("修改product图片");
            String url = ossClientUtil.checkImage(img, OSSClientUtil.PRODUCT_IMAGE_DIR);
            product.setImgUrl(url);
        }
    }

    public Product updateProduct(Product product){
        //更新product
        logger.info("更新product: " + product.toString());
        productMapper.updateProduct(product);

        return product;
    }

    public Map<String, Object> deleteProduct(String productId){
        Map<String, Object> map = new HashMap<>();
        List<String> itemIdList = itemMapper.getItemIdListByProductId(productId);

        //遍历删除item, 如果遇到有待发货的订单项则存入map, 并取消删除product
        for(String itemId : itemIdList){
            List<Order> orderList = deleteItem(itemId);
            if(!orderList.isEmpty()){
                map.put(itemId, orderList);
            }
        }

        //判断是否含有未处理订单
        if(map.size() == 0){ //允许删除product
            if(productMapper.deleteProduct(productId)){
                logger.info("正常删除 product: " + productId);
                map.put("code", 1);
            }
            else{
                logger.warn("删除失败 数据库中不存在 product: " + productId);
                map.put("code", ResultCode.PRODUCT_NOT_FOUND);
            }
        }
        else{ //拒绝删除product
            logger.warn(productId + " 含有未发货订单, 拒绝删除item");
            map.put("code", ResultCode.ITEM_UNDELIVERED_CANT_DELETE);
        }

        return map;
    }

    /**
     * 功能描述: 删除item项
     * @return 所有未发货且包含该item的商品列表
     */
    public List<Order> deleteItem(String itemId){
        //首先查找是否包含: 已付款但未发货的,且含有该item的订单; 全部列出并取消删除item
        List<Order> orderList = orderMapper.getOrdersByItemId(itemId, Order.SUCCEED);

        //如果为空,代表可以删除
        if(orderList.isEmpty()){
            if(itemMapper.deleteItem(itemId)){
                logger.info("正常删除 item: " + itemId);
            }
            else{
                logger.warn("删除失败 数据库中不存在 item:" + itemId);
            }
        }
        else{
            logger.warn(itemId + " 含有未发货订单, 拒绝删除item");
        }

        return orderList;
    }

    //加入事务管理
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public boolean setInventoryQuantity(String itemId, int qty){
        if(qty < 0) {
            qty = 0;
        }
        Map<String, Object> param = new HashMap<>(2);
        param.put("itemId", itemId);
        param.put("qty", qty);

        if(!itemMapper.setInventoryQuantity(param)){
            logger.warn("更新失败 库存表中不存在 " + itemId);
            return false;
        }
        //更新成功
        logger.info(itemId + " 库存更新至 " + qty);
        return true;
    }

    //保证序列号不会冲突
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Product createProduct(Product product){
        String productId = idSequenceUtil.constructProductId(product.getCategoryId());
        if(productId == null){
            logger.warn("CategoryId 为非法值, 取消插入product");
            return null;
        }
        product.setProductId(productId);

        if(!productMapper.insertProduct(product)){
            logger.warn("product 插入失败 显示json串\n" + JSON.toJSONString(product));
            return null;
        }
        logger.info(productId + " 插入成功");
        return product;
    }

    //保证序列号不会冲突
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Item createItem(Item item){
        String itemId = idSequenceUtil.constructItemId();
        item.setItemId(itemId);

        Map<String, Object> param = new HashMap<>(2);
        param.put("itemId", itemId);
        param.put("qty", item.getQuantity());

        //插入数据
        if(!itemMapper.insertItem(item) ){
            logger.warn("item 插入失败 显示json串\n" + JSON.toJSONString(item));
            return null;
        }
        if(!itemMapper.insertInventory(param)){
            logger.warn(itemId + "库存插入失败 显示参数map\n" + param.toString());
            item.setQuantity(-1);
            return item;
        }
        logger.info(itemId + " 插入成功");
        return item;
    }
}
