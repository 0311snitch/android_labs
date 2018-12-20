package com.example.snitch.snitchapp.models;

public class User {
    public int ID;
    public String surname;
    public String userame;
    public String phoneNumber;
    public String email;
    public String imageUri;

    public User(int ID, String surname, String username,
                String phoneNumber, String email, String imageUri) {
        this.ID = ID;
        this.surname = surname;
        this.userame = username;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.imageUri = imageUri;
    }

    public User() {

    }

}
