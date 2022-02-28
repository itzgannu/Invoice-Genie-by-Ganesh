package com.invoice.genie;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;

import com.google.android.material.textfield.TextInputLayout;
import com.invoice.genie.databinding.ActivityCartCustomerBinding;
import com.invoice.genie.helpers.RecursiveMethods;
import com.invoice.genie.model.cart.Items;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Cart_Customer extends AppCompatActivity implements View.OnClickListener {

    ActivityCartCustomerBinding binding;

    RecursiveMethods helper;
    private final String TAG = "Cart_Customer Class";
    String current_user; double order_total; List<Items> itemsList = new ArrayList<>();
    Context context; Activity activity;

    TextInputLayout name_layout, email_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.binding = ActivityCartCustomerBinding.inflate(getLayoutInflater());
        setContentView(this.binding.getRoot());

        helper = new RecursiveMethods(); Intent i = getIntent();
        current_user = helper.getCurrentUser(this);
        context = this; activity = this;

        order_total = i.getDoubleExtra("order_total",0);
        itemsList = (List<Items>) i.getSerializableExtra("items_list");

        name_layout = this.binding.cartCustomerNameLayout;
        email_layout = this.binding.cartCustomerEmailLayout;

        this.binding.cartCustomerBack.setOnClickListener(this);
        this.binding.cartCustomerPlaceOrderButton.setOnClickListener(this);
    }

    private boolean validateEmailField() {
        email_layout = this.binding.cartCustomerEmailLayout;
        String email = Objects.requireNonNull(email_layout.getEditText()).getText().toString();
        if (email.isEmpty()) {
            helper.logD(TAG, "Email field is empty ");
            email_layout.setError("Email field is empty");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            email_layout.setError(getString(R.string.enter_valid_email));
            return false;
        } else {
            email_layout.setError(null);
            return true;
        }
    }

    private boolean validateNameField() {
        name_layout = this.binding.cartCustomerNameLayout;
        String name_text = Objects.requireNonNull(name_layout.getEditText()).getText().toString();
        if (name_text.isEmpty()) {
            helper.logD(TAG, "Name field is empty ");
            name_layout.setError("Field is empty");
            return false;
        } else if (name_text.length() > 10) {
            name_layout.setError("Name field should be maximum 10 characters");
            return false;
        } else {
            name_layout.setError(null);
            return true;
        }
    }

    @Override
    public void onClick(View v) {
        if (v != null) {
            int id = v.getId();
            if (id == R.id.cart_customer_back) {
                Intent backIntent = new Intent(this, Cart_Products.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(backIntent);
                finish();
            } else if (id == R.id.cart_customer_place_order_button) {
                if (validateEmailField() && validateNameField()) {
                    //place order
                    helper.logD(TAG, "Now Place Order");

                    String customer_name = Objects.requireNonNull(this.binding.cartCustomerNameLayout.getEditText()).getText().toString();
                    String customer_email = Objects.requireNonNull(this.binding.cartCustomerEmailLayout.getEditText()).getText().toString();

                    Intent placeOrder = new Intent(this, Cart_PlaceHolder.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    placeOrder.putExtra("customer_name", customer_name);
                    placeOrder.putExtra("customer_email", customer_email);
                    placeOrder.putExtra("items_list", (Serializable) this.itemsList);
                    placeOrder.putExtra("order_total", order_total);
                    startActivity(placeOrder);
                    finish();
                }
            }
        }
    }
}