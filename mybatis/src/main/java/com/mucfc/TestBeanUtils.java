package com.mucfc;

import com.mucfc.mybatis.WebSite;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Date;

public class TestBeanUtils {
    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        Logger logger = LogManager.getLogger(TestBeanUtils.class);
        WebSite webSite = new WebSite();
        webSite.setId(4L);
        webSite.setClazz("test");
        webSite.setCreateTime(new Date());
        webSite.setDesc("desc");
        webSite.setName("kafka");
        webSite.setUpdateTime(new Date());
        webSite.setUrl("http://www.baidu.com");

        for (int j = 0; j < 10; j++) {
            {
                // 支持跳过属性
                long start = System.currentTimeMillis();
                for (int i = 0; i < 100000; i++) {
                    WebSite target = new WebSite();
                    BeanUtils.copyProperties(webSite, target);
                }
                logger.info("spring copy: {}", (System.currentTimeMillis() - start));
            }
            {
                // 类型转换
                long start = System.currentTimeMillis();
                for (int i = 0; i < 100000; i++) {
                    WebSite target = new WebSite();
                    org.apache.commons.beanutils.BeanUtils.copyProperties(target, webSite);
                }
                logger.info("apache copy: {}", (System.currentTimeMillis() - start));
            }
            {
                // 基本copy
                long start = System.currentTimeMillis();
                for (int i = 0; i < 100000; i++) {
                    WebSite target = new WebSite();
                    PropertyUtils.copyProperties(target, webSite);
                }
                logger.info("apache property copy: {}", (System.currentTimeMillis() - start));
            }
            {
                // 基本copy
                long start = System.currentTimeMillis();
                for (int i = 0; i < 100000; i++) {
                    org.apache.commons.beanutils.BeanUtils.cloneBean(webSite);
                }
                logger.info("apache clone: {}", (System.currentTimeMillis() - start));
            }
            {
                // self copy
                long start = System.currentTimeMillis();
                for (int i = 0; i < 100000; i++) {
                    WebSite target = new WebSite();
                    com.mucfc.BeanUtils.copyProperties(webSite, target);
                }
                logger.info("self copy: {}", (System.currentTimeMillis() - start));
            }
            {
                // self clone
                long start = System.currentTimeMillis();
                for (int i = 0; i < 100000; i++) {
                    com.mucfc.BeanUtils.cloneBean(webSite);
                }
                logger.info("self clone: {}", (System.currentTimeMillis() - start));
            }
            {
                // cglib
                long start = System.currentTimeMillis();
                for (int i = 0; i < 100000; i++) {
                    com.mucfc.BeanUtils.cloneBean(webSite, Collections.singletonList("name"));
                }
                logger.info("cglib copier: {}", (System.currentTimeMillis() - start));
            }
            {
                long start = System.currentTimeMillis();
                for (int i = 0; i < 100000; i++) {
                    WebSite target = new WebSite();
                    target.setClazz(webSite.getClazz());
                    target.setCreateTime(webSite.getCreateTime());
                    target.setDesc(webSite.getDesc());
                    target.setName(webSite.getName());
                    target.setUpdateTime(webSite.getUpdateTime());
                    target.setUrl(webSite.getUrl());
                }
                logger.info("new: {}", (System.currentTimeMillis() - start));
            }
            System.out.println("=============================");
        }
    }
}
