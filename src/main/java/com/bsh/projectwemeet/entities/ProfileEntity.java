package com.bsh.projectwemeet.entities;

import java.util.Date;
import java.util.Objects;

public class ProfileEntity {
    private int index;
    private String email;
    private byte[] profileThumbnail;
    private String profileThumbnailMime;
    private Date createdAt;
    private String introduceText;
    private String nickname;

    public String getNickname() {
        return nickname;
    }

    public ProfileEntity setNickname(String nickname) {
        this.nickname = nickname;
        return this;
    }

    public String getIntroduceText() {
        return introduceText;
    }

    public ProfileEntity setIntroduceText(String introduceText) {
        this.introduceText = introduceText;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProfileEntity profile = (ProfileEntity) o;
        return index == profile.index;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index);
    }

    public int getIndex() {
        return index;
    }

    public ProfileEntity setIndex(int index) {
        this.index = index;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public ProfileEntity setEmail(String email) {
        this.email = email;
        return this;
    }

    public byte[] getProfileThumbnail() {
        return profileThumbnail;
    }

    public ProfileEntity setProfileThumbnail(byte[] profileThumbnail) {
        this.profileThumbnail = profileThumbnail;
        return this;
    }

    public String getProfileThumbnailMime() {
        return profileThumbnailMime;
    }

    public ProfileEntity setProfileThumbnailMime(String profileThumbnailMime) {
        this.profileThumbnailMime = profileThumbnailMime;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public ProfileEntity setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }
}
