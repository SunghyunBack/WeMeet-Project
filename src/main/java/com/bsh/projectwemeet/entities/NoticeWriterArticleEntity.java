package com.bsh.projectwemeet.entities;

import java.util.Date;
import java.util.Objects;

public class NoticeWriterArticleEntity {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NoticeWriterArticleEntity that = (NoticeWriterArticleEntity) o;
        return index == that.index;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index);
    }

    public int getIndex() {
        return index;
    }

    public NoticeWriterArticleEntity setIndex(int index) {
        this.index = index;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public NoticeWriterArticleEntity setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getContent() {
        return content;
    }

    public NoticeWriterArticleEntity setContent(String content) {
        this.content = content;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public NoticeWriterArticleEntity setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public String getClientIp() {
        return clientIp;
    }

    public NoticeWriterArticleEntity setClientIp(String clientIp) {
        this.clientIp = clientIp;
        return this;
    }

    public String getClientUa() {
        return clientUa;
    }

    public NoticeWriterArticleEntity setClientUa(String clientUa) {
        this.clientUa = clientUa;
        return this;
    }

    private int index;
    private String title;
    private String content;
    private Date createdAt;
    private String clientIp;
    private String clientUa;
}
