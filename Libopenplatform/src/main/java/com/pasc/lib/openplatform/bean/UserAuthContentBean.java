package com.pasc.lib.openplatform.bean;

import com.pasc.lib.openplatform.address.AddressResp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 功能：授权列表授权显示实体类
 *      授权列表的显示数据
 *
 * @author lichangbao702
 * @email : lichangbao702@pingan.com.cn
 * @date : 2020/2/10
 */
public class UserAuthContentBean implements Serializable{

    /**
     * 标题，比如 请选择授权xxx
     */
    public String title;

    /**
     * 授权项列表
     */
    public List<ItemBean> itemList;


    public void addItem(ItemBean item){
        if (itemList == null){
            itemList = new ArrayList<>();
        }
        itemList.add(item);
    }

    /**
     * 授权列表的单个选项数据
     */
    public static class ItemBean implements Serializable {

        /**
         * 数据的唯一标识
         */
        public String id;
        /**
         * 显示图标
         */
        public String iconURL;
        /**
         * 标题
         */
        public String title;
        /**
         * 副标题
         */
        public String subTitle;
        /**
         * 是否被选中
         */
        public boolean select;

        /**
         * 原始数据，根据与后台的协议，获取授权码的时候是上传原数据的
         */
        public AddressResp sourceData;

    }

}


