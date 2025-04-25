package com.example.agritechapp;

public class User {
    public String name, email, username, phone;

    public User() {} // required for Firebase

    public User(String name, String email, String username, String phone) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.phone = phone;
    }
}

