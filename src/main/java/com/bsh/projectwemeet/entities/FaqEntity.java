package com.bsh.projectwemeet.entities;

import java.util.Date;
import java.util.Objects;

public class FaqEntity {
    private int index;
    private String title;
    private String content;
    private Date createdAt;
    private String clientIp;
    private String clientUa;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FaqEntity faqEntity = (FaqEntity) o;
        return index == faqEntity.index;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index);
    }

    public int getIndex() {
        return index;
    }

    public FaqEntity setIndex(int index) {
        this.index = index;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public FaqEntity setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getContent() {
        return content;
    }

    public FaqEntity setContent(String content) {
        this.content = content;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public FaqEntity setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public String getClientIp() {
        return clientIp;
    }

    public FaqEntity setClientIp(String clientIp) {
        this.clientIp = clientIp;
        return this;
    }

    public String getClientUa() {
        return clientUa;
    }

    public FaqEntity setClientUa(String clientUa) {
        this.clientUa = clientUa;
        return this;
    }
}
