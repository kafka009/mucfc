package com.mucfc.aspect;

import com.mucfc.aspect.download.Download;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.pool.TypePool;
import org.mapstruct.Mapper;
import org.mapstruct.Qualifier;

import java.lang.reflect.InvocationTargetException;

import static net.bytebuddy.matcher.ElementMatchers.named;

public class ByteBuddyMain {
    public static void main(String[] args) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        String helloWorld = new ByteBuddy()
                .subclass(Object.class)
                .method(named("toString"))
                .intercept(FixedValue.value("Hello World!"))
                .make()
                .load(ByteBuddyMain.class.getClassLoader())
                .getLoaded()
                .getConstructor()
                .newInstance()
                .toString();

        ByteBuddyAgent.install();
        TypePool typePool = TypePool.Default.ofSystemLoader();
        Class<?> x = new ByteBuddy()
                .decorate(typePool.describe("com.mucfc.aspect.download.Download").resolve(), ClassFileLocator.ForClassLoader.ofSystemLoader())
                .annotateType(AnnotationDescription.Builder.ofType(Mapper.class).build())
                .make()
                .load(Thread.currentThread().getContextClassLoader())
                .getLoaded();

        System.out.println(x);
        System.out.println(Download.class.getAnnotations()[0]);
        System.out.println(helloWorld + "111");  // Hello World!
    }
}
