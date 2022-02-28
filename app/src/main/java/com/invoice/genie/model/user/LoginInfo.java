package com.invoice.genie.model.user;

import java.io.Serializable;

public class LoginInfo implements Serializable {
    private String email;
    private String first_name;
    private String last_name;
    private String seller;
    private String company;
    private int image;

    public LoginInfo() {
    }

    public LoginInfo(String email, String first_name, String last_name, String seller, String company, int image) {
        this.email = email;
        this.first_name = first_name;
        this.last_name = last_name;
        this.seller = seller;
        this.company = company;
        this.image = image;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "LoginInfo{" +
                "email='" + email + '\'' +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", seller='" + seller + '\'' +
                ", company='" + company + '\'' +
                ", image=" + image +
                '}';
    }
}
