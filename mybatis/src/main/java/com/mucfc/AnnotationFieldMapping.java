package com.mucfc;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

public class AnnotationFieldMapping implements FieldMapping {
    @Override
    public String mapping(PropertyDescriptor descriptor) {
        String readFrom = (String) descriptor.getValue("read_from");
        if (null != readFrom) {
            return readFrom;
        }

        Method method = descriptor.getWriteMethod();
        CopyField copyField = method.getAnnotation(CopyField.class);
        if (null != copyField) {
            descriptor.setValue("read_from", copyField.value());
            return copyField.value();
        }

        try {
            copyField = descriptor.getPropertyEditorClass().getDeclaredField(descriptor.getName()).getAnnotation(CopyField.class);
        } catch (NoSuchFieldException ignore) {
        }
        if (null != copyField) {
            descriptor.setValue("read_from", copyField.value());
            return copyField.value();
        }

        return descriptor.getName();
    }
}
