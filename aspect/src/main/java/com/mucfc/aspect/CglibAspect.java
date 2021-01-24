package com.mucfc.aspect;

import com.mucfc.aspect.service.DataProvider;
import com.mucfc.aspect.service.DataProviderImpl;
import net.sf.cglib.proxy.*;
import net.sf.cglib.transform.ClassFilter;

import java.beans.Transient;
import java.lang.reflect.Method;

// https://blog.csdn.net/lizhengyu891231/article/details/95353980
public class CglibAspect {
    public static void main(String[] args) {
        // 科科项目改造
        DataProvider enhancer = (DataProvider) Enhancer.create(DataProviderImpl.class,
                new MethodInterceptor() {
                    @Override
                    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                        System.out.println(method.getDeclaredAnnotation(Transient.class));
                        return proxy.invokeSuper(obj, args);
                    }
                });
        System.out.println(enhancer.getById(1L));
        System.out.println(enhancer.getById(1L));
        System.out.println(enhancer.getById(1L));
    }
}
