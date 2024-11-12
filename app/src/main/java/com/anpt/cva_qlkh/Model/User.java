package com.anpt.cva_qlkh.Model;

import java.io.Serializable;
import java.util.HashMap;

public class User implements Serializable {
    private String id;

    private String username;
    private String password;
    private String numberphone;
    private int position;
    private String avatar;
    private String profile;
    private String lastLogin;
    private String createDate;
    private String lastAction;

    public User() {
    }

    public User(String id,String username, String password, String numberphone, int position, String avatar, String profile, String createDate) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.numberphone = numberphone;
        this.position = position;
        this.avatar = avatar;
        this.profile = profile;
        this.createDate = createDate;
    }
    public String getUsername() {
        return username;
    }

    public String getId() {
        return id;
    }

    public User setId(String id) {
        this.id = id;
        return this;
    }

    public User setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getNumberphone() {
        return numberphone;
    }

    public User setNumberphone(String numberphone) {
        this.numberphone = numberphone;
        return this;
    }

    public int getPosition() {
        return position;
    }

    public User setPosition(int position) {
        this.position = position;
        return this;
    }

    public String getAvatar() {
        return avatar;
    }

    public User setAvatar(String avatar) {
        this.avatar = avatar;
        return this;
    }

    public String getProfile() {
        return profile;
    }

    public User setProfile(String profile) {
        this.profile = profile;
        return this;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public User setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
        return this;
    }

    public String getCreateDate() {
        return createDate;
    }

    public User setCreateDate(String createDate) {
        this.createDate = createDate;
        return this;
    }

    public String getLastAction() {
        return lastAction;
    }

    public User setLastAction(String lastAction) {
        this.lastAction = lastAction;
        return this;
    }

    public HashMap<String,Object> convertHashMap(){
        HashMap<String,Object> user = new HashMap<>();
        user.put("id", id);
        user.put("username", username);
        user.put("password", password);
        user.put("numberphone", numberphone);
        user.put("position", position);
        user.put("avatar",avatar);
        user.put("profile",profile);
        user.put("lastLogin",lastLogin);
        user.put("createDate",createDate);
        user.put("lastAction",lastAction);
        return user;
    }
    public HashMap<String,Object> updatetHashMap(){
        HashMap<String,Object> user = new HashMap<>();
        user.put("username", username);
        user.put("password", password);
        user.put("numberphone", numberphone);
        user.put("position", position);
        user.put("avatar",avatar);
        user.put("profile",profile);
        user.put("lastLogin",lastLogin);
        user.put("createDate",createDate);
        user.put("lastAction",lastAction);
        return user;
    }
}
