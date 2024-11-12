package com.anpt.cva_qlkh.Model;

import java.io.Serializable;
import java.util.HashMap;

public class Product implements Serializable {
    private String id;
    private String name;
    private  int quantity;
    private int price;
    private String photo;
    private String Date;
    private String userID;
    private String ProductType;

    public Product() {
    }

    public Product(String id, String name, int quantity, int price, String photo, String date, String userID, String productType) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.photo = photo;
        Date = date;
        this.userID = userID;
        ProductType = productType;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getProductType() {
        return ProductType;
    }

    public void setProductType(String productType) {
        ProductType = productType;
    }

    public HashMap<String, Object> converHashMap(){
        HashMap<String, Object> work = new HashMap<>();
        work.put("id", id);
        work.put("name", name);
        work.put("quantity", quantity);
        work.put("price", price);
        work.put("Date", Date);
        work.put("photo", photo);
        work.put("userId", userID);
        work.put("productType", ProductType);
        return work;
    }
}
