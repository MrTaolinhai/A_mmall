package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * 产品管理模块
 */

@Controller
@RequestMapping("/manager/product")
public class productManagerController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IProductService iProductService;


    /**
     * 商品保存
     * @param httpSession
     * @param product
     * @return
     */
    @RequestMapping("save.do")
    @ResponseBody
    public ServerResponse productSave(HttpSession httpSession, Product product){
        //强制跳转到登录页面
        User user = (User) httpSession.getAttribute(Const.CURRENT_USER);
        //判断用户是否存在,不存在强制跳转到登录页面
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录,请登录管理员");
        }
        //判断管理员信息
        if(iUserService.checkAdminRole(user).isSuccess()){
            //处理业务逻辑
            return  iProductService.saveOrUpdateProduct(product);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    /**
     *  商品上下架
     */

    @RequestMapping("set_sale_status.do")
    @ResponseBody
    public ServerResponse SetSaleStatus(HttpSession httpSession, Integer productId,Integer status){
        //强制跳转到登录页面
        User user = (User) httpSession.getAttribute(Const.CURRENT_USER);
        //判断用户是否存在,不存在强制跳转到登录页面
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录,请登录管理员");
        }
        //判断管理员信息
        if(iUserService.checkAdminRole(user).isSuccess()){
            //处理业务逻辑
            return iProductService.SetSaleStatus(productId,status);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }


    /**
     *  商品详情
     */

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse getDetail(HttpSession httpSession, Integer productId){
        //强制跳转到登录页面
        User user = (User) httpSession.getAttribute(Const.CURRENT_USER);
        //判断用户是否存在,不存在强制跳转到登录页面
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录,请登录管理员");
        }
        //判断管理员信息
        if(iUserService.checkAdminRole(user).isSuccess()){
            //处理业务逻辑
           return iProductService.manageProductDetail(productId);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    /**
     * 商品列表
     */
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse getList(HttpSession httpSession, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,@RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        //强制跳转到登录页面
        User user = (User) httpSession.getAttribute(Const.CURRENT_USER);
        //判断用户是否存在,不存在强制跳转到登录页面
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录,请登录管理员");
        }
        //判断管理员信息
        if(iUserService.checkAdminRole(user).isSuccess()){
            //处理业务逻辑
            return iProductService.getProductList(pageNum,pageSize);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    /**
     * 搜索
     */
    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse productSearch(HttpSession httpSession,String productName6                         ,@RequestParam(value = "pageNum",defaultValue = "1") int pageNum,@RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        //强制跳转到登录页面
        User user = (User) httpSession.getAttribute(Const.CURRENT_USER);
        //判断用户是否存在,不存在强制跳转到登录页面
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录,请登录管理员");
        }
        //判断管理员信息
        if(iUserService.checkAdminRole(user).isSuccess()){
            //处理业务逻辑
            return iProductService.getProductList(pageNum,pageSize);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

}
