package com.anpt.cva_qlkh.Model;

import java.io.Serializable;

public class Bill implements Serializable {

    private String id;
    private  int status;
    private  String createdByUser;
    private  String createdDate;
    private  String note;
    private  double totalPrice;

    public Bill() {
    }

    public Bill(String id, int status, String createdByUser, String createdDate, String note, double totalPrice) {
        this.id = id;
        this.status = status;
        this.createdByUser = createdByUser;
        this.createdDate = createdDate;
        this.note = note;
        this.totalPrice = totalPrice;
    }

    public String getId() {
        return id;
    }

    public Bill setId(String id) {
        this.id = id;
        return this;
    }

    public int getStatus() {
        return status;
    }

    public Bill setStatus(int status) {
        this.status = status;
        return this;
    }

    public String getCreatedByUser() {
        return createdByUser;
    }

    public Bill setCreatedByUser(String createdByUser) {
        this.createdByUser = createdByUser;
        return this;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public Bill setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public String getNote() {
        return note;
    }

    public Bill setNote(String note) {
        this.note = note;
        return this;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public Bill setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
        return this;
    }
}
