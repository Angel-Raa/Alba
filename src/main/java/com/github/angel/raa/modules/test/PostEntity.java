package com.github.angel.raa.modules.test;


import java.io.Serial;
import java.io.Serializable;

public class PostEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = -7034261873L;

    private Integer postId;
    private String title;
    private String body;

    public PostEntity() {
    }

    public PostEntity(Integer postId, String title, String body) {
        this.postId = postId;
        this.title = title;
        this.body = body;
    }

    public Integer getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "Post{" +
                "postId=" + postId +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                '}';
    }
}
