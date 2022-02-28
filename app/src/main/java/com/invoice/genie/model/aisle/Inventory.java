package com.invoice.genie.model.aisle;

import java.io.Serializable;

public class Inventory implements Serializable {
    String name;
    String description;
    String pack;
    Double price;
    int quantity;
    String url;

    public Inventory() {
    }

    public Inventory(String name, String description, String pack, Double price, int quantity, String url) {
        this.name = name;
        this.description = description;
        this.pack = pack;
        this.price = price;
        this.quantity = quantity;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPack() {
        return pack;
    }

    public void setPack(String pack) {
        this.pack = pack;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Inventory{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", pack='" + pack + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", url=" + url +
                '}';
    }
}
