package com.anpt.cva_qlkh.Model;

public class StockOut {
    private String id;
    private String billID;
    private String imageProduct;
    private  String createdDate;
    private String nameProduct;
    private  double price;
    private int quantity;

    public StockOut() {
    }

    public StockOut(String id, String billID, String imageProduct, String createdDate, String nameProduct, double price, int quantity) {
        this.id = id;
        this.billID = billID;
        this.imageProduct = imageProduct;
        this.createdDate = createdDate;
        this.nameProduct = nameProduct;
        this.price = price;
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBillID() {
        return billID;
    }

    public void setBillID(String billID) {
        this.billID = billID;
    }

    public String getImageProduct() {
        return imageProduct;
    }

    public void setImageProduct(String imageProduct) {
        this.imageProduct = imageProduct;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getNameProduct() {
        return nameProduct;
    }

    public void setNameProduct(String nameProduct) {
        this.nameProduct = nameProduct;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
