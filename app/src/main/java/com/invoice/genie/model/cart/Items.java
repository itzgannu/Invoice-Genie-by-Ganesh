package com.invoice.genie.model.cart;

import java.io.Serializable;

public class Items implements Serializable {
    String product_name, unit_type, url;
    int product_quantity;
    double product_price, product_total_price;

    public Items() {
    }

    public Items(String product_name, String unit_type, String url, int product_quantity, double product_price, double product_total_price) {
        this.product_name = product_name;
        this.unit_type = unit_type;
        this.url = url;
        this.product_quantity = product_quantity;
        this.product_price = product_price;
        this.product_total_price = product_total_price;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getUnit_type() {
        return unit_type;
    }

    public void setUnit_type(String unit_type) {
        this.unit_type = unit_type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getProduct_quantity() {
        return product_quantity;
    }

    public void setProduct_quantity(int product_quantity) {
        this.product_quantity = product_quantity;
    }

    public double getProduct_price() {
        return product_price;
    }

    public void setProduct_price(double product_price) {
        this.product_price = product_price;
    }

    public double getProduct_total_price() {
        return product_total_price;
    }

    public void setProduct_total_price(double product_total_price) {
        this.product_total_price = product_total_price;
    }

    @Override
    public String toString() {
        return "Items{" +
                "product_name='" + product_name + '\'' +
                ", unit_type='" + unit_type + '\'' +
                ", url='" + url + '\'' +
                ", product_quantity=" + product_quantity +
                ", product_price=" + product_price +
                ", product_total_price=" + product_total_price +
                '}';
    }

}
