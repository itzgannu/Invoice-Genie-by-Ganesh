package com.invoice.genie.model.cart;

import java.io.Serializable;
import java.util.List;

public class Invoice implements Serializable {

    String customer_name, customer_email, company_name, company_email, order_date, order_time;
    double order_total;
    List<Items> items;

    public Invoice() {
    }

    public Invoice(String customer_name, String customer_email, String company_name, String company_email, String order_date, String order_time, double order_total, List<Items> items) {
        this.customer_name = customer_name;
        this.customer_email = customer_email;
        this.company_name = company_name;
        this.company_email = company_email;
        this.order_date = order_date;
        this.order_time = order_time;
        this.order_total = order_total;
        this.items = items;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getCustomer_email() {
        return customer_email;
    }

    public void setCustomer_email(String customer_email) {
        this.customer_email = customer_email;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getCompany_email() {
        return company_email;
    }

    public void setCompany_email(String company_email) {
        this.company_email = company_email;
    }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public String getOrder_time() {
        return order_time;
    }

    public void setOrder_time(String order_time) {
        this.order_time = order_time;
    }

    public double getOrder_total() {
        return order_total;
    }

    public void setOrder_total(double order_total) {
        this.order_total = order_total;
    }

    public List<Items> getItems() {
        return items;
    }

    public void setItems(List<Items> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "customer_name='" + customer_name + '\'' +
                ", customer_email='" + customer_email + '\'' +
                ", company_name='" + company_name + '\'' +
                ", company_email='" + company_email + '\'' +
                ", order_date='" + order_date + '\'' +
                ", order_time='" + order_time + '\'' +
                ", order_total=" + order_total +
                ", items=" + items +
                '}';
    }
}
