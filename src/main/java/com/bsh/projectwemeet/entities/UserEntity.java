package com.bsh.projectwemeet.entities;

import java.util.Date;
import java.util.Objects;

public class UserEntity {
    private String email;
    private String name;
    private String nickname;
    private String contact;

    private boolean isAdmin;
    private Date birth;
    private String registeredAt;
    private String addressSecondary;
    private String gender;
    private String password;
    private String addressPostal;
    private String addressPrimary;


    public String getPassword() {
        return password;
    }

    public UserEntity setPassword(String password) {
        this.password = password;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }


    public String getEmail() {
        return email;
    }

    public UserEntity setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getName() {
        return name;
    }

    public UserEntity setName(String name) {
        this.name = name;
        return this;
    }

    public String getNickname() {
        return nickname;
    }

    public UserEntity setNickname(String nickname) {
        this.nickname = nickname;
        return this;
    }

    public String getContact() {
        return contact;
    }

    public UserEntity setContact(String contact) {
        this.contact = contact;
        return this;
    }


    public boolean isAdmin() {
        return isAdmin;
    }

    public UserEntity setAdmin(boolean admin) {
        isAdmin = admin;
        return this;
    }

    public Date getBirth() {
        return birth;
    }

    public UserEntity setBirth(Date birth) {
        this.birth = birth;
        return this;
    }

    public String getRegisteredAt() {
        return registeredAt;
    }

    public UserEntity setRegisteredAt(String registeredAt) {
        this.registeredAt = registeredAt;
        return this;
    }

    public String getAddressPostal() {
        return addressPostal;
    }

    public UserEntity setAddressPostal(String addressPostal) {
        this.addressPostal = addressPostal;
        return this;
    }

    public String getAddressPrimary() {
        return addressPrimary;
    }

    public UserEntity setAddressPrimary(String addressPrimary) {
        this.addressPrimary = addressPrimary;
        return this;
    }

    public String getAddressSecondary() {
        return addressSecondary;
    }

    public UserEntity setAddressSecondary(String addressSecondary) {
        this.addressSecondary = addressSecondary;
        return this;
    }

    public String getGender() {
        return gender;
    }

    public UserEntity setGender(String gender) {
        this.gender = gender;
        return this;
    }


}
