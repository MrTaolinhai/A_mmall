package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVo;

public interface IProductService {
    //保存或者更新操作
    ServerResponse saveOrUpdateProduct(Product product);

    //更新产品销售状态
    ServerResponse SetSaleStatus(Integer productId,Integer status);

    //商品详情
    ServerResponse<ProductDetailVo> manageProductDetail(Integer productId);

    //分页
    ServerResponse<PageInfo> getProductList(int pageNum , int pageSize);
}
