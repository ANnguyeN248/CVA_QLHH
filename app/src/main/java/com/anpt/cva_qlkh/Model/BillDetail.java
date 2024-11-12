package com.anpt.cva_qlkh.Model;

public class BillDetail {
    private String id;
    private String billId;
    private String idProduct;
    private int quantity;
    private double price;
    private String imageProduct;
    private String nameProduct;
    private String createdDate;

    public BillDetail(String id, String billId, String idProduct, int quantity, double price, String imageProduct, String nameProduct, String createdDate) {
        this.id = id;
        this.billId = billId;
        this.idProduct = idProduct;
        this.quantity = quantity;
        this.price = price;
        this.imageProduct = imageProduct;
        this.nameProduct = nameProduct;
        this.createdDate = createdDate;
    }

    public BillDetail() {
    }

    public String getId() {
        return id;
    }

    public BillDetail setId(String id) {
        this.id = id;
        return this;
    }

    public String getBillId() {
        return billId;
    }

    public BillDetail setBillId(String billId) {
        this.billId = billId;
        return this;
    }

    public String getIdProduct() {
        return idProduct;
    }

    public BillDetail setIdProduct(String idProduct) {
        this.idProduct = idProduct;
        return this;
    }

    public int getQuantity() {
        return quantity;
    }

    public BillDetail setQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    public double getPrice() {
        return price;
    }

    public BillDetail setPrice(double price) {
        this.price = price;
        return this;
    }

    public String getImageProduct() {
        return imageProduct;
    }

    public BillDetail setImageProduct(String imageProduct) {
        this.imageProduct = imageProduct;
        return this;
    }

    public String getNameProduct() {
        return nameProduct;
    }

    public BillDetail setNameProduct(String nameProduct) {
        this.nameProduct = nameProduct;
        return this;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public BillDetail setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
        return this;
    }
}
