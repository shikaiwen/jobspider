package com.kevin.entity;

import java.util.Date;

/**
 * Created by kaiwen on 07/03/2017.
 */
public class CsdnComment {

    private String _id;

    private String ArticleId;
    private String BlogId;
    private String CommentId;
    private String Content;
    private String ParentId;
    private String PostTime;
    private String Replies;
    private String UserName;
    private String Userface;

    //是否已经提取用户
    private int version;

    private Date createDate;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getArticleId() {
        return ArticleId;
    }

    public void setArticleId(String articleId) {
        ArticleId = articleId;
    }

    public String getBlogId() {
        return BlogId;
    }

    public void setBlogId(String blogId) {
        BlogId = blogId;
    }

    public String getCommentId() {
        return CommentId;
    }

    public void setCommentId(String commentId) {
        CommentId = commentId;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getParentId() {
        return ParentId;
    }

    public void setParentId(String parentId) {
        ParentId = parentId;
    }

    public String getPostTime() {
        return PostTime;
    }

    public void setPostTime(String postTime) {
        PostTime = postTime;
    }

    public String getReplies() {
        return Replies;
    }

    public void setReplies(String replies) {
        Replies = replies;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUserface() {
        return Userface;
    }

    public void setUserface(String userface) {
        Userface = userface;
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
}
