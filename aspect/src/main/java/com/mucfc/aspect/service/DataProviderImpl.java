package com.mucfc.aspect.service;

import com.mucfc.aspect.User;

import java.beans.Transient;

public class DataProviderImpl implements DataProvider {
    @Transient
    public User getById(long id) {
        User user = new User();
        user.setId(1);
        user.setName("kafka");
        System.out.println("DataProviderImpl.getById(long id)==========================");
        return user;
    }
}
