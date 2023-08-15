package com.bsh.projectwemeet.entities;

import java.util.Date;
import java.util.Objects;

public class ReviewEntity {

    private int index;
    private String content;
    private Date CreatedAt;
    private int reviewStar;
    private int articleIndex;
    private int reviewIndex;
    private boolean isDeleted;
    private String  clientIp;
    private String clientUa;
    private String email;

    public String getNickname() {
        return nickname;
    }

    public ReviewEntity setNickname(String nickname) {
        this.nickname = nickname;
        return this;
    }

    private String nickname;

    public String getEmail() {
        return email;
    }

    public ReviewEntity setEmail(String email) {
        this.email = email;
        return this;
    }


    public int getReviewStar() {
        return reviewStar;
    }

    public ReviewEntity setReviewStar(int reviewStar) {
        this.reviewStar = reviewStar;
        return this;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReviewEntity that = (ReviewEntity) o;
        return index == that.index;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index);
    }

    public int getIndex() {
        return index;
    }

    public ReviewEntity setIndex(int index) {
        this.index = index;
        return this;
    }

    public String getContent() {
        return content;
    }

    public ReviewEntity setContent(String content) {
        this.content = content;
        return this;
    }

    public Date getCreatedAt() {
        return CreatedAt;
    }

    public ReviewEntity setCreatedAt(Date createdAt) {
        CreatedAt = createdAt;
        return this;
    }



    public int getArticleIndex() {
        return articleIndex;
    }

    public ReviewEntity setArticleIndex(int articleIndex) {
        this.articleIndex = articleIndex;
        return this;
    }

    public int getReviewIndex() {
        return reviewIndex;
    }

    public ReviewEntity setReviewIndex(int reviewIndex) {
        this.reviewIndex = reviewIndex;
        return this;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public ReviewEntity setDeleted(boolean deleted) {
        isDeleted = deleted;
        return this;
    }

    public String getClientIp() {
        return clientIp;
    }

    public ReviewEntity setClientIp(String clientIp) {
        this.clientIp = clientIp;
        return this;
    }

    public String getClientUa() {
        return clientUa;
    }

    public ReviewEntity setClientUa(String clientUa) {
        this.clientUa = clientUa;
        return this;
    }




}
