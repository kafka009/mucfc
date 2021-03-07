package com.mucfc;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.NumberFormat;

public class TestConvert {
    public static void main(String[] args) throws Exception {
        System.out.println(new ConvertUtilsBean().convert("1.3", double.class));
        Logger logger = LogManager.getLogger(TestConvert.class);
        String text = "3.14";
        Method method = Double.class.getMethod("valueOf", String.class);
        ConvertUtilsBean convertUtilsBean = new ConvertUtilsBean2();
        System.out.println(NumberFormat.getInstance().parse("3.141234324312543534543542141325435423434"));
        for (int j = 0; j < 10; j++) {
            {
                // 反射
                long start = System.currentTimeMillis();
                for (int i = 0; i < 10000000; i++) {
                    method.invoke(Double.class, text);
                }
                logger.info("reflect convert: {}", (System.currentTimeMillis() - start));
            }
            {
                // 调用
                long start = System.currentTimeMillis();
                for (int i = 0; i < 10000000; i++) {
                    convertUtilsBean.convert(text, BigDecimal.class);
                }
                logger.info("apache convert: {}", (System.currentTimeMillis() - start));
            }
        }
    }
}
