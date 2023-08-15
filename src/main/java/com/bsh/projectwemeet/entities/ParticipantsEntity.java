package com.bsh.projectwemeet.entities;

import java.util.Date;
import java.util.Objects;

public class ParticipantsEntity {
    private int index;
    private String email;
    private int ArticleIndex;
    private Date createdAt;
    private boolean checkParticipationStatus;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParticipantsEntity that = (ParticipantsEntity) o;
        return index == that.index;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index);
    }

    public int getIndex() {
        return index;
    }

    public ParticipantsEntity setIndex(int index) {
        this.index = index;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public ParticipantsEntity setEmail(String email) {
        this.email = email;
        return this;
    }

    public int getArticleIndex() {
        return ArticleIndex;
    }

    public ParticipantsEntity setArticleIndex(int articleIndex) {
        ArticleIndex = articleIndex;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public ParticipantsEntity setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public boolean isCheckParticipationStatus() {
        return checkParticipationStatus;
    }

    public ParticipantsEntity setCheckParticipationStatus(boolean checkParticipationStatus) {
        this.checkParticipationStatus = checkParticipationStatus;
        return this;
    }
}
