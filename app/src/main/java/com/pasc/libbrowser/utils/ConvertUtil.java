package com.pasc.libbrowser.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.pasc.lib.net.resp.BaseResp;
import com.pasc.lib.net.resp.VoidObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Copyright (C) 2018 pasc Licensed under the Apache License, Version 2.0 (the "License");
 *
 * 后台返回数据转换工具
 *
 * @author yangzijian
 * @date 2018/9/4
 * @des
 * @modify
 **/
public class ConvertUtil {
    static final Gson assistGson = new Gson();

    public static Gson getConvertGson() {
        Gson gson = new GsonBuilder().registerTypeAdapter(BaseResp.class,
                new JsonDeserializer<BaseResp>() {
                    @Override
                    public BaseResp deserialize(JsonElement json, Type typeOfT,
                                                JsonDeserializationContext context) throws JsonParseException {
                        BaseResp baseResp;
                        //主类型
                        Type itemType = ((ParameterizedType) typeOfT).getActualTypeArguments()[0];
                        //泛型
                        Class typeArgument = itemType instanceof Class ? (Class) itemType : (Class) ((ParameterizedType) itemType).getRawType();
                        if (typeArgument == Void.class) {
                            // 泛型格式如下： new JsonCallback<BaseResp<Void>>(this)
                            baseResp = assistGson.fromJson(json, typeOfT);
                            //noinspection unche return (T) baseResp;
                        } else if (typeArgument == VoidObject.class) {
                            BaseResp tmp = assistGson.fromJson(json, BaseResp.class);
                            baseResp = new BaseResp<VoidObject> ();
                            baseResp.data = VoidObject.getInstance();
                            baseResp.code = tmp.code;
                            baseResp.msg = tmp.msg;
                            //noinspection unche return (T) baseResp;
                        } else if (typeArgument == String.class) {
                            //需求 BaseResp<String>
                            BaseResp tmp = assistGson.fromJson(json, BaseResp.class);
                            if (tmp.data != null && tmp.data instanceof String) {
                                //data本身就是String
                                baseResp = tmp;
                            } else {
                                //data不是String
                                baseResp = new BaseResp<String> ();
                                baseResp.data = assistGson.toJson(tmp.data);
                                baseResp.code = tmp.code;
                                baseResp.msg = tmp.msg;
                            }
                        } else {
                            // 泛型格式如下： new JsonCallback<BaseResp<内层JavaBean>>(this)
                            baseResp = assistGson.fromJson(json, typeOfT);

                        }
                        /*****data 为空时****/
                        if (baseResp.data == null) {
                            if (typeArgument == List.class) {
                                baseResp.data = Collections.EMPTY_LIST;
                            } else if (typeArgument == Set.class) {
                                baseResp.data = Collections.EMPTY_SET;
                            } else if (typeArgument == VoidObject.class) {
                                baseResp.data = VoidObject.getInstance();
                            } else if (typeArgument == Void.class) {
                                try {
                                    Constructor<Void> con = Void.class.getDeclaredConstructor();
                                    con.setAccessible(true);
                                    baseResp.data = con.newInstance();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    baseResp.data = typeArgument.newInstance();
                                } catch (InstantiationException e) {
                                    e.printStackTrace();
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        return baseResp;


                    }
                }).

                create();

        return gson;
    }
}
