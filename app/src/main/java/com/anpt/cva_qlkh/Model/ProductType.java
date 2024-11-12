package com.anpt.cva_qlkh.Model;

import java.io.Serializable;
import java.util.HashMap;

public class ProductType implements Serializable {
    String id;
    String name;
    String photo;

    public ProductType() {
    }

    public ProductType(String id, String name, String photo) {
        this.id = id;
        this.name = name;
        this.photo = photo;
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

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
    public HashMap<String,Object> converHashMap(){
        HashMap<String,Object> pt = new HashMap<>();
        pt.put("name",name);
        pt.put("photo",photo);
        return pt;
    }
    public HashMap<String,Object> converHashMap2(){
        HashMap<String,Object> pt = new HashMap<>();
        pt.put("name",name);
        return pt;
    }
}
