package com.anpt.cva_qlkh.Model;

import java.util.HashMap;

public class Admin {
    private String username;
    private String password;
    private String avatar;

    public Admin(String username, String password,String avatar) {
        this.username = username;
        this.password = password;
        this.avatar = avatar;
    }

    public Admin(String avatar) {
        this.avatar = avatar;
    }

    public String getAvatar() {
        return avatar;
    }

    public Admin setAvatar(String avatar) {
        this.avatar = avatar;
        return this;
    }

    public Admin() {
    }

    public String getUsername() {
        return username;
    }

    public Admin setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public Admin setPassword(String password) {
        this.password = password;
        return this;
    }
    public HashMap<String,Object> convertHashMap(){
        HashMap<String,Object> admin = new HashMap<>();
        admin.put("username",username);
        admin.put("password",password);
        admin.put("avatar",avatar);
        return admin;
    }
    public HashMap<String,Object> convertHashMap2(){
        HashMap<String,Object> admin = new HashMap<>();
        admin.put("avatar",avatar);
        return admin;
    }
}
