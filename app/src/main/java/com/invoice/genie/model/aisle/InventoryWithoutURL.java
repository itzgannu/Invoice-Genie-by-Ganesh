package com.invoice.genie.model.aisle;

import java.io.Serializable;

import androidx.annotation.NonNull;

public class InventoryWithoutURL implements Serializable {
    String description;
    String name;
    Double price;
    int quantity;
    String pack;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getPack() {
        return pack;
    }

    public void setPack(String pack) {
        this.pack = pack;
    }

    public InventoryWithoutURL() {
    }

    public InventoryWithoutURL(String description, String name, Double price, int quantity, String pack) {
        this.description = description;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.pack = pack;
    }

    @NonNull
    @Override
    public String toString() {
        return "InventoryWithoutURL{" +
                "description='" + description + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", package='" + pack + '\'' +
                '}';
    }
}