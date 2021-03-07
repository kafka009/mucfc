package com.mucfc;

import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.FatalBeanException;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.cglib.core.Converter;
import org.springframework.lang.Nullable;

import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.net.URI;
import java.net.URL;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class BeanUtils {
    private static final ConvertUtilsBean convertUtilsBean = new ConvertUtilsBean();
    private static final ConcurrentMap<Class<?>, LinkedHashMap<String, PropertyDescriptor>> propertyDescriptorsMap = new ConcurrentHashMap<>(32);
    private static final ConcurrentMap<Class<?>, List<String>> simplePropertiesMap = new ConcurrentHashMap<>(32);
    private static final ConcurrentMap<Class<?>, List<String>> complexPropertiesMap = new ConcurrentHashMap<>(32);
    private static final ConcurrentMap<Class<?>, BeanCopier> beanCopierConcurrentMapWithoutConverter = new ConcurrentHashMap<>(32);
    private static final ConcurrentMap<Class<?>, BeanCopier> beanCopierConcurrentMap = new ConcurrentHashMap<>(32);
    private static final ConcurrentMap<Tuple3<Class<?>, Class<?>, Boolean>, BeanCopier> beanCopierCustomConcurrentMap = new ConcurrentHashMap<>(32);

    private static String getPropertyFromMethodName(String methodName) {
        if (methodName.startsWith("set") || methodName.startsWith("get")) {
            return StringUtils.uncapitalize(methodName.substring(3));
        } else if (methodName.startsWith("is")) {
            return StringUtils.uncapitalize(methodName.substring(2));
        } else {
            return methodName;
        }
    }

    private static Converter createIgnoreConverter(List<String> ignoreProperties) {
        return (o2, aClass1, o11) -> {
            if (ignoreProperties.contains(getPropertyFromMethodName((String) o11))) {
                if (aClass1.isPrimitive()) {
                    if (Boolean.TYPE.equals(aClass1)) {
                        return false;
                    } else if (Character.TYPE.equals(aClass1)) {
                        return ' ';
                    } else {
                        return 0;
                    }
                } else {
                    return null;
                }
            }
            return o2;
        };
    }

    private static Converter createConverter(List<String> ignoreProperties) {
        return (o2, aClass1, o11) -> {
            if (ignoreProperties.contains(getPropertyFromMethodName((String) o11))) {
                if (aClass1.isPrimitive()) {
                    if (Boolean.TYPE.equals(aClass1)) {
                        return false;
                    } else if (Character.TYPE.equals(aClass1)) {
                        return ' ';
                    } else {
                        return 0;
                    }
                } else {
                    return null;
                }
            }

            if (aClass1.isInstance(o2)) {
                return o2;
            }
            return convertUtilsBean.convert(o2, aClass1);
        };
    }

    public static <T> T instantiate(Class<T> clazz) {
        if (clazz.isInterface()) {
            throw new BeanInstantiationException(clazz, "Specified class is an interface");
        }
        try {
            return clazz.newInstance();
        } catch (InstantiationException ex) {
            throw new BeanInstantiationException(clazz, "Is it an abstract class?", ex);
        } catch (IllegalAccessException ex) {
            throw new BeanInstantiationException(clazz, "Is the constructor accessible?", ex);
        }
    }

    /**
     * 以原类型克隆对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T cloneBean(final T bean) {
        Class<T> clazz = (Class<T>) bean.getClass();
        BeanCopier beanCopier = beanCopierConcurrentMapWithoutConverter.computeIfAbsent(clazz, key -> BeanCopier.create(clazz, clazz, false));

        try {
            T newBean = clazz.newInstance();
            beanCopier.copy(bean, newBean, null);
            return newBean;
        } catch (InstantiationException ex) {
            throw new BeanInstantiationException(clazz, "Is it an abstract class?", ex);
        } catch (IllegalAccessException ex) {
            throw new BeanInstantiationException(clazz, "Is the constructor accessible?", ex);
        }
    }

    public static <T> T cloneBean(final T bean, String... ignoreProperties) {
        if (null == ignoreProperties || ignoreProperties.length == 0) {
            return cloneBean(bean);
        }

        return cloneBean(bean, Arrays.asList(ignoreProperties));
    }

    @SuppressWarnings("unchecked")
    public static <T> T cloneBean(final T bean, final List<String> ignoreProperties) {
        Class<T> clazz = (Class<T>) bean.getClass();
        BeanCopier beanCopier = beanCopierConcurrentMap.computeIfAbsent(clazz, key -> BeanCopier.create(clazz, clazz, true));

        try {
            T newBean = clazz.newInstance();
            beanCopier.copy(bean, newBean, createIgnoreConverter(ignoreProperties));
            return newBean;
        } catch (InstantiationException ex) {
            throw new BeanInstantiationException(clazz, "Is it an abstract class?", ex);
        } catch (IllegalAccessException ex) {
            throw new BeanInstantiationException(clazz, "Is the constructor accessible?", ex);
        }
    }

    private static LinkedHashMap<String, PropertyDescriptor> getPropertyDescriptors(Class<?> clazz) {
        return propertyDescriptorsMap.computeIfAbsent(clazz, x -> {
            PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(x);
            LinkedHashMap<String, PropertyDescriptor> hashMap = new LinkedHashMap<>(descriptors.length * 2);
            for (PropertyDescriptor descriptor : descriptors) {
                if (!Modifier.isPublic(descriptor.getReadMethod().getModifiers())) {
                    descriptor.getReadMethod().setAccessible(true);
                }
                if (!Modifier.isPublic(descriptor.getWriteMethod().getModifiers())) {
                    descriptor.getWriteMethod().setAccessible(true);
                }
                hashMap.put(descriptor.getName(), descriptor);
            }
            return hashMap;
        });
    }

    @Nullable
    public static PropertyDescriptor getPropertyDescriptor(Class<?> clazz, String propertyName) {
        return getPropertyDescriptors(clazz).get(propertyName);
    }

    @Nullable
    public static PropertyDescriptor findPropertyForMethod(Method method) {
        return findPropertyForMethod(method.getDeclaringClass(), method);
    }

    @Nullable
    public static PropertyDescriptor findPropertyForMethod(Class<?> clazz, Method method) {
        for (Map.Entry<String, PropertyDescriptor> entry : getPropertyDescriptors(clazz).entrySet()) {
            PropertyDescriptor descriptor = entry.getValue();
            if (method.equals(descriptor.getReadMethod()) || method.equals(descriptor.getWriteMethod())) {
                return descriptor;
            }
        }
        return null;
    }

    public static List<String> getSimpleProperties(Class<?> type) {
        return simplePropertiesMap.computeIfAbsent(type, x -> {
            Map<String, PropertyDescriptor> propertyDescriptorMap = getPropertyDescriptors(type);
            List<String> simpleProperties = new ArrayList<>(propertyDescriptorMap.size());
            for (Map.Entry<String, PropertyDescriptor> entry : propertyDescriptorMap.entrySet()) {
                Class<?> propertyType = entry.getValue().getPropertyType();
                if (isSimpleProperty(propertyType)) {
                    simpleProperties.add(entry.getKey());
                }
            }
            return simpleProperties;
        });
    }

    public static List<String> getComplexProperties(Class<?> type) {
        return complexPropertiesMap.computeIfAbsent(type, x -> {
            List<String> complexProperties = new ArrayList<>(4);
            for (Map.Entry<String, PropertyDescriptor> entry : getPropertyDescriptors(type).entrySet()) {
                Class<?> propertyType = entry.getValue().getPropertyType();
                if (!isSimpleProperty(propertyType)) {
                    complexProperties.add(entry.getKey());
                }
            }
            return complexProperties;
        });
    }

    /**
     * @return 是否是简单类型，或者简单类型的数组
     */
    public static boolean isSimpleProperty(Class<?> type) {
        return isSimpleValueType(type) || (type.isArray() && isSimpleValueType(type.getComponentType()));
    }

    /**
     * @return 是否是简单类型（不包括数组）
     */
    public static boolean isSimpleValueType(Class<?> type) {
        return (Void.class != type && void.class != type &&
                (ClassUtils.isPrimitiveOrWrapper(type) ||
                        Enum.class.isAssignableFrom(type) ||
                        CharSequence.class.isAssignableFrom(type) ||
                        Number.class.isAssignableFrom(type) ||
                        Date.class.isAssignableFrom(type) ||
                        Temporal.class.isAssignableFrom(type) ||
                        TemporalAmount.class.isAssignableFrom(type) ||
                        URI.class == type ||
                        URL.class == type ||
                        Locale.class == type ||
                        Class.class == type));
    }

    public static void copyProperties(Object source, Object target) {
        copyProperties(source, target, null);
    }

    public static void copyProperties(Map<String, Object> source, Object target) {
        Class<?> targetType = target.getClass();
        Map<String, PropertyDescriptor> propertyDescriptorMap = getPropertyDescriptors(targetType);

        for (final Map.Entry<String, Object> entry : source.entrySet()) {
            final String name = entry.getKey();
            PropertyDescriptor targetPd = propertyDescriptorMap.get(name);
            if (null == targetPd) {
                continue;
            }

            Object value = entry.getValue();
            Method writeMethod = targetPd.getWriteMethod();
            Class<?> sourceType = value.getClass();
            try {
                if (!targetType.isAssignableFrom(sourceType)) {
                    value = convertUtilsBean.convert(value, targetType);
                }
                writeMethod.invoke(target, value);
            } catch (Throwable ex) {
                throw new FatalBeanException("Could not copy property '" + targetPd.getName() + "' from source to target", ex);
            }
        }
    }

    public static void copyProperties(Object source, Object target, List<String> ignoreList) {
        Class<?> sourceType = source.getClass();
        Class<?> targetType = target.getClass();
        boolean ignore = null == ignoreList || ignoreList.isEmpty();
        BeanCopier beanCopier = beanCopierCustomConcurrentMap.computeIfAbsent(new Tuple3<>(sourceType, targetType, !ignore), tuple3 -> BeanCopier.create(tuple3.getO1(), tuple3.getO2(), tuple3.getO3()));
        beanCopier.copy(source, target, ignore ? null : createIgnoreConverter(ignoreList));
    }

    public static void copyPropertiesWithMapping(Object source, Object target, FieldMapping mapping) {
        Class<?> actualEditable = target.getClass();

        for (Map.Entry<String, PropertyDescriptor> entry : getPropertyDescriptors(actualEditable).entrySet()) {
            PropertyDescriptor targetPd = entry.getValue();
            Method writeMethod = targetPd.getWriteMethod();
            if (null == writeMethod) {
                continue;
            }

            String readField = mapping.mapping(targetPd);
            if (null == readField) {
                readField = targetPd.getName();
            }

            PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(), readField);
            if (null == sourcePd) {
                if (readField.equals(targetPd.getName())) {
                    continue;
                }
                readField = targetPd.getName();
                sourcePd = getPropertyDescriptor(source.getClass(), readField);
                if (null == sourcePd) {
                    continue;
                }
            }
            Method readMethod = sourcePd.getReadMethod();
            if (null == readMethod) {
                continue;
            }

            Class<?> targetType = writeMethod.getParameterTypes()[0];

            try {
                Object value = readMethod.invoke(source);
                Class<?> sourceType = value.getClass();
                if (targetType != sourceType && !targetType.isAssignableFrom(sourceType)) {
                    value = convertUtilsBean.convert(value, targetType);
                }
                writeMethod.invoke(target, value);
            } catch (Throwable ex) {
                throw new FatalBeanException("Could not copy property '" + targetPd.getName() + "' from source to target", ex);
            }
        }
    }

    public static void copyPropertiesWithMapping(Object source, Object target, FieldMapping mapping, int depth) {
        if (depth <= 1) {
            copyPropertiesWithMapping(source, target, mapping);
            return;
        }

        Class<?> actualEditable = target.getClass();

        for (Map.Entry<String, PropertyDescriptor> entry : getPropertyDescriptors(actualEditable).entrySet()) {
            PropertyDescriptor targetPd = entry.getValue();
            Method writeMethod = targetPd.getWriteMethod();
            if (null == writeMethod) {
                continue;
            }

            String readField = mapping.mapping(targetPd);
            if (null == readField) {
                readField = targetPd.getName();
            }

            PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(), readField);
            if (null == sourcePd) {
                if (readField.equals(targetPd.getName())) {
                    continue;
                }
                readField = targetPd.getName();
                sourcePd = getPropertyDescriptor(source.getClass(), readField);
                if (null == sourcePd) {
                    continue;
                }
            }
            Method readMethod = sourcePd.getReadMethod();
            if (null == readMethod) {
                continue;
            }

            Class<?> targetType = writeMethod.getParameterTypes()[0];

            if (targetType.isArray()) {
                try {
                    Class<?> componentType = targetType.getComponentType();
                    Object value = readMethod.invoke(source);
                    if (value instanceof List) {
                        List list = (List) value;
                        int i = 0;
                        Object array = Array.newInstance(componentType, list.size());
                        for (Object item : list) {
                            Object targetItem = componentType.newInstance();
                            copyPropertiesWithMapping(item, targetItem, mapping, depth - 1);
                            Array.set(array, i++, targetItem);
                        }
                        writeMethod.invoke(target, array);
                    } else if (value.getClass().isArray()) {
                        int length = Array.getLength(value);
                        Object array = Array.newInstance(componentType, length);
                        for (int j = length - 1; j >= 0; j--) {
                            Object targetItem = componentType.newInstance();
                            copyPropertiesWithMapping(Array.get(value, j), targetItem, mapping, depth - 1);
                            Array.set(array, j, targetItem);
                        }
                        writeMethod.invoke(target, array);
                    }
                } catch (Throwable ex) {
                    throw new FatalBeanException("Could not create property '" + targetPd.getName() + "' from source to target", ex);
                }
            } else if (List.class.isAssignableFrom(targetType)) {
                try {
                    Object value = readMethod.invoke(source);
                    Class<?> componentType = get(writeMethod.getParameterTypes()[0]);
                    if (value instanceof List) {
                        List list = (List) value;
                        int i = 0;
                        List targetList = new ArrayList(list.size());
                        for (Object item : list) {
                            Object targetItem = componentType.newInstance();
                            copyPropertiesWithMapping(item, targetItem, mapping, depth - 1);
                            targetList.add(targetType);
                        }
                        writeMethod.invoke(target, targetList);
                    } else if (value.getClass().isArray()) {
                        int length = Array.getLength(value);
                        List targetList = new ArrayList(length);
                        for (int j = length - 1; j >= 0; j--) {
                            Object targetItem = componentType.newInstance();
                            copyPropertiesWithMapping(Array.get(value, j), targetItem, mapping, depth - 1);
                            targetList.add(targetType);
                        }
                        writeMethod.invoke(target, targetList);
                    }
                } catch (Throwable ex) {
                    throw new FatalBeanException("Could not create property '" + targetPd.getName() + "' from source to target", ex);
                }
            } else if (Map.class.isAssignableFrom(targetType)) {
                if (targetType.isInterface()) {
                    try {
                        Map<?, ?> value = (Map<?, ?>) readMethod.invoke(source);
                        Class<?> componentType = get(writeMethod.getParameterTypes()[0]);
                        Map targetValue = new HashMap<>(value.size() * 2);
                        for (Map.Entry<?, ?> mapEntry : value.entrySet()) {
                            Object entryValue = mapEntry.getValue();
                            Object targetEntryValue = componentType.newInstance();
                            copyPropertiesWithMapping(entryValue, targetEntryValue, mapping, depth - 1);
                            targetValue.put(mapEntry.getKey(), targetEntryValue);
                        }
                        writeMethod.invoke(target, targetValue);
                    } catch (Throwable ex) {
                        throw new FatalBeanException("Could not copy property '" + targetPd.getName() + "' from source to target", ex);
                    }
                } else if (Modifier.isAbstract(targetType.getModifiers())) {
                    throw new FatalBeanException("Could not instantiate abstract class '" + targetType.getName() + "': " + writeMethod);
                } else {
                    try {
                        Map<?, ?> value = (Map<?, ?>) readMethod.invoke(source);
                        Class<?> componentType = get(writeMethod.getParameterTypes()[0]);
                        Map targetValue = (Map) targetType.getConstructor(int.class).newInstance(value.size() * 2);
                        for (Map.Entry<?, ?> mapEntry : value.entrySet()) {
                            Object entryValue = mapEntry.getValue();
                            Object targetEntryValue = componentType.newInstance();
                            copyPropertiesWithMapping(entryValue, targetEntryValue, mapping, depth - 1);
                            targetValue.put(mapEntry.getKey(), targetEntryValue);
                        }
                        writeMethod.invoke(target, targetValue);
                    } catch (Throwable ex) {
                        throw new FatalBeanException("Could not copy property '" + targetPd.getName() + "' from source to target", ex);
                    }
                }
            } else if (isSimpleValueType(targetType)) {
                try {
                    Object value = readMethod.invoke(source);
                    Class<?> sourceType = value.getClass();
                    if (targetType != sourceType && !targetType.isAssignableFrom(sourceType)) {
                        value = convertUtilsBean.convert(value, targetType);
                    }
                    writeMethod.invoke(target, value);
                } catch (Throwable ex) {
                    throw new FatalBeanException("Could not copy property '" + targetPd.getName() + "' from source to target", ex);
                }
            } else {
                try {
                    Object value = readMethod.invoke(source);
                    Object targetValue = targetType.newInstance();
                    copyPropertiesWithMapping(value, targetValue, mapping, depth - 1);
                    writeMethod.invoke(target, targetValue);
                } catch (Throwable ex) {
                    throw new FatalBeanException("Could not deep copy property '" + targetPd.getName() + "' from source to target", ex);
                }
            }
        }
    }

    private static Class<?> get(Type type) {
        ParameterizedType parameterizedType = (ParameterizedType) type;
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        return (Class<?>) actualTypeArguments[actualTypeArguments.length - 1];
    }

    @SuppressWarnings("unchecked")
    public static <T> T convert(final Object value, final Class<T> targetType) {
        return (T) convertUtilsBean.convert(value, targetType);
    }

    public void register(final org.apache.commons.beanutils.Converter converter, final Class<?> clazz) {
        convertUtilsBean.register(converter, clazz);
    }
}
