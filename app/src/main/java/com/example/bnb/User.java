package com.example.bnb;

import java.io.Serializable;
import java.util.UUID;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String username;
    private String password;
    private boolean isManager;

    public User(String username, String password, boolean isManager) {
        this.id = UUID.randomUUID().toString();
        this.username = username;
        this.password = password;
        this.isManager = isManager;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isManager() {
        return isManager;
    }

    public void setId(String id){
        this.id = id;
    }
}
