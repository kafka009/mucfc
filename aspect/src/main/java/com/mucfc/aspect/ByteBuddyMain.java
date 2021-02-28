package com.mucfc.aspect;

import com.mucfc.aspect.download.Download;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.pool.TypePool;
import org.mapstruct.Mapper;

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
//        TypePool typePool = TypePool.ClassLoading.ofSystemLoader();
        DynamicType.Loaded<?> load = new ByteBuddy()
//                .decorate(typePool.describe("com.mucfc.aspect.download.Download").resolve(), ClassFileLocator.ForFolder.ForClassLoader.ofSystemLoader())
                .redefine(Download.class)
                .annotateType(AnnotationDescription.Builder.ofType(Mapper.class).build())
                .make()
                .load(Download.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());

        System.out.println(load.getLoaded().getAnnotations().length);
        System.out.println(Download.class.getClassLoader());
        System.out.println("===================================");
        System.out.println(Download.class.getAnnotations().length);
        System.out.println(helloWorld + "111");  // Hello World!
    }
}
