package com.example.snitch.snitchapp.models;

public class User {
    public String surname;
    public String name;
    public String phoneNumber;
    public String email;
    public String imageUri;

    public User(String surname, String name,
                String phoneNumber, String email, String imageUri) {
        this.surname = surname;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.imageUri = imageUri;
    }

    public User() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phoneNumber;
    }

    public void setPhone(String phone) {
        phoneNumber = phone;
    }
}
