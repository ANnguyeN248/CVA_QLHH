package com.anpt.cva_qlkh.Model;

public class Cart {
    private String id;
    private String idProduct;
    private String userId;
    private String imageProduct;
    private String nameProduct;
    private double priceProduct;
    private int quantity;
    private boolean checked;

    public Cart() {
    }

    public Cart(String id, String idProduct, String usernameUser, String imageProduct, String nameProduct, double priceProduct, int quantity, boolean checked) {
        this.id = id;
        this.idProduct = idProduct;
        this.userId = usernameUser;
        this.imageProduct = imageProduct;
        this.nameProduct = nameProduct;
        this.priceProduct = priceProduct;
        this.quantity = quantity;
        this.checked = checked;
    }

    public String getId() {
        return id;
    }

    public Cart setId(String id) {
        this.id = id;
        return this;
    }

    public String getIdProduct() {
        return idProduct;
    }

    public Cart setIdProduct(String idProduct) {
        this.idProduct = idProduct;
        return this;
    }

    public String getUsernameUser() {
        return userId;
    }

    public Cart setUsernameUser(String usernameUser) {
        this.userId = usernameUser;
        return this;
    }

    public String getImageProduct() {
        return imageProduct;
    }

    public Cart setImageProduct(String imageProduct) {
        this.imageProduct = imageProduct;
        return this;
    }

    public String getNameProduct() {
        return nameProduct;
    }

    public Cart setNameProduct(String nameProduct) {
        this.nameProduct = nameProduct;
        return this;
    }

    public double getPriceProduct() {
        return priceProduct;
    }

    public Cart setPriceProduct(double priceProduct) {
        this.priceProduct = priceProduct;
        return this;
    }

    public int getQuantity() {
        return quantity;
    }

    public Cart setQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    public boolean isChecked() {
        return checked;
    }

    public Cart setChecked(boolean checked) {
        this.checked = checked;
        return this;
    }
}
