package com.mucfc;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class AnnotationFieldMapping implements FieldMapping {
    @Override
    public String mapping(PropertyDescriptor descriptor) {
        String readFrom = (String) descriptor.getValue("read_from");
        if (null != readFrom) {
            descriptor.setValue("read_from", descriptor.getName());
            return readFrom;
        }

        Method method = descriptor.getWriteMethod();
        CopyField copyField = method.getAnnotation(CopyField.class);
        if (null != copyField) {
            descriptor.setValue("read_from", copyField.value());
            return copyField.value();
        }

        try {
            Field field = descriptor.getWriteMethod().getDeclaringClass().getDeclaredField(descriptor.getName());
            copyField = field.getAnnotation(CopyField.class);
        } catch (NoSuchFieldException ignore) {
        }
        if (null != copyField) {
            descriptor.setValue("read_from", copyField.value());
            return copyField.value();
        }

        descriptor.setValue("read_from", descriptor.getName());
        return descriptor.getName();
    }
}
