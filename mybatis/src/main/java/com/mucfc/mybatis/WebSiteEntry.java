package com.mucfc.mybatis;

import com.mucfc.AnnotationFieldMapping;
import com.mucfc.BeanUtils;
import com.mucfc.CopyField;

import java.util.Date;

public class WebSiteEntry {
    public static void main(String[] args) {
        WebSite webSite = new WebSite();
        webSite.setId(4L);
        webSite.setClazz("test");
        webSite.setCreateTime(new Date());
        webSite.setDesc("desc");
        webSite.setName("kafka");
        webSite.setUpdateTime(new Date());
        webSite.setUrl("http://www.baidu.com");

        WebSiteEntry entry = new WebSiteEntry();
        BeanUtils.copyPropertiesWithMapping(webSite, entry, new AnnotationFieldMapping());
        System.out.println(entry);
    }

    private long id;
    private String name;
    private String url;
    private String desc;
    @CopyField("name")
    private String clazz;
    private Date createTime;
    private Date updateTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "WebSite{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", desc='" + desc + '\'' +
                ", clazz='" + clazz + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
