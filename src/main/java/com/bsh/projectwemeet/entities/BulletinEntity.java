package com.bsh.projectwemeet.entities;

import java.util.Date;
import java.util.Objects;

public class BulletinEntity {

    private int index;
    private int articleIndex;
    private int commentIndex;
    private String content;
    private String email;
    private Date createdAt;
    private String clientIp;
    private String clientUa;

    private  boolean isDeleted;

    public boolean isDeleted() {
        return isDeleted;
    }

    public BulletinEntity setDeleted(boolean deleted) {
        isDeleted = deleted;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BulletinEntity that = (BulletinEntity) o;
        return index == that.index;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index);
    }

    public int getIndex() {
        return index;
    }

    public BulletinEntity setIndex(int index) {
        this.index = index;
        return this;
    }

    public int getArticleIndex() {
        return articleIndex;
    }

    public BulletinEntity setArticleIndex(int articleIndex) {
        this.articleIndex = articleIndex;
        return this;
    }

    public int getCommentIndex() {
        return commentIndex;
    }

    public BulletinEntity setCommentIndex(int commentIndex) {
        this.commentIndex = commentIndex;
        return this;
    }

    public String getContent() {
        return content;
    }

    public BulletinEntity setContent(String content) {
        this.content = content;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public BulletinEntity setEmail(String email) {
        this.email = email;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public BulletinEntity setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public String getClientIp() {
        return clientIp;
    }

    public BulletinEntity setClientIp(String clientIp) {
        this.clientIp = clientIp;
        return this;
    }

    public String getClientUa() {
        return clientUa;
    }

    public BulletinEntity setClientUa(String clientUa) {
        this.clientUa = clientUa;
        return this;
    }
}
