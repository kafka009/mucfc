package com.mucfc.mybatis;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * SQL_NO_CACHE
 */
@Mapper
public interface UserMapper {
    @Select("SELECT * FROM web_site WHERE name = #{name}")
    WebSite findByName(@Param("name") String name);

    @Select("SELECT * FROM web_site WHERE id = #{id}")
    WebSite findById(@Param("id") long id);
}
