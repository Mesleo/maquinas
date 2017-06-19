package com.javifocus2009gmail.ubication;

/**
 * Created by jbenitez on 18/10/16.
 */

public class User {

    private String id;
    private String name;
    private String email;
    private String password;

    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public User(String id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
