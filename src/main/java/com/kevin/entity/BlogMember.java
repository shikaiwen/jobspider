package com.kevin.entity;

/**
 * Created by kaiwen on 01/03/2017.
 */
public class BlogMember {

    public static final String CSDN_ICON_DOMAIN = "http://avatar.csdn.net/";
    public static final String CSDN_BLOG_DOMAIN = "http://blog.csdn.net/";


    private String _id;
    private String username;
    private String address;
    private String iconPath;

    private Integer articleCnt;
    private Integer viewCnt;
    private String blogUrl;

    // 1:csdn
    private String source;


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
}
