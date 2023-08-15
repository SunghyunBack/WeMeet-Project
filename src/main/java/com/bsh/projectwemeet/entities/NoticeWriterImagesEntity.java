package com.bsh.projectwemeet.entities;

import java.util.Date;
import java.util.Objects;

public class NoticeWriterImagesEntity {
    private int index;
    private int articleIndex;
    private String name;
    private long size;
    private String contentType;
    private byte[] data;
    private Date createdAt;
    private String clientIp;
    private String clientUa;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NoticeWriterImagesEntity that = (NoticeWriterImagesEntity) o;
        return index == that.index;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index);
    }

    public int getIndex() {
        return index;
    }

    public NoticeWriterImagesEntity setIndex(int index) {
        this.index = index;
        return this;
    }

    public int getArticleIndex() {
        return articleIndex;
    }

    public NoticeWriterImagesEntity setArticleIndex(int articleIndex) {
        this.articleIndex = articleIndex;
        return this;
    }

    public String getName() {
        return name;
    }

    public NoticeWriterImagesEntity setName(String name) {
        this.name = name;
        return this;
    }

    public long getSize() {
        return size;
    }

    public NoticeWriterImagesEntity setSize(long size) {
        this.size = size;
        return this;
    }

    public String getContentType() {
        return contentType;
    }

    public NoticeWriterImagesEntity setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public byte[] getData() {
        return data;
    }

    public NoticeWriterImagesEntity setData(byte[] data) {
        this.data = data;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public NoticeWriterImagesEntity setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public String getClientIp() {
        return clientIp;
    }

    public NoticeWriterImagesEntity setClientIp(String clientIp) {
        this.clientIp = clientIp;
        return this;
    }

    public String getClientUa() {
        return clientUa;
    }

    public NoticeWriterImagesEntity setClientUa(String clientUa) {
        this.clientUa = clientUa;
        return this;
    }
}
