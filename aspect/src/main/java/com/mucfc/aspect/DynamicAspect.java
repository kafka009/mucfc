package com.mucfc.aspect;

import com.mucfc.aspect.service.DataProvider;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class DynamicAspect {
    private static ThreadLocal<User> local = new ThreadLocal<>();

    public static void main(String[] args) {
//        DataProviderImpl provider = new DataProviderImpl();
        InvocationHandler handler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if ("getById".equals(method.getName())) {
                    System.out.println("===");
                    if (null != local.get()) {
                        return local.get();
                    }

//                    User user = provider.getById((long) args[0]);
//                    User user = (User) method.invoke(provider, args);
//                    local.set(user);
//                    return user;
                } else if ("run".equals(method.getName())) {
                    System.out.println(proxy.getClass() + "run......");
                    return null;
                }

                return method.invoke(proxy, args);
            }
        };

        DataProvider proxy = (DataProvider) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{DataProvider.class, Runnable.class}, handler);

        System.out.println(proxy.getById(12L));
        System.out.println(proxy.getById(12L));
        System.out.println(proxy.getById(12L));
        System.out.println(proxy.getById(12L));

        Runnable runnable = (Runnable) proxy;
        runnable.run();
    }
}
