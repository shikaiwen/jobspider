package com.kevin.entity;

import java.util.Date;

/**
 * Created by kaiwen on 01/03/2017.
 */
public class BlogMember {

    public static final String CSDN_ICON_DOMAIN = "http://avatar.csdn.net/";
//    public static final String CSDN_BLOG_DOMAIN = "http://blog.csdn.net/";


    private String _id;
    private String username;
    private String address;
    private String iconPath;

    private Integer articleCnt;
    private Integer viewCnt;
    private String blogUrl;

    // 1:csdn
    private String source;

    // 1:评论爬取
    private String fetchType;
    /**
     * 版本号 新入库的version为0,随着更新爬取的次数增加而增加
     */
    private int version;
    private Date createDate;
    private Date updateDate;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public Integer getArticleCnt() {
        return articleCnt;
    }

    public void setArticleCnt(Integer articleCnt) {
        this.articleCnt = articleCnt;
    }

    public Integer getViewCnt() {
        return viewCnt;
    }

    public void setViewCnt(Integer viewCnt) {
        this.viewCnt = viewCnt;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getBlogUrl() {
        return blogUrl;
    }

    public void setBlogUrl(String blogUrl) {
        this.blogUrl = blogUrl;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getFetchType() {
        return fetchType;
    }

    public void setFetchType(String fetchType) {
        this.fetchType = fetchType;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}
