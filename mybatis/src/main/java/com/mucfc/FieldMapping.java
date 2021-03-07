package com.mucfc;

import java.beans.PropertyDescriptor;

public interface FieldMapping {
    String mapping(PropertyDescriptor descriptor);
}
