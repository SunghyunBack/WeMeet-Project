package com.bsh.projectwemeet.entities;

import java.util.Date;
import java.util.Objects;

public class EventImagesEntity {
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
        EventImagesEntity that = (EventImagesEntity) o;
        return index == that.index;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index);
    }

    public int getIndex() {
        return index;
    }

    public EventImagesEntity setIndex(int index) {
        this.index = index;
        return this;
    }

    public int getArticleIndex() {
        return articleIndex;
    }

    public EventImagesEntity setArticleIndex(int articleIndex) {
        this.articleIndex = articleIndex;
        return this;
    }

    public String getName() {
        return name;
    }

    public EventImagesEntity setName(String name) {
        this.name = name;
        return this;
    }

    public long getSize() {
        return size;
    }

    public EventImagesEntity setSize(long size) {
        this.size = size;
        return this;
    }

    public String getContentType() {
        return contentType;
    }

    public EventImagesEntity setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public byte[] getData() {
        return data;
    }

    public EventImagesEntity setData(byte[] data) {
        this.data = data;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public EventImagesEntity setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public String getClientIp() {
        return clientIp;
    }

    public EventImagesEntity setClientIp(String clientIp) {
        this.clientIp = clientIp;
        return this;
    }

    public String getClientUa() {
        return clientUa;
    }

    public EventImagesEntity setClientUa(String clientUa) {
        this.clientUa = clientUa;
        return this;
    }
}
